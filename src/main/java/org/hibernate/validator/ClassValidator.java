//$Id: ClassValidator.java,v 1.1 2006/04/17 12:11:07 daniel_wiese Exp $
package org.hibernate.validator;

import java.beans.Introspector;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.AssertionFailure;
import org.hibernate.Hibernate;
import org.hibernate.MappingException;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.reflection.Filter;
import org.hibernate.reflection.ReflectionManager;
import org.hibernate.reflection.XClass;
import org.hibernate.reflection.XMember;
import org.hibernate.reflection.XMethod;
import org.hibernate.reflection.XProperty;
import org.hibernate.util.IdentitySet;


/**
 * Engine that take a bean and check every expressed annotation restrictions
 *
 * @author Gavin King
 * @author Emmanuel Bernard
 */
public class ClassValidator<T> implements Serializable {
	//TODO Define magic number
	private static Log log = LogFactory.getLog( ClassValidator.class );
	private static final InvalidValue[] EMPTY_INVALID_VALUE_ARRAY = new InvalidValue[]{};
	private static final String DEFAULT_VALIDATOR_MESSAGE = "org.hibernate.validator.resources.DefaultValidatorMessages";
	private static final String VALIDATOR_MESSAGE = "ValidatorMessages";
	private static final Set<Class> INDEXABLE_CLASS = new HashSet<Class>();

	static {
		INDEXABLE_CLASS.add( Integer.class );
		INDEXABLE_CLASS.add( Long.class );
		INDEXABLE_CLASS.add( String.class );
	}

	private final Class<T> beanClass;
	private transient ResourceBundle messageBundle;
	private transient boolean defaultResourceBundle;

	private final transient Map<XClass, ClassValidator> childClassValidators;
	private transient List<Validator> beanValidators;
	private transient List<Validator> memberValidators;
	private transient List<XMember> memberGetters;
	private transient Map<Validator, String> messages;
	private transient List<XMember> childGetters;

	/**
	 * create the validator engine for this bean type
	 */
	public ClassValidator(Class<T> beanClass) {
		this( beanClass, null );
	}

	/**
	 * create the validator engine for a particular bean class, using a resource bundle
	 * for message rendering on violation
	 */
	public ClassValidator(Class<T> beanClass, ResourceBundle resourceBundle) {
		this( beanClass, resourceBundle, new HashMap<XClass, ClassValidator>() );
	}

	protected ClassValidator(
			Class<T> beanClass, ResourceBundle resourceBundle, Map<XClass, ClassValidator> childClassValidators
	) {
		this( ReflectionManager.INSTANCE.toXClass( beanClass ), resourceBundle, childClassValidators );
	}

	@SuppressWarnings("unchecked")
	protected ClassValidator(
			XClass beanClass, ResourceBundle resourceBundle, Map<XClass, ClassValidator> childClassValidators
	) {
		this.beanClass = ReflectionManager.INSTANCE.toClass( beanClass );
		this.messageBundle = resourceBundle == null ?
				getDefaultResourceBundle() :
				resourceBundle;
		this.childClassValidators = childClassValidators;
		initValidator( beanClass, childClassValidators, this.messageBundle );
	}

	private ResourceBundle getDefaultResourceBundle() {
		ResourceBundle rb;
		try {
			//use context class loader as a first citizen
			ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
			if ( contextClassLoader == null ) {
				throw new MissingResourceException( "No context classloader", null, VALIDATOR_MESSAGE );
			}
			rb = ResourceBundle.getBundle(
					VALIDATOR_MESSAGE,
					Locale.getDefault(),
					contextClassLoader
			);
		}
		catch (MissingResourceException e) {
			log.trace( "ResourceBundle " + VALIDATOR_MESSAGE + " not found in thread context classloader" );
			//then use the Validator Framework classloader
			try {
				rb = ResourceBundle.getBundle(
						VALIDATOR_MESSAGE,
						Locale.getDefault(),
						this.getClass().getClassLoader()
				);
			}
			catch (MissingResourceException ee) {
				log.debug(
						"ResourceBundle ValidatorMessages not found in Validator classloader. Delegate to " + DEFAULT_VALIDATOR_MESSAGE
				);
				//the user did not override the default ValidatorMessages
				rb = ResourceBundle.getBundle( DEFAULT_VALIDATOR_MESSAGE );
			}
		}
		defaultResourceBundle = true;
		return rb;
	}

