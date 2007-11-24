package com.bm.utils;

import java.lang.reflect.Method;
import java.util.Set;

import com.bm.ejb3metadata.annotations.metadata.ClassAnnotationMetadata;
import com.bm.ejb3metadata.annotations.metadata.MetaDataCache;
import com.bm.ejb3metadata.annotations.metadata.MethodAnnotationMetadata;
import com.bm.introspectors.IntrospectorFactory;

/**
 * Invokes the life cycle methods.
 * 
 * @author Daniel Wiese
 * @since Jul 19, 2007
 */
public class LifeCycleMethodExecuter {

	/**
	 * Executes the life cycle methods after a object was created.
	 * 
	 * @author Daniel Wiese
	 * @since Jul 19, 2007
	 * @param justCreated
	 *            invokes life cycle methods.
	 */
	public void executeLifeCycleMethodsForCreate(Object justCreated) {
		ClassAnnotationMetadata classMeta = MetaDataCache.getMetaData(justCreated
				.getClass());
		if (classMeta != null && (classMeta.isBean() || classMeta.isMdb())) {
			final Set<MethodAnnotationMetadata> lifeCycleMethods = IntrospectorFactory
					.createIntrospector(justCreated.getClass()).getLifecycleMethods();
			for (MethodAnnotationMetadata current : lifeCycleMethods) {
				if (current.isPostConstruct() || current.isPostActivate()) {
					if (current.getJMethod().getSignature() != null) {
						throw new IllegalArgumentException("The life cycle method ("
								+ current.getJMethod() + ") has arguments");
					}
					Method toInvoke = Ejb3Utils.getParameterlessMethodByName(current
							.getMethodName(), justCreated.getClass());
					// dont inject lifecycle methods with this name
					// 'injectGuiceDependencies' This method is reserved for
					// guice integration
					if (!toInvoke.getName().equalsIgnoreCase("injectGuiceDependencies")) {
						toInvoke.setAccessible(true);
						try {
							toInvoke.invoke(justCreated, (Object[]) null);
						} catch (Exception e) {
							throw new IllegalArgumentException("Can't invoke method ("
									+ current.getJMethod() + ")", e);
						}
					}
				}
			}
		}

	}
}
