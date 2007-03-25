package com.bm.introspectors;

import java.lang.annotation.Annotation;

import javax.persistence.Embeddable;

import org.apache.log4j.Logger;

/**
 * This class inspects all relevant fields of an embedded class and holds the
 * information.
 * 
 * @author Daniel Wiese
 * @param <T> -
 *            the type of the embedded class
 * @since 07.10.2005
 */
public class EmbeddedClassIntrospector<T> extends AbstractPersistentClassIntrospector<T>
		implements Introspector<T> {

	private static final Logger log = Logger
			.getLogger(EmbeddedClassIntrospector.class);

	/** the type of the ebmedded class* */
	private final Class<T> embeddedClassName;

	/** the name of the attribute in the bean class holding the embedded class* */
	private final Property attibuteName;

	/**
	 * Constructor with the class to inspect.
	 * 
	 * @param toInspect -
	 *            the class to inspect
	 */
	@SuppressWarnings("unchecked")
	public EmbeddedClassIntrospector(Property toInspect) {
		this.embeddedClassName = toInspect.getType();
		this.attibuteName = toInspect;

		Annotation[] classAnnotations = this.embeddedClassName.getAnnotations();
		boolean isEbeddeable = false;

		// iterate over the annotations
		for (Annotation a : classAnnotations) {
			if (a instanceof Embeddable) {

				isEbeddeable = true;
			}
		}

		// check for mandatory conditions
		if (!isEbeddeable) {
			log.error("The class " + this.embeddedClassName.getSimpleName()
					+ " is not a ebededable class");
			throw new RuntimeException("The class "
					+ this.embeddedClassName.getSimpleName()
					+ " is not a ebededable class");
		}

		// TODO currently abbeded classses have always field access
		this.processAccessTypeField(this.embeddedClassName);
	}

	/**
	 * Returns the embeddedClassName.
	 * 
	 * @return Returns the embeddedClassName.
	 */
	public Class getEmbeddedClassName() {
		return embeddedClassName;
	}

	/**
	 * Returns the filed/gett/setter Name of the source class.
	 * 
	 * @return Returns the attibuteName.
	 */
	public Property getAttibuteName() {
		return attibuteName;
	}

}
