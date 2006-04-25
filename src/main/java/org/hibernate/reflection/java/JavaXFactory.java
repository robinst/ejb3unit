package org.hibernate.reflection.java;

import java.lang.reflect.Member;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.reflection.ReflectionManager;
import org.hibernate.reflection.XClass;
import org.hibernate.reflection.XMethod;
import org.hibernate.reflection.XPackage;
import org.hibernate.reflection.XProperty;
import org.hibernate.reflection.java.generics.IdentityTypeEnvironment;
import org.hibernate.reflection.java.generics.TypeEnvironment;
import org.hibernate.reflection.java.generics.TypeEnvironmentFactory;
import org.hibernate.reflection.java.generics.TypeSwitch;
import org.hibernate.reflection.java.generics.TypeUtils;
import org.hibernate.reflection.java.xml.XMLContext;
import org.hibernate.util.ReflectHelper;
import org.hibernate.util.XMLHelper;
import org.xml.sax.EntityResolver;

/**
 * The factory for all the objects in this package.
 *
 * @author Paolo Perrotta
 * @author Davide Marchignoli
 */
public class JavaXFactory implements ReflectionManager {

	private static Log log = LogFactory.getLog( JavaXFactory.class );
	private transient XMLHelper xmlHelper;
	private EntityResolver entityResolver;
	private XMLContext xmlContext;

	private static class TypeKey extends Pair<Type, TypeEnvironment> {
		TypeKey(Type t, TypeEnvironment context) {
			super( t, context );
		}
	}

	private static class MemberKey extends Pair<Member, TypeKey> {
		MemberKey(Member member, Type owner, TypeEnvironment context) {
			super( member, new TypeKey( owner, context ) );
		}
	}

	private final Map<TypeKey, JavaXClass> xClasses = new HashMap<TypeKey, JavaXClass>();

	private final Map<Package, JavaXPackage> packagesToXPackages = new HashMap<Package, JavaXPackage>();

	private final Map<MemberKey, JavaXProperty> xProperties = new HashMap<MemberKey, JavaXProperty>();
	
	private final Map<MemberKey, JavaXMethod> xMethods = new HashMap<MemberKey, JavaXMethod>();

	private final TypeEnvironmentFactory typeEnvs = new TypeEnvironmentFactory();

	public JavaXFactory() {
		reset();
	}

	private void reset() {
		xmlHelper = new XMLHelper();
		entityResolver = XMLHelper.DEFAULT_DTD_RESOLVER;
		xmlContext = new XMLContext();
	}

	public XClass toXClass(Class clazz) {
		return toXClass( clazz, IdentityTypeEnvironment.INSTANCE );
	}

	public Class toClass(XClass xClazz) {
		if ( ! ( xClazz instanceof JavaXClass ) ) {
			throw new IllegalArgumentException( "XClass not coming from this ReflectionManager implementation" );
		}
		return (Class) ( (JavaXClass) xClazz ).toAnnotatedElement();
	}

	public XClass classForName(String name, Class caller) throws ClassNotFoundException {
		return toXClass( ReflectHelper.classForName( name, caller ) );
	}

	public XPackage packageForName(String packageName) throws ClassNotFoundException {
		return getXAnnotatedElement( ReflectHelper.classForName( packageName + ".package-info" ).getPackage() );
	}

	XClass toXClass(Type t, final TypeEnvironment context) {
		return new TypeSwitch<XClass>() {
			@Override
			public XClass caseClass(Class classType) {
				TypeKey key = new TypeKey( classType, context );
				JavaXClass result = xClasses.get( key );
				if ( result == null ) {
					result = new JavaXClass( classType, context, JavaXFactory.this );
					//TODO get rid of it
					//result.setXMLDescriptor( xml );
					xClasses.put( key, result );
				}
				return result;
			}

			@Override
			public XClass caseParameterizedType(ParameterizedType parameterizedType) {
				return toXClass( parameterizedType.getRawType(), context );
			}
		}.doSwitch( context.bind( t ) );
	}

	XPackage getXAnnotatedElement(Package pkg) {
		JavaXPackage xPackage = packagesToXPackages.get( pkg );
		if ( xPackage == null ) {
			xPackage = new JavaXPackage( pkg, this );
			//TODO get rid of it
			//xPackage.setXMLDescriptor( xml );
			packagesToXPackages.put( pkg, xPackage );
		}
		return xPackage;
	}

	XProperty getXProperty(Member member, JavaXClass owner) {
		MemberKey key = new MemberKey( member, owner.toClass(), owner.getTypeEnvironment() );
		JavaXProperty xProperty = xProperties.get( key );
		if ( ! xProperties.containsKey( key ) ) {
			xProperty = JavaXProperty.create( member, owner.getTypeEnvironment(), this );
			xProperties.put( key, xProperty );
		}
		return xProperty;
	}

	XMethod getXMethod(Member member, JavaXClass owner) {
		MemberKey key = new MemberKey( member, owner.toClass(), owner.getTypeEnvironment() );
		JavaXMethod xMethod = xMethods.get( key );
		if ( ! xMethods.containsKey( key ) ) {
			xMethod = JavaXMethod.create( member, owner.getTypeEnvironment(), this );
			xMethods.put( key, xMethod );
		}
		return xMethod;
	}

	TypeEnvironment getTypeEnvironment(final Type t) {
		return new TypeSwitch<TypeEnvironment>() {
			@Override
			public TypeEnvironment caseClass(Class classType) {
				return typeEnvs.getEnvironment( classType );
			}

			@Override
			public TypeEnvironment caseParameterizedType(ParameterizedType parameterizedType) {
				return typeEnvs.getEnvironment( parameterizedType );
			}

			@Override
			public TypeEnvironment defaultCase(Type type) {
				return IdentityTypeEnvironment.INSTANCE;
			}
		}.doSwitch( t );
	}

	public JavaXType toXType(TypeEnvironment context, Type propType) {
		Type boundType = toApproximatingEnvironment( context ).bind( propType );
		if ( TypeUtils.isArray( boundType ) )
			return new JavaXArrayType( propType, context, this );
		if ( TypeUtils.isCollection( boundType ) )
			return new JavaXCollectionType( propType, context, this );
		if ( TypeUtils.isSimple( boundType ) )
			return new JavaXSimpleType( propType, context, this );
		throw new IllegalArgumentException( "No PropertyTypeExtractor available for type void ");
	}

	public boolean equals(XClass class1, Class class2) {
		if ( class1 == null ) {
			return class2 == null;
		}
		return ( (JavaXClass) class1 ).toClass().equals( class2 );
	}

	public TypeEnvironment toApproximatingEnvironment(TypeEnvironment context) {
		return typeEnvs.toApproximatingEnvironment( context );
	}

	XMLContext getXMLContext() {
		return xmlContext;
	}
}
