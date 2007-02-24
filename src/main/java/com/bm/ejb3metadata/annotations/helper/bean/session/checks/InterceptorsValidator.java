/**
 * EasyBeans
 * Copyright (C) 2006 Bull S.A.S.
 * Contact: easybeans@objectweb.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 * --------------------------------------------------------------------------
 * $Id: InterceptorsValidator.java 450 2006-05-12 15:30:25Z benoitf $
 * --------------------------------------------------------------------------
 */

package com.bm.ejb3metadata.annotations.helper.bean.session.checks;

import java.util.List;

import static org.ejb3unit.asm.Opcodes.ACC_FINAL;
import static org.ejb3unit.asm.Opcodes.ACC_STATIC;

import com.bm.ejb3metadata.annotations.JMethod;
import com.bm.ejb3metadata.annotations.exceptions.InterceptorsValidationException;
import com.bm.ejb3metadata.annotations.impl.JInterceptors;
import com.bm.ejb3metadata.annotations.metadata.ClassAnnotationMetadata;
import com.bm.ejb3metadata.annotations.metadata.EjbJarAnnotationMetadata;
import com.bm.ejb3metadata.annotations.metadata.MethodAnnotationMetadata;

/**
 * This class ensures that the interceptors have the correct signature.
 * 
 * @author Florent Benoit
 */
public final class InterceptorsValidator {

	/**
	 * Signature for an AroundInvoke interceptor (InvocationContext).
	 */
	private static final String AROUND_INVOKE_DESCRIPTOR_EJB = "(Ljavax/interceptor/InvocationContext;)Ljava/lang/Object;";

	/**
	 * Signature for a lifecycle interceptor inside the bean (void type).
	 */
	private static final String LIFECYCLE_DESCRIPTOR_OUTSIDEBEAN = "(Ljavax/interceptor/InvocationContext;)V";

	/**
	 * Signature for a lifecycle interceptor inside the bean (void type).
	 */
	private static final String LIFECYCLE_DESCRIPTOR_BEAN = "()V";

	/**
	 * Exception required in AroundInvoke interceptor.
	 */
	private static final String AROUND_INVOKE_EXCEPTION = "java/lang/Exception";

	/**
	 * Constructor without args.
	 */
	private static final String DEFAULT_CONSTRUCTOR_DESCRIPTOR = "()V";

	/**
	 * Default constructor method's name.
	 */
	private static final String CONSTRUCTOR_METHOD = "<init>";

	/**
	 * Helper class, no public constructor.
	 */
	private InterceptorsValidator() {
	}

	/**
	 * Validate a bean.
	 * 
	 * @param bean
	 *            bean to validate.
	 */
	public static void validate(final ClassAnnotationMetadata bean) {

		// Root metadata
		EjbJarAnnotationMetadata ejbMetaData = bean
				.getEjbJarAnnotationMetadata();

		// Interceptors in the bean
		if (bean.isBean()) {
			for (MethodAnnotationMetadata method : bean
					.getMethodAnnotationMetadataCollection()) {

				// lifecycle
				if (method.isLifeCycleMethod()) {
					validateJMethod(method.getJMethod(),
							LIFECYCLE_DESCRIPTOR_BEAN, null, bean
									.getClassName());
				} else if (method.isAroundInvoke()) {
					validateJMethod(method.getJMethod(),
							AROUND_INVOKE_DESCRIPTOR_EJB,
							AROUND_INVOKE_EXCEPTION, bean.getClassName());
				}

				// Interceptors defined on the bean's methods
				JInterceptors methodInterceptors = method
						.getAnnotationInterceptors();
				// Look in the interceptor class
				if (methodInterceptors != null) {
					for (String className : methodInterceptors.getClasses()) {
						analyzeInterceptorClass(ejbMetaData, className);
					}
				}
			}

			// Now, check interceptors outside the bean
			JInterceptors methodInterceptors = bean.getAnnotationInterceptors();
			// Look in the interceptor class
			if (methodInterceptors != null) {
				for (String className : methodInterceptors.getClasses()) {
					analyzeInterceptorClass(ejbMetaData, className);
				}
			}

			// Analyze interfaces and check that there are no methods with
			// @AroundInvoke or @PostConstruct
			String[] interfaces = bean.getInterfaces();
			if (interfaces != null) {
				for (String itf : interfaces) {
					ClassAnnotationMetadata interfaceMetaData = ejbMetaData
							.getClassAnnotationMetadata(itf);
					if (interfaceMetaData != null) {
						for (MethodAnnotationMetadata method : interfaceMetaData
								.getMethodAnnotationMetadataCollection()) {
							// no AroundInvoke or PostConstruct
							if (method.isAroundInvoke()) {
								throw new InterceptorsValidationException(
										"The method '"
												+ method
												+ "' in the bean class '"
												+ bean.getClassName()
												+ "' cannot be an AroundInvoke as it is an interface");
							}
							if (method.isLifeCycleMethod()) {
								throw new InterceptorsValidationException(
										"The method '"
												+ method
												+ "' in the bean class '"
												+ bean.getClassName()
												+ "' cannot be a lifecycle as it is an interface");
							}
						}
					}
				}
			}
		}

		// Standalone interceptors
		if (bean.isInterceptor()) {
			analyzeInterceptorClass(ejbMetaData, bean.getClassName());
		}

	}

