package com.bm.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import com.bm.datagen.utils.BaseRandomDataGenerator;
import com.bm.introspectors.Property;

/**
 * This class tests simple Bean-Classes automatically. Every Bean class must
 * have a parameterless constructor. We are using a random data generator
 * automatically generate N beans.
 * 
 * @author Daniel Wiese
 * @since 09.02.2006
 */
public final class AutomaticBeanEqualsTester extends Assert {

	/**
	 * Executes the automatic eauals test.
	 * 
	 * @author Daniel Wiese
	 * @since 09.02.2006
	 * @param <T> -
	 *            the type of the bean
	 * @param beanToTest -
	 *            the class of the bean
	 * @param keyProperties -
	 *            the names of the key properties, which are important for equal
	 */
	public static <T> void assertEqualsImplementation(
			Class<? extends T> beanToTest, String... keyProperties) {
		final List<Property> fields = getClassKeyProperties(beanToTest,
				keyProperties);
		final Object[] lastGeneratedValues = new Object[fields.size()];
		for (int i = 0; i < 50; i++) {
			try {
				final T instance = beanToTest.newInstance();
				// set the key properties randomly
				for (int j = 0; j < fields.size(); j++) {
					final Property current = fields.get(j);
					lastGeneratedValues[j] = setRandomValue(instance, current);
				}
				// create equal clone
				final T cloneInstance = beanToTest.newInstance();
				copyProperties(cloneInstance, fields, lastGeneratedValues);

				// now change k times exactly one property value (k=key property
				// size)
				for (int k = 0; k < fields.size(); k++) {
					final T differentInstance = beanToTest.newInstance();
					copyProperties(differentInstance, fields,
							lastGeneratedValues);
					changeOnePropertyValue(differentInstance,
							lastGeneratedValues, fields, k);

					// NOW DO THE MAIN EQUALS TEST
					new EqualsTester(instance, cloneInstance,
							differentInstance, null, false);
				}

			} catch (InstantiationException e) {
				fail("Can´t instatiate bean :" + e.getMessage());
			} catch (IllegalAccessException e) {
				fail("Can´t instatiate bean :" + e.getMessage());
			}
		}

	}

	private static <T> void copyProperties(T cloneInstance,
			List<Property> fields, Object[] lastGeneratedValues) {
		for (int j = 0; j < fields.size(); j++) {
			final Property current = fields.get(j);
			try {
				current.setField(cloneInstance, lastGeneratedValues[j]);
			} catch (IllegalAccessException e) {
				fail("Cant set the value (" + lastGeneratedValues[j]
						+ ") of property (" + current + ")");
			}
		}
	}

	private static <T> void changeOnePropertyValue(T differentInstance,
			Object[] lastGeneratedValues, List<Property> fields,
			int indexToChange) {
		// try to change 100 times (100 random values) and hope one is different
		boolean changeSuccessfull = false;
		for (int i = 0; i < 100; i++) {
			final Property current = fields.get(indexToChange);
			final Object newValue = setRandomValue(differentInstance, current);
			if (!newValue.equals(lastGeneratedValues[indexToChange])) {
				changeSuccessfull = true;
				break;
			}
		}

		if (!changeSuccessfull) {
			fail("Can´t generate a object with one different value");
		}

	}

	private static <T> List<Property> getClassKeyProperties(
			Class<? extends T> beanToTest, String... keyProperties) {
		final Set<String> propertiesSet = new HashSet<String>(Arrays
				.asList(keyProperties));
		final List<Property> back = new ArrayList<Property>();
		Field[] fields = Ejb3Utils.getAllFields(beanToTest);
		for (Field current : fields) {
			final String name = current.getName();
			if (propertiesSet.contains(name)) {
				// the field is a key property
				Property toAdd = new Property(current);
				back.add(toAdd);
			}
		}
		return back;
	}

	private static <T> Object setRandomValue(T instance, Property forField) {
		Object back = null;
		if (Ejb3Utils.getNonPrimitiveType(forField) == String.class) {
			back = BaseRandomDataGenerator.getValueString(255, false);
		} else if (Ejb3Utils.getNonPrimitiveType(forField) == Date.class) {
			back = BaseRandomDataGenerator.getValueDate();
		} else if (Ejb3Utils.getNonPrimitiveType(forField) == Boolean.class) {
			back = BaseRandomDataGenerator.getValueBoolean();
		} else if (Ejb3Utils.getNonPrimitiveType(forField) == Character.class) {
			back = BaseRandomDataGenerator.getValueChar();
		} else if (Ejb3Utils.getNonPrimitiveType(forField) == Double.class) {
			back = BaseRandomDataGenerator.getValueDouble();
		} else if (Ejb3Utils.getNonPrimitiveType(forField) == Float.class) {
			back = BaseRandomDataGenerator.getValueFloat();
		} else if (Ejb3Utils.getNonPrimitiveType(forField) == Integer.class) {
			back = BaseRandomDataGenerator.getValueInt();
		} else if (Ejb3Utils.getNonPrimitiveType(forField) == Long.class) {
			back = BaseRandomDataGenerator.getValueLong();
		} else if (Ejb3Utils.getNonPrimitiveType(forField) == Short.class) {
			back = BaseRandomDataGenerator.getValueShort();
		}

		if (back != null) {
			try {
				forField.setField(instance, back);
			} catch (IllegalAccessException e) {
				fail("Cant set the random value (" + back + ") of property ("
						+ forField + ")");
			}
		} else {
			// back is null
			fail("Cant create bean instance, because the field ("
					+ forField.getName() + ") of type (" + forField.getType()
					+ ") is currently not supported");
		}

		return back;
	}

}
