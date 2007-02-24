package com.bm.ejb3metadata.annotations.metadata.interfaces;

import com.bm.ejb3metadata.annotations.impl.JInterceptors;

/**
 * This interface represents methods which can be call on
 * ClassAnnotationMetadata and MethodAnnotationMetadata.<br>
 * It manages &#64;{@link javax.interceptor.Interceptors} annotation.
 * 
 * @author Daniel Wiese
 */
public interface IEJBInterceptors {

	/**
	 * Set JInterceptors object.
	 * 
	 * @param annotationInterceptors
	 *            object representing javax.ejb.Interceptors annotation value.
	 */
	void setAnnotationsInterceptors(final JInterceptors annotationInterceptors);
}
