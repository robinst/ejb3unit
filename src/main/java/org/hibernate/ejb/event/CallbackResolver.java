/*
 * JBoss, the OpenSource EJB server
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.hibernate.ejb.event;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import javax.persistence.PersistenceException;
import javax.persistence.ExcludeSuperclassListeners;

/**
 * @author <a href="mailto:kabir.khan@jboss.org">Kabir Khan</a>
 * @version $Revision: 1.1 $
 */
public final class CallbackResolver {
	private static boolean useAnnotationAnnotatedByListener;
	{
		//check whether reading annotations of annotations is useful or not
		useAnnotationAnnotatedByListener = false;
		Target target = (Target) EntityListeners.class.getAnnotation( Target.class );
		if (target != null) {
			for ( ElementType type : target.value() ) {
				if ( type.equals( ElementType.ANNOTATION_TYPE ) ) useAnnotationAnnotatedByListener = true;
			}
		}
	}

	private CallbackResolver() {}

	public static Callback resolveCallback(Class beanClass, Class annotation, List<Class> listeners) throws Exception {
		Callback callback = null;
		Method[] methods = beanClass.getDeclaredMethods();

		for ( int i = 0 ; i < methods.length ; i++ ) {
			if ( methods[i].getAnnotation( annotation ) != null ) {
				if ( callback == null ) {
					callback = new BeanCallback( methods[i] );
					Class returnType = methods[i].getReturnType();
					Class[] args = methods[i].getParameterTypes();
					if ( returnType != Void.TYPE || args.length != 0 ) {
						throw new RuntimeException(
								"Callback methods annotated on the bean class must return void and take no arguments: " + annotation.getName() + " - " + methods[i]
						);
					}
					if ( ! methods[i].isAccessible() ) {
						methods[i].setAccessible( true );
					}
				}
				else {
					throw new RuntimeException(
							"You can only annotate one callback method with "
							+ annotation.getName() + " in bean class: " + beanClass.getName()
					);
				}
			}
		}

		for ( Class listener : listeners ) {
			if ( listener != null ) {
				try {
					methods = listener.getDeclaredMethods();
					for ( int i = 0 ; i < methods.length ; i++ ) {
						if ( methods[i].getAnnotation( annotation ) != null ) {
							if ( callback == null ) {
								callback = new ListenerCallback( methods[i], listener.newInstance() );
								Class returnType = methods[i].getReturnType();
								Class[] args = methods[i].getParameterTypes();
								if ( returnType != Void.TYPE || args.length != 1 ) {
									throw new RuntimeException(
											"Callback methods annotated in a listener bean class must return void and take one argument: " + annotation.getName() + " - " + methods[i]
									);
								}
								if ( ! methods[i].isAccessible() ) {
									methods[i].setAccessible( true );
								}
							}
							else {
								throw new RuntimeException(
										"You can only annotate one callback method with "
										+ annotation.getName() + " in bean class: " + beanClass.getName() + " and callback listener: "
										+ listener.getName()
								);
							}
						}
					}
				}
				catch (Exception e) {
					throw new RuntimeException( e.getCause() );
				}
			}
		}
		return callback;
	}

	public static Callback[] resolveCallback(Class beanClass, Class annotation) {
		List<Callback> callbacks = new ArrayList<Callback>();
		List<Class> orderedListeners = new ArrayList<Class>();
		Class currentClazz = beanClass;
		boolean stopListeners = false;
		//TODO add exclude default listeners
		do {
			//FIXME exclude overriden callback methods
			Callback callback = null;
			Method[] methods = currentClazz.getDeclaredMethods();
			for ( int i = 0 ; i < methods.length ; i++ ) {
				if ( methods[i].getAnnotation( annotation ) != null ) {
					if (callback == null) {
						callback = new BeanCallback( methods[i] );
						Class returnType = methods[i].getReturnType();
						Class[] args = methods[i].getParameterTypes();
						if ( returnType != Void.TYPE || args.length != 0 ) {
							throw new RuntimeException(
									"Callback methods annotated on the bean class must return void and take no arguments: " + annotation.getName() + " - " + methods[i]
							);
						}
						if ( ! methods[i].isAccessible() ) {
							methods[i].setAccessible( true );
						}
						callbacks.add(0, callback); //superclass first
					}
					else {
						throw new PersistenceException(
								"You can only annotate one callback method with "
								+ annotation.getName() + " in bean class: " + beanClass.getName()
						);
					}
				}
			}
			if (!stopListeners)	{
				getListeners( currentClazz, orderedListeners );
				stopListeners = currentClazz.isAnnotationPresent( ExcludeSuperclassListeners.class );
			}

			do {
				currentClazz = currentClazz.getSuperclass();
			} while (currentClazz != null
					&& ! ( currentClazz.isAnnotationPresent( Entity.class)
						|| currentClazz.isAnnotationPresent( MappedSuperclass.class) )
			);
		}
		while ( currentClazz != null );

		for (Class listener : orderedListeners) {
			Callback callback = null;
			if ( listener != null ) {
				Method[] methods = listener.getDeclaredMethods();
				for ( int i = 0 ; i < methods.length ; i++ ) {
					if ( methods[i].getAnnotation( annotation ) != null ) {
						if ( callback == null ) {
							try {
								callback = new ListenerCallback( methods[i], listener.newInstance() );
							}
							catch (IllegalAccessException e) {
								throw new PersistenceException( "Unable to create instance of " + listener.getName()
									+ " as a listener of beanClass", e);
							}
							catch (InstantiationException e) {
								throw new PersistenceException( "Unable to create instance of " + listener.getName()
									+ " as a listener of beanClass", e);
							}
							Class returnType = methods[i].getReturnType();
							Class[] args = methods[i].getParameterTypes();
							if ( returnType != Void.TYPE || args.length != 1 ) {
								throw new PersistenceException(
										"Callback methods annotated in a listener bean class must return void and take one argument: " + annotation.getName() + " - " + methods[i]
								);
							}
							if ( ! methods[i].isAccessible() ) {
								methods[i].setAccessible( true );
							}
							callbacks.add(0, callback); // listeners first
						}
						else {
							throw new PersistenceException(
									"You can only annotate one callback method with "
									+ annotation.getName() + " in bean class: " + beanClass.getName() + " and callback listener: "
									+ listener.getName()
							);
						}
					}
				}
			}
		}
		return callbacks.toArray( new Callback[ callbacks.size() ] );
	}

	private static void getListeners(Class currentClazz, List<Class> orderedListeners) {
		EntityListeners entityListeners = (EntityListeners) currentClazz.getAnnotation( EntityListeners.class );
		if ( entityListeners != null ) {
			Class[] classes = entityListeners.value();
			int size = classes.length;
			for (int index = size - 1 ; index >= 0 ; index--) {
				orderedListeners.add( classes[index] );
			}
		}
		if (useAnnotationAnnotatedByListener) {
			Annotation[] annotations = currentClazz.getAnnotations();
			for (Annotation annot : annotations) {
				entityListeners = annot.getClass().getAnnotation( EntityListeners.class );
				if (entityListeners != null ) {
					Class[] classes = entityListeners.value();
					int size = classes.length;
					for (int index = size - 1 ; index >= 0 ; index--) {
						orderedListeners.add( classes[index] );
					}
				}
			}
		}
	}
}
