package com.bm.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * This class will find out a access type (FIELD, METHOD).
 * 
 * @author Daniel Wiese
 * 
 */
public final class AccessTypeFinder {

	private static final org.apache.log4j.Logger log = org.apache.log4j.Logger
			.getLogger(AccessTypeFinder.class);

	/**
	 * Constructor.
	 */
	private AccessTypeFinder() {
		// intentionally left blank
	}

	/**
	 * Finds the accesstype.
	 * 
	 * @param toFind -
	 *            the access type to find
	 * @return the accesstype
	 */
	public static AccessType findAccessType(Class toFind) {
		AccessType back = null;

		Field[] fields = toFind.getDeclaredFields();
		for (Field aktField : fields) {
			Annotation[] fieldAnnotations = aktField.getAnnotations();

			// look into the annotations
			for (Annotation a : fieldAnnotations) {
				if (a instanceof Id) {
					back = AccessType.FIELD;
					break;
				} else if (a instanceof EmbeddedId) {
					back = AccessType.FIELD;
					break;
				}
			}
		}
		
		if (back == null) {
			// Not found: try super class (if entity inheritance is used).
			// Specification 2.1.1: "A single access type applies to an entity hierarchy"
			Class superClass = toFind.getSuperclass();
			if (superClass != null && (superClass.getAnnotation(Entity.class) != null 
					                   || superClass.getAnnotation(MappedSuperclass.class) != null)) {
				return findAccessType(superClass);
			}
		}
		
		if (back == null) {
			// default access type
			back = AccessType.METHOD;
		}
		
		log.debug("Accesstype: " + back);
		return back;
	}

}
