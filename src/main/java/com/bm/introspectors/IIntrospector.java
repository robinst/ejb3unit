package com.bm.introspectors;

import java.lang.annotation.Annotation;
import java.util.Set;

import com.bm.ejb3metadata.annotations.metadata.ClassAnnotationMetadata;
import com.bm.ejb3metadata.annotations.metadata.MethodAnnotationMetadata;

public interface IIntrospector<T> {

	/**
	 * Returns the representingClass.
	 * 
	 * @return Returns the representingClass.
	 */
	public abstract Class<? extends T> getRepresentingClass();

	/**
	 * Returns the annotationForField.
	 * 
	 * @param field -
	 *            for field
	 * @return Returns the annotationForField.
	 */
	public abstract Annotation getAnnotationForField(Property field);

	/**
	 * Returns the fieldsToInject.
	 * 
	 * @return Returns the fieldsToInject.
	 */
	public abstract Set<Property> getFieldsToInject();

	/**
	 * Returns the lifecycle methods.
	 * 
	 * @return Returns the lofe cycle methods.
	 */
	public abstract Set<MethodAnnotationMetadata> getLifecycleMethods();

	/**
	 * True is the session bean has an entity manager.
	 * 
	 * @author Daniel Wiese
	 * @since 08.11.2005
	 * @return - true if yes
	 */
	public abstract boolean hasEntityManager();

	/**
	 * Returns the field, which holds the reference to the entity manager.
	 * 
	 * @author Daniel Wiese
	 * @since 08.11.2005
	 * @return - the em field
	 */
	public abstract Property getEntityManagerField();

	/**
	 * Returns the classMetaData.
	 * 
	 * @return Returns the classMetaData.
	 */
	public abstract ClassAnnotationMetadata getClassMetaData();

	/**
	 * Get the implemenation name for the interface.
	 * 
	 * 
	 * @param toInspect
	 *            interface to whoes implementation to look for
	 * @return the implementation of the parameter interface
	 */
	public abstract Class<?> getImplementationForInterface(Class<?> toInspect);

}