	private void initValidator(
			XClass xClass, Map<XClass, ClassValidator> childClassValidators,
			ResourceBundle resourceBundle
	) {
		beanValidators = new ArrayList<Validator>();
		memberValidators = new ArrayList<Validator>();
		memberGetters = new ArrayList<XMember>();
		messages = new HashMap<Validator, String>();
		childGetters = new ArrayList<XMember>();

		//build the class hierarchy to look for members in
		childClassValidators.put( xClass, this );
		Collection<XClass> classes = new HashSet<XClass>();
		addSuperClassesAndInterfaces( xClass, classes );
		for ( XClass currentClass : classes ) {
			Annotation[] classAnnotations = currentClass.getAnnotations();
			for ( int i = 0; i < classAnnotations.length ; i++ ) {
				Annotation classAnnotation = classAnnotations[i];
				Validator beanValidator = createValidator( classAnnotation );
				if ( beanValidator != null ) beanValidators.add( beanValidator );
			}
		}

		//Check on all selected classes
		for ( XClass currClass : classes ) {
			List<XMethod> methods = currClass.getDeclaredMethods();
			for ( XMethod method : methods ) {
				createMemberValidator( method );
				createChildValidator( resourceBundle, method );
			}

			List<XProperty> fields = currClass.getDeclaredProperties(
					"field", new Filter() {
				public boolean returnStatic() {
					return true;
				}

				public boolean returnTransient() {
					return true;
				}
			}
			);
			for ( XProperty field : fields ) {
				createMemberValidator( field );
				createChildValidator( resourceBundle, field );
			}
		}
	}

