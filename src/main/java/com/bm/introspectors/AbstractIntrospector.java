package com.bm.introspectors;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.persistence.PersistenceContext;



import com.bm.ejb3metadata.annotations.metadata.ClassAnnotationMetadata;
import com.bm.ejb3metadata.annotations.metadata.MetaDataCache;
import com.bm.ejb3metadata.annotations.metadata.MethodAnnotationMetadata;
import com.bm.utils.Ejb3Utils;

/**
 * Abstract instrospector fur Session, MDBs und JBoss services.
 * 
 * @author Daniel Wiese
 * @since 24.03.2007
 * @param <T>
 *            the type
 * 
 */
public abstract class AbstractIntrospector<T> implements IIntrospector<T> {
	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory
			.getLogger(SessionBeanIntrospector.class);

	/** we assume thet a injected field has only one annotation * */
	private Map<Property, Annotation> annotationForField = new HashMap<Property, Annotation>();

	/** because the entity manager is often used so, we store it extra * */
	private Property entityManagerField = null;

	/** the fields to inject * */
	private Set<Property> fieldsToInject = new HashSet<Property>();

	protected ClassAnnotationMetadata classMetaData = null;

	/** the class represented by this introspector (e.g. the entity bean)* */
	protected final Class<? extends T> representingClass;

	/**
	 * Constructor.
	 * 
	 * @param toInspect -
	 *            the class to inspect.
	 */
	public AbstractIntrospector(Class<? extends T> toInspect) {
		this.representingClass = toInspect;
		classMetaData = MetaDataCache.getMetaData(toInspect);
	}

	/**
	 * {@inheritDoc}
	 * @see com.bm.introspectors.IIntrospector#getAnnotationForField(com.bm.introspectors.Property)
	 */
	public Annotation getAnnotationForField(Property field) {
		return this.annotationForField.get(field);
	}

	/**
	 * {@inheritDoc}
	 * @see com.bm.introspectors.IIntrospector#getClassMetaData()
	 */
	public ClassAnnotationMetadata getClassMetaData() {
		return this.classMetaData;
	}

	/**
	 * {@inheritDoc}
	 * @see com.bm.introspectors.IIntrospector#getEntityManagerField()
	 */
	public Property getEntityManagerField() {
		return this.entityManagerField;
	}

	/**
	 * {@inheritDoc}
	 * @see com.bm.introspectors.IIntrospector#getFieldsToInject()
	 */
	public Set<Property> getFieldsToInject() {
		return this.fieldsToInject;
	}

	/**
	 * {@inheritDoc}
	 * @see com.bm.introspectors.IIntrospector#getImplementationForInterface(java.lang.Class)
	 */
	public Class<?> getImplementationForInterface(Class<?> toInspect) {

		// try to get the implemenation name for the interface
		String implementationName = MetaDataCache
				.getBeanImplementationForInterface(toInspect);

		log.debug("Using: Local/Remote Interface (" + toInspect
				+ ") --> Implemetation (" + implementationName + ")");

		Class<?> implementation = null;
		try {
			implementation = Thread.currentThread().getContextClassLoader()
					.loadClass(implementationName.replace('/', '.'));
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Class (" + implementationName
					+ ") was not found as an implementation for interface ("
					+ toInspect.getName() + ")");
		}

		return implementation;
	}

	/**
	 * {@inheritDoc}
	 * @see com.bm.introspectors.IIntrospector#getLifecycleMethods()
	 */
	public Set<MethodAnnotationMetadata> getLifecycleMethods() {
		final Set<MethodAnnotationMetadata> back = new HashSet<MethodAnnotationMetadata>();
		final Collection<MethodAnnotationMetadata> methods = this
				.getClassMetaData().getMethodAnnotationMetadataCollection();
		for (MethodAnnotationMetadata current : methods) {
			if (current.isLifeCycleMethod()) {
				back.add(current);
			}
		}

		return back;
	}

	/**
	 * {@inheritDoc}
	 * @see com.bm.introspectors.IIntrospector#getRepresentingClass()
	 */
	public Class<? extends T> getRepresentingClass() {
		return this.representingClass;
	}

	/**
	 * {@inheritDoc}
	 * @see com.bm.introspectors.IIntrospector#hasEntityManager()
	 */
	public boolean hasEntityManager() {
		return this.entityManagerField != null;
	}

	/**
	 * Register a new field with annotation
	 * 
	 * @author Daniel Wiese
	 * @since 08.11.2005
	 * @param aktProperty -
	 *            akt property
	 * @param a -
	 *            akt annotation
	 */
	private void addAnotatedField(Property aktProperty, Annotation a) {
		this.fieldsToInject.add(aktProperty);
		this.annotationForField.put(aktProperty, a);
	}

	/**
	 * Check if the field is static
	 * 
	 * @param toCheck
	 *            -the field to check
	 * @return - true if static
	 */
	private boolean isStatic(Field toCheck) {
		return Modifier.isStatic(toCheck.getModifiers());

	}

	/**
	 * Anylse the annotation of a (field or getterMethod)
	 * 
	 * @param aktProperty -
	 *            the property
	 * @param propertyAnnotations -
	 *            the corresponding annotations
	 */
	private void processAnnotations(Property aktProperty,
			Annotation[] propertyAnnotations) {

		// look into the annotations
		for (Annotation a : propertyAnnotations) {
			// skip transient fields
			if (a instanceof PersistenceContext) {
				this.entityManagerField = aktProperty;
				addAnotatedField(aktProperty, a);
			} else if (a instanceof Resource) {
				addAnotatedField(aktProperty, a);
			} else if (a instanceof EJB) {
				addAnotatedField(aktProperty, a);
			} else {
				log.warn("The Annotation (" + a + ") for field (" + aktProperty
						+ ") will not be injected (is unknown)");
			}
		}
	}

	/**
	 * If the access type is field, we will extract all the neccessary meta
	 * informations from the fields.
	 * 
	 * @param toInspect -
	 *            the class to inspect
	 */
	protected void processAccessTypeField(Class<? extends T> toInspect) {

		// extract meta information
		Field[] fields = Ejb3Utils.getAllFields(toInspect);
		for (Field aktField : fields) {
			// dont´s introspect fields generated by hibernate
			if (!this.isStatic(aktField) && !aktField.getName().startsWith("$")) {
				Annotation[] fieldAnnotations = aktField.getAnnotations();
				this.processAnnotations(new Property(aktField),
						fieldAnnotations);
			}
		}
	}

}
