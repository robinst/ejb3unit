package com.bm.introspectors.relations;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.bm.introspectors.PrimaryKeyInfo;
import com.bm.introspectors.Property;

/**
 * Global store for storing the primary key properties of entity classes.
 * Such a global store is necessary to avoid cyclic dependencies while processing relations.
 * 
 * @author Peter Doornbosch
 */
public class GlobalPrimaryKeyStore {

	private static final GlobalPrimaryKeyStore singleton = new GlobalPrimaryKeyStore();

	private Map<Class, Map<Property, PrimaryKeyInfo>> store = new HashMap<Class, Map<Property, PrimaryKeyInfo>>();

	private GlobalPrimaryKeyStore() {
		// singleton constructor
	}

	/**
	 * Returns the singleton instance.
	 * 
	 * @return - the singleton instance
	 */
	public static GlobalPrimaryKeyStore getStore() {
		return singleton;
	}
	
	/**
	 * Stores primary key info of a given entity class.	
	 * @param entityClass	the entity class
	 * @param pkFieldInfo	the primary key property and field info; usually the map will contain
	 * just one entry, but it might contain more entries in case of a composite primary key.
	 */
	public void put(Class entityClass, Map<Property, PrimaryKeyInfo> pkFieldInfo) {
		store.put(entityClass, pkFieldInfo);
	}
	
	/**
	 * Retrieves primary key info.  
	 * @param entityClass
	 * @return	one or more (in case of a composite primary key) properties that specify the
	 * primary key(s), or null if no primary key info is registered for the given class.
	 */
	public  Set<Property> getPrimaryKeyInfo(Class entityClass) {
		Map<Property, PrimaryKeyInfo> pkInfo = store.get(entityClass);
		if (pkInfo == null) {
			return null;
		}
		else {
			return pkInfo.keySet();
		}
	}
}
