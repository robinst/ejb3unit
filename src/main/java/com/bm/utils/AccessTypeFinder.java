package com.bm.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import javax.persistence.EmbeddedId;
import javax.persistence.Id;

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
	 * Consructor.
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
		AccessType back = AccessType.METHOD;

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
		// default access type
		log.debug("Accesstype: " + back);
		return back;
	}

}
