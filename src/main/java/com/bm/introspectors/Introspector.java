package com.bm.introspectors;

import java.util.List;
import java.util.Set;

/**
 * This interface represents different introspectors like
 * EntityBeanIntrospector.
 * 
 * @author Daniel Wiese
 * @param <T> -
 *            the type of the inspected class
 * @since 07.10.2005
 */
public interface Introspector<T> {

	/**
	 * This method returns informations about a peristent field.
	 * 
	 * @param toCheck -
	 *            the field to check
	 * @return - the information about the field
	 */
	PersistentPropertyInfo getPresistentFieldInfo(Property toCheck);

	/**
	 * This method returns informations about a primary key field.
	 * 
	 * @param toCheck -
	 *            the field to check
	 * @return - the information about the field
	 */
	PrimaryKeyInfo getPrimaryKeyInfo(Property toCheck);

	/**
	 * Returns the persistent fields.
	 * 
	 * @return Returns the persitentFields.
	 */
	List<Property> getPersitentFields();

	/**
	 * Return the primary key fields.
	 * 
	 * @return Returns the pkFields.
	 */
	Set<Property> getPkFields();

	/**
	 * Returns a value of an field.
	 * 
	 * @param instance -
	 *            the instance
	 * @param toGet -
	 *            the field to read the value
	 * @return - the readed value
	 * @throws IllegalAccessException -
	 *             in error case
	 */
	Object getField(T instance, Property toGet) throws IllegalAccessException;

	/**
	 * Sets a value of an field.
	 * 
	 * @param instance -
	 *            the instance
	 * @param toSet -
	 *            the field to set the value
	 * @param value -
	 *            the new value
	 * @throws IllegalAccessException -
	 *             in error case
	 */
	void setField(T instance, Property toSet, Object value)
			throws IllegalAccessException;

}
