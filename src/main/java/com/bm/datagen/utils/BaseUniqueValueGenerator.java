package com.bm.datagen.utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.bm.introspectors.Introspector;
import com.bm.introspectors.Property;

/**
 * Hleper class which helps to generate unique values.
 * 
 * @author Daniel Wiese
 * 
 * @param <T>
 */
public abstract class BaseUniqueValueGenerator<T> {

	private static final int NUMBER_OF_TRIES = 300;

	private Map<Property, Set<T>> generated = new HashMap<Property, Set<T>>();

	/**
	 * Generates a unique value for each field (if the field is a PK field)
	 * 
	 * @param forProperty
	 *            -the field
	 * @param introspector -
	 *            the introspector
	 * @return - a unique value
	 */
	protected T getUniqueValueForEachPkField(Property forProperty,
			Introspector<? extends Object> introspector) {

		if (!introspector.getPkFields().contains(forProperty)) {
			// the field is not a pk field
			return this.generateCadidate();
		} else {
			// the field is a pk field
			boolean done = false;
			int count = 0;
			while (!done && count < NUMBER_OF_TRIES) {
				final T act = this.generateCadidate();
				if (!alreadyGenerated(forProperty, act)) {
					done = true;
					return act;
				}
			}
			throw new RuntimeException(
					"Cant generate a unique value for the field: "
							+ forProperty.getName());
		}
	}

	/**
	 * This method genrates a cadidate - thiscandidate will be automatcally
	 * checked for uniqueness
	 * 
	 * @return - a cadidate
	 */
	protected abstract T generateCadidate();

	/**
	 * Returns trueif the value wasnot generated yet (for the field)
	 * 
	 * @param forProperty -
	 *            the field
	 * @param toCheck
	 *            -the value to check
	 * @return false if not generated yet
	 */
	private boolean alreadyGenerated(Property forProperty, T toCheck) {
		if (generated.containsKey(toCheck)) {
			final Set<T> currentSet = generated.get(toCheck);
			if (currentSet.contains(toCheck)) {
				return true;
			} else {
				currentSet.add(toCheck);
				return false;
			}
		} else {
			final Set<T> newSet = new HashSet<T>();
			newSet.add(toCheck);
			generated.put(forProperty, newSet);
			return false;
		}
	}

}
