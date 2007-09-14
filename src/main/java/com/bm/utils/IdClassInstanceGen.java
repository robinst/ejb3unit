package com.bm.utils;

import java.lang.reflect.Field;
import java.util.Set;

import com.bm.introspectors.Property;

/**
 * Creates an instance of a id class.
 * 
 * @author Daniel Wiese
 * 
 */
public class IdClassInstanceGen {

	private Object newInstance;

	public IdClassInstanceGen(Set<Property> idFields, Class<?> idClass, Object entityBean) {
		try {
			newInstance = idClass.newInstance();
			for (Property property : idFields) {
				Object fieldValue = property.getField(entityBean);
				Field declaredField = idClass
						.getDeclaredField(property.getPropertyName());
				declaredField.setAccessible(true);
				declaredField.set(newInstance, fieldValue);
			}
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (SecurityException e) {
			throw new RuntimeException(e);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		}

	}

	/**
	 * Getter to return the newInstance.
	 * 
	 * @return the newInstance
	 */
	public Object getIDClassIntance() {
		return newInstance;
	}

}
