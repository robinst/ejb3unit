package com.bm.utils;

import java.util.List;

import org.apache.log4j.Logger;

import com.bm.introspectors.Introspector;
import com.bm.introspectors.PersistentPropertyInfo;
import com.bm.introspectors.Property;

/**
 * Sets nullable fields to null.
 * 
 * @author Daniel Wiese
 * @since 16.10.2005
 */
public final class NullableSetter {

	private static final Logger log = Logger.getLogger(NullableSetter.class);

	private NullableSetter() {
		// intetinally empty
	}

	/**
	 * Sets nullable fields to null.
	 * 
	 * @author Daniel Wiese
	 * @since 16.10.2005
	 * @param <T> -
	 *            the type of the bean
	 * @param beans -
	 *            collection of beans
	 * @param intro -
	 *            the introspector of the bean class
	 */
	public static <T> void setFieldsToNull(List<T> beans, Introspector<T> intro) {

		// iterate over all beans
		for (T aktBean : beans) {
			setFieldsToNull(aktBean, intro);
		}
	}

	/**
	 * Sets nullable fields to null.
	 * 
	 * @author Daniel Wiese
	 * @since 16.10.2005
	 * @param <T> -
	 *            the type of the bean
	 * @param bean -
	 *            the bean
	 * @param intro -
	 *            the introspector of the bean class
	 */
	public static <T> void setFieldsToNull(T bean, Introspector<T> intro) {
		List<Property> fields = intro.getPersitentProperties();

		for (Property aktProperty : fields) {
			final PersistentPropertyInfo pfi = intro
					.getPresistentFieldInfo(aktProperty);
			// pk fields and primitives are by default not nullable
			if (pfi.isNullable() && !aktProperty.getType().isPrimitive()
					&& !intro.getPkFields().contains(aktProperty)) {
				try {
					intro.setField(bean, aktProperty, null);
				} catch (IllegalAccessException e) {
					log.error("Can´t set the field " + aktProperty.getName()
							+ " to null");
					throw new RuntimeException("Can´t set the field "
							+ aktProperty.getName() + " to null");
				}
			}
		}
	}

}