	/**
	 * Analyze an interceptor class and check the interceptors method.
	 * 
	 * @param ejbMetaData
	 *            root metadata used to extract class metadata
	 * @param className
	 *            the name of the class being analyzed
	 */
	private static void analyzeInterceptorClass(
			final EjbJarAnnotationMetadata ejbMetaData, final String className) {
		// get the metadata of the class
		ClassAnnotationMetadata interceptorMetaData = ejbMetaData
				.getClassAnnotationMetadata(className);
		if (interceptorMetaData == null) {
			throw new InterceptorsValidationException(
					"Internal problem as no metadata was found for '"
							+ className + "'.");
		}

		List<MethodAnnotationMetadata> aroundInvokeList = interceptorMetaData
				.getAroundInvokeMethodMetadatas();
		if (aroundInvokeList != null && aroundInvokeList.size() > 1) {
			String errMsg = "There are severals @AroundInvoke in the class '"
					+ className
					+ "', while only one is allowed. List of Methods : '"
					+ aroundInvokeList + "'.";
			throw new InterceptorsValidationException(errMsg);
		}

		// Ensure that interceptor has a default constructor.
		JMethod defaultConstructor = new JMethod(0, CONSTRUCTOR_METHOD,
				DEFAULT_CONSTRUCTOR_DESCRIPTOR, null, null);
		if (interceptorMetaData.getMethodAnnotationMetadata(defaultConstructor) == null) {
			throw new InterceptorsValidationException(
					"No default constructor in the interceptor class '"
							+ className + "'.");
		}

		for (MethodAnnotationMetadata method : interceptorMetaData
				.getMethodAnnotationMetadataCollection()) {

			// lifecycle (outside the bean)
			if (method.isLifeCycleMethod()
					&& !method.getClassAnnotationMetadata().isBean()) {
				validateJMethod(method.getJMethod(),
						LIFECYCLE_DESCRIPTOR_OUTSIDEBEAN, null, className);
			} else if (method.isAroundInvoke()) {
				// signature
				validateJMethod(method.getJMethod(),
						AROUND_INVOKE_DESCRIPTOR_EJB, AROUND_INVOKE_EXCEPTION,
						className);

				// No final or static method
				ensureNoAccess(ACC_FINAL, method.getJMethod(), "Final",
						className);
				ensureNoAccess(ACC_STATIC, method.getJMethod(), "Static",
						className);
			}

		}

	}

	/**
	 * Validate that a given method don't use a given access mode.
	 * 
	 * @param acc
	 *            the access mode to refuse.
	 * @param jMethod
	 *            method to check.
	 * @param desc
	 *            the description of the access.
	 * @param className
	 *            the name of the class of the given method.
	 */
	private static void ensureNoAccess(final int acc, final JMethod jMethod,
			final String desc, final String className) {
		if ((jMethod.getAccess() & acc) == acc) {
			throw new InterceptorsValidationException(
					"The method '"
							+ jMethod
							+ "' of the class '"
							+ className
							+ "' is not compliant on the method access. It shouldn't use the '"
							+ desc + "' keyword.");
		}
	}

	/**
	 * Validate the given method with the given signature/exceptions.
	 * 
	 * @param jMethod
	 *            method to check.
	 * @param desc
	 *            signature to ensure.
	 * @param awaitedException
	 *            exception to ensure.
	 * @param className
	 *            the name of the class of the given method.
	 */
	private static void validateJMethod(final JMethod jMethod,
			final String desc, final String awaitedException,
			final String className) {

		// validate signature
		if (!jMethod.getDescriptor().equals(desc)) {
			throw new InterceptorsValidationException("Method '" + jMethod
					+ "' of the class '" + className
					+ "' is not compliant with the signature '" + desc
					+ "'. Signature found = '" + jMethod.getDescriptor() + "'.");
		}

		// validate exceptions
		String[] exceptions = jMethod.getExceptions();
		if (awaitedException == null) {
			return;
		}

		boolean found = false;

		if (exceptions != null) {
			for (String exception : exceptions) {
				if (exception.equals(awaitedException)) {
					found = true;
				}
			}
		}
		if (!found) {
			throw new InterceptorsValidationException("Method '" + jMethod
					+ "' of the class '" + className
					+ "' is not compliant with the signature '" + desc
					+ "' as the required exception '" + awaitedException
					+ "' is missing.");
		}
	}

}
