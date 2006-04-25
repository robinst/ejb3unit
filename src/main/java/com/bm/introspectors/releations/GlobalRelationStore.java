package com.bm.introspectors.releations;

import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.bm.introspectors.Property;

/**
 * The problems in finding releations using introspectors is to avaid cyclic
 * dependencies. This singelton is used as such a store of properties
 * (OneToMany, OneToOne,...) to avoid such dependencies
 * 
 * @author Daniel Wiese
 * 
 */
public final class GlobalRelationStore {

	private static final GlobalRelationStore singelton = new GlobalRelationStore();

	private Map<Class, Set<Property>> store = new HashMap<Class, Set<Property>>();

	private GlobalRelationStore() {
		// singelton constructor
	}

	/**
	 * Returns the singelton instance.
	 * 
	 * @return - the singelton instance
	 */
	public static GlobalRelationStore getStore() {
		return singelton;
	}

	/**
	 * Save a property wich represents a relation in a class (bean).
	 * 
	 * @param forClass -
	 *            the entity bean class
	 * @param toSave -
	 *            the property to save
	 */
	public void put(Class forClass, Property toSave) {
		if (store.containsKey(forClass)) {
			final Set<Property> tmp = store.get(forClass);
			tmp.add(toSave);
		} else {
			final Set<Property> tmp = new HashSet<Property>();
			tmp.add(toSave);
			store.put(forClass, tmp);
		}
	}

	/**
	 * Returns the property based on the class and the name.
	 * 
	 * @param forClass -
	 *            the class where the property is in
	 * @param propertyName -
	 *            the name of the property
	 * @return - the property or null (if not found)
	 */
	public Property getProperty(Class forClass, String propertyName) {

		if (store.containsKey(forClass)) {
			final Set<Property> tmp = store.get(forClass);
			for (Property akt : tmp) {
				if (akt.getName().equals(propertyName)) {
					return akt;
				}
			}
		}

		return null;
	}

	/**
	 * Returns the releation properties for special class (entity bean).
	 * 
	 * @param forClass -
	 *            the class where the property is in
	 * @param forType -
	 *            the type of the property
	 * @return - the property or null (if not found)
	 */
	public Property getProperty(Class forClass, Class forType) {

		if (store.containsKey(forClass)) {
			final Set<Property> tmp = store.get(forClass);
			for (Property akt : tmp) {
				// e.g clollections> we are searching the typed
				if (akt.getType().equals(forType)
						|| this.isGenericTypeEqual(akt, forType)) {
					return akt;
				}
			}
		}

		return null;
	}

	private boolean isGenericTypeEqual(Property aktProperty, Class clazz) {
		if (aktProperty.getGenericType() instanceof ParameterizedType) {
			final ParameterizedType type = (ParameterizedType) aktProperty
					.getGenericType();
			if (type.getActualTypeArguments().length == 1) {
				Class<Object> ty = (Class<Object>) type
						.getActualTypeArguments()[0];
				if (ty.equals(clazz)) {
					return true;
				}
			}
		}
		return false;
	}

}