	private void addSuperClassesAndInterfaces(XClass clazz, Collection<XClass> classes) {
		for ( XClass currClass = clazz; currClass != null ; currClass = currClass.getSuperclass() ) {
			if ( ! classes.add( currClass ) ) return;
			XClass[] interfaces = currClass.getInterfaces();
			for ( XClass interf : interfaces ) {
				addSuperClassesAndInterfaces( interf, classes );
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void createChildValidator(ResourceBundle resourceBundle, XMember member) {
		if ( member.isAnnotationPresent( Valid.class ) ) {
			setAccessible( member );
			childGetters.add( member );
			XClass clazz;
			if ( member.isCollection() || member.isArray() ) {
				clazz = member.getElementClass();
			}
			else {
				clazz = member.getType();
			}
			if ( !childClassValidators.containsKey( clazz ) ) {
				new ClassValidator( clazz, resourceBundle, childClassValidators );
			}
		}
	}

	private void createMemberValidator(XMember member) {
    	if( !member.isTypeResolved() )
			log.warn( "Original type of property " + member + " is unbound and has been approximated.");
		Annotation[] memberAnnotations = member.getAnnotations();
		for ( int j = 0; j < memberAnnotations.length ; j++ ) {
			Annotation methodAnnotation = memberAnnotations[j];
			Validator propertyValidator = createValidator( methodAnnotation );
			if ( propertyValidator != null ) {
				memberValidators.add( propertyValidator );
				setAccessible( member );
				memberGetters.add( member );
			}
		}
	}

	private static void setAccessible(XMember member) {
		if ( !Modifier.isPublic( member.getModifiers() ) ) {
			member.setAccessible( true );
		}
	}

	@SuppressWarnings("unchecked")
	private Validator createValidator(Annotation annotation) {
		try {
			ValidatorClass validatorClass = annotation.annotationType().getAnnotation( ValidatorClass.class );
			if ( validatorClass == null ) {
				return null;
			}
			Validator beanValidator = validatorClass.value().newInstance();
			beanValidator.initialize( annotation );
			String messageTemplate = (String) annotation.getClass()
					.getMethod( "message", (Class[]) null )
					.invoke( annotation );
			String message = replace( messageTemplate, annotation );
			messages.put( beanValidator, message );
			return beanValidator;
		}
		catch (Exception e) {
			throw new IllegalArgumentException( "could not instantiate ClassValidator", e );
		}
	}

	public boolean hasValidationRules() {
		return beanValidators.size() != 0 || memberValidators.size() != 0;
	}

	/**
	 * apply constraints on a bean instance and return all the failures.
	 * if <code>bean</code> is null, an empty array is returned
	 */
	public InvalidValue[] getInvalidValues(T bean) {
		return this.getInvalidValues( bean, new IdentitySet() );
	}

	/**
	 * apply constraints on a bean instance and return all the failures.
	 * if <code>bean</code> is null, an empty array is returned
	 */
	@SuppressWarnings("unchecked")
	protected InvalidValue[] getInvalidValues(T bean, Set<Object> circularityState) {
		if ( bean == null || circularityState.contains( bean ) ) {
			return EMPTY_INVALID_VALUE_ARRAY; //Avoid circularity
		}
		else {
			circularityState.add( bean );
		}

		if ( !beanClass.isInstance( bean ) ) {
			throw new IllegalArgumentException( "not an instance of: " + bean.getClass() );
		}

		List<InvalidValue> results = new ArrayList<InvalidValue>();

		for ( int i = 0; i < beanValidators.size() ; i++ ) {
			Validator validator = beanValidators.get( i );
			if ( !validator.isValid( bean ) ) {
				results.add( new InvalidValue( messages.get( validator ), beanClass, null, bean, bean ) );
			}
		}

		for ( int i = 0; i < memberValidators.size() ; i++ ) {
			XMember getter = memberGetters.get( i );
			if ( Hibernate.isPropertyInitialized( bean, getPropertyName( getter ) ) ) {
				Object value = getMemberValue( bean, getter );
				Validator validator = memberValidators.get( i );
				if ( !validator.isValid( value ) ) {
					String propertyName = getPropertyName( getter );
					results.add( new InvalidValue( messages.get( validator ), beanClass, propertyName, value, bean ) );
				}
			}
		}

		for ( int i = 0; i < childGetters.size() ; i++ ) {
			XMember getter = childGetters.get( i );
			if ( Hibernate.isPropertyInitialized( bean, getPropertyName( getter ) ) ) {
				Object value = getMemberValue( bean, getter );
				if ( value != null && Hibernate.isInitialized( value ) ) {
					String propertyName = getPropertyName( getter );
					if ( getter.isCollection() ) {
						int index = 0;
						boolean isIterable = value instanceof Iterable;
						Map map = ! isIterable ? (Map) value : null;
						Iterable elements = isIterable ?
								(Iterable) value :
								map.keySet();
						for ( Object element : elements ) {
							Object actualElement = isIterable ? element : map.get( element );
							if (actualElement == null) {
								index++;
								continue;
							}
							InvalidValue[] invalidValues = getClassValidator( actualElement )
									.getInvalidValues( actualElement, circularityState );

							String indexedPropName = MessageFormat.format(
									"{0}[{1}]",
									propertyName,
									INDEXABLE_CLASS.contains( element.getClass() ) ?
											("'" + element + "'") :
											index
							);
							index++;

							for ( InvalidValue invalidValue : invalidValues ) {
								invalidValue.addParentBean( bean, indexedPropName );
								results.add( invalidValue );
							}
						}
					}
					if ( getter.isArray() ) {
						int index = 0;
						for ( Object element : (Object[]) value ) {
							if (element == null) {
								index++;
								continue;
							}
							InvalidValue[] invalidValues = getClassValidator( element )
									.getInvalidValues( element, circularityState );

							String indexedPropName = MessageFormat.format(
									"{0}[{1}]",
									propertyName,
									index
							);
							index++;

							for ( InvalidValue invalidValue : invalidValues ) {
								invalidValue.addParentBean( bean, indexedPropName );
								results.add( invalidValue );
							}
						}
					}
					else {
						InvalidValue[] invalidValues = getClassValidator( value )
								.getInvalidValues( value, circularityState );
						for ( InvalidValue invalidValue : invalidValues ) {
							invalidValue.addParentBean( bean, propertyName );
							results.add( invalidValue );
						}
					}
				}
			}
		}

		return results.toArray( new InvalidValue[results.size()] );
	}

	@SuppressWarnings("unchecked")
	private ClassValidator getClassValidator(Object value) {
		Class clazz = value.getClass();
		ClassValidator validator = childClassValidators.get( ReflectionManager.INSTANCE.toXClass( clazz ) );
		if ( validator == null ) { //handles polymorphism
			validator = new ClassValidator( clazz );
		}
		return validator;
	}

	/**
	 * Apply constraints of a particular property on a bean instance and return all the failures.
	 * Note this is not recursive.
	 */
	//TODO should it be recursive?
	public InvalidValue[] getInvalidValues(T bean, String propertyName) {
		List<InvalidValue> results = new ArrayList<InvalidValue>();

		for ( int i = 0; i < memberValidators.size() ; i++ ) {
			XMember getter = memberGetters.get( i );
			if ( getPropertyName( getter ).equals( propertyName ) ) {
				Object value = getMemberValue( bean, getter );
				Validator validator = memberValidators.get( i );
				if ( !validator.isValid( value ) ) {
					results.add( new InvalidValue( messages.get( validator ), beanClass, propertyName, value, bean ) );
				}
			}
		}

		return results.toArray( new InvalidValue[results.size()] );
	}

	/**
	 * Apply constraints of a particular property value of a bean type and return all the failures.
	 * The InvalidValue objects returns return null for InvalidValue#getBean() and InvalidValue#getRootBean()
	 * Note this is not recursive.
	 */
	//TODO should it be recursive?
	public InvalidValue[] getPotentialInvalidValues(String propertyName, Object value) {
		List<InvalidValue> results = new ArrayList<InvalidValue>();

		for ( int i = 0; i < memberValidators.size() ; i++ ) {
			XMember getter = memberGetters.get( i );
			if ( getPropertyName( getter ).equals( propertyName ) ) {
				Validator validator = memberValidators.get( i );
				if ( !validator.isValid( value ) ) {
					results.add( new InvalidValue( messages.get( validator ), beanClass, propertyName, value, null ) );
				}
			}
		}

		return results.toArray( new InvalidValue[results.size()] );
	}

	private Object getMemberValue(T bean, XMember getter) {
		Object value;
		try {
			value = getter.invoke( bean );
		}
		catch (Exception e) {
			throw new IllegalStateException( "Could not get property value", e );
		}
		return value;
	}

	public String getPropertyName(XMember member) {
		//Do no try to cache the result in a map, it's actually much slower (2.x time)
		String propertyName;
		if ( XProperty.class.isAssignableFrom( member.getClass() ) ) {
			propertyName = member.getName();
		}
		else if ( XMethod.class.isAssignableFrom( member.getClass() ) ) {
			propertyName = member.getName();
			if ( propertyName.startsWith( "is" ) ) {
				propertyName = Introspector.decapitalize( propertyName.substring( 2 ) );
			}
			else if ( propertyName.startsWith( "get" ) ) {
				propertyName = Introspector.decapitalize( propertyName.substring( 3 ) );
			}
			//do nothing for non getter method, in case someone want to validate a PO Method
		}
		else {
			throw new AssertionFailure( "Unexpected member: " + member.getClass().getName() );
		}
		return propertyName;
	}

	private String replace(String message, Annotation parameters) {
		StringTokenizer tokens = new StringTokenizer( message, "{}", true );
		StringBuilder buf = new StringBuilder( 30 );
		boolean escaped = false;
		while ( tokens.hasMoreTokens() ) {
			String token = tokens.nextToken();
			if ( "{".equals( token ) ) {
				escaped = true;
			}
			else if ( "}".equals( token ) ) {
				escaped = false;
			}
			else if ( !escaped ) {
				buf.append( token );
			}
			else {
				Method member;
				try {
					member = parameters.getClass().getMethod( token, (Class[]) null );
				}
				catch (NoSuchMethodException nsfme) {
					member = null;
				}
				if ( member != null ) {
					try {
						buf.append( member.invoke( parameters ) );
					}
					catch (Exception e) {
						throw new IllegalArgumentException( "could not render message", e );
					}
				}
				else if ( messageBundle != null ) {
					String string = messageBundle.getString( token );
					if ( string != null ) buf.append( replace( string, parameters ) );
				}
			}
		}
		return buf.toString();
	}

	/**
	 * apply the registred constraints rules on the hibernate metadata (to be applied on DB schema...)
	 *
	 * @param persistentClass hibernate metadata
	 */
	public void apply(PersistentClass persistentClass) {

		for ( Validator validator : beanValidators ) {
			if ( validator instanceof PersistentClassConstraint ) {
				( (PersistentClassConstraint) validator ).apply( persistentClass );
			}
		}

		Iterator<Validator> validators = memberValidators.iterator();
		Iterator<XMember> getters = memberGetters.iterator();
		while ( validators.hasNext() ) {
			Validator validator = validators.next();
			String propertyName = getPropertyName( getters.next() );
			if ( validator instanceof PropertyConstraint ) {
				try {
					Property property = persistentClass.getIdentifierProperty();
					if ( property == null || ! propertyName.equals( property.getName() ) ) {
						property = persistentClass.getProperty( propertyName );
					}
					( (PropertyConstraint) validator ).apply( property );
				}
				catch (MappingException pnfe) {
					//do nothing
				}
			}
		}

	}

	public void assertValid(T bean) {
		InvalidValue[] values = getInvalidValues( bean );
		if ( values.length > 0 ) {
			throw new InvalidStateException( values );
		}
	}

	private void writeObject(ObjectOutputStream oos) throws IOException {
		ResourceBundle rb = messageBundle;
		if ( rb != null && ! ( rb instanceof Serializable ) ) {
			messageBundle = null;
			if ( ! defaultResourceBundle ) {
				log.warn(
						"Serializing a ClassValidator with a not serializable ResourceBundle: ResourceBundle ignored"
				);
			}
		}
		oos.defaultWriteObject();
		oos.writeObject( messageBundle );
		messageBundle = rb;
	}

	private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
		ois.defaultReadObject();
		ResourceBundle rb = (ResourceBundle) ois.readObject();
		if ( rb == null ) rb = getDefaultResourceBundle();
		initValidator( ReflectionManager.INSTANCE.toXClass( beanClass ), new HashMap<XClass, ClassValidator>(), rb );
	}
}
