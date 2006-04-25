package com.bm.datagen.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import com.bm.datagen.Generator;
import com.bm.datagen.annotations.CleanupGenerator;
import com.bm.datagen.annotations.ForInstance;
import com.bm.datagen.annotations.ForProperty;
import com.bm.datagen.annotations.PrepareGenerator;
import com.bm.datagen.annotations.UsedIntrospector;
import com.bm.introspectors.Introspector;
import com.bm.introspectors.Property;
import com.bm.utils.Ejb3Utils;

/**
 * This class is used to enable dependency injections in generators e.g. a
 * Generator can obtain a reference to a property he is used for -> all
 * references are setted before the call of the next value method
 * 
 * @author Daniel Wiese
 * 
 */
public class GeneratorDependencyInjector {

	private Object instance = null;

	private Introspector introspector = null;

	private Property property = null;

	/**
	 * This class injects the presetted values if the registered annotations are
	 * present.
	 * 
	 * @param toInject -
	 *            the Generator whe the values shuld be injected
	 */
	public void inject(Generator toInject) {
		Field[] fields = Ejb3Utils.getAllFields(toInject.getClass());
		for (Field aktField : fields) {
			// dont�s introspect fields generated by hibernate
			if (!this.isStatic(aktField) && !aktField.getName().startsWith("$")) {
				Annotation[] fieldAnnotations = aktField.getAnnotations();
				for (Annotation akt : fieldAnnotations) {
					// now check the registered anotations
					if (akt instanceof ForInstance) {
						this.setField(toInject, aktField, this.instance);
					} else if (akt instanceof ForProperty) {
						this.setField(toInject, aktField, this.property);
					} else if (akt instanceof UsedIntrospector) {
						this.setField(toInject, aktField, this.introspector);
					}
				}
			}
		}
	}

	/**
	 * Invokes all annotated Prepare methods at the current generator.
	 * 
	 * @PrepareGenerator methods in generator
	 * @param toInvoke - the generator wich method gets invoked
	 */
	public void callPrepare(Generator toInvoke) {
		Method[] methods = Ejb3Utils.getAllMethods(toInvoke.getClass());
		for (Method aktMethod : methods) {
			Annotation[] aktMethodAnnotations = aktMethod.getAnnotations();
			for (Annotation akt : aktMethodAnnotations) {
				if (akt instanceof PrepareGenerator) {
					this.callMethod(toInvoke, aktMethod);
				}
			}
		}
	}

	/**
	 * Invokes all annotated clean up methods.
	 * 
	 * @CleanupGenerator methods in generator
	 * @param toInvoke - the cleanup method of the generator. 
	 */
	public void callCleanup(Generator toInvoke) {
		Method[] methods = Ejb3Utils.getAllMethods(toInvoke.getClass());
		for (Method aktMethod : methods) {
			Annotation[] aktMethodAnnotations = aktMethod.getAnnotations();
			for (Annotation akt : aktMethodAnnotations) {
				if (akt instanceof CleanupGenerator) {
					this.callMethod(toInvoke, aktMethod);
				}
			}
		}
	}

	/**
	 * The current object to inject.
	 * @param instance
	 *            The instance to set.
	 */
	public void setInstance(Object instance) {
		this.instance = instance;
	}

	/**
	 * The current introspector.
	 * @param introspector
	 *            The introspector to set.
	 */
	public void setIntrospector(Introspector introspector) {
		this.introspector = introspector;
	}

	/**
	 * The property to inject. 
	 * @param property
	 *            The property to set.
	 */
	public void setProperty(Property property) {
		this.property = property;
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

	private void setField(Generator gen, Field inField, Object value) {
		try {
			final Property prop = new Property(inField);
			prop.setField(gen, value);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	private void callMethod(Generator gen, Method method) {
		try {
			method.invoke(gen, (Object[]) null);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

}
