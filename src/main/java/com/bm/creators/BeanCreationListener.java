package com.bm.creators;

import java.util.HashSet;
import java.util.Set;

import com.bm.ejb3guice.inject.CreationListner;

/**
 * Protokolls which objects are created.
 * @author wiesda00
 *
 */
public class BeanCreationListener implements CreationListner {

	private Set<Object> createdBeans = new HashSet<Object>();

	/**
	 * Will be called by ejb3guice after creation.
	 */
	public void afterCreation(Object obj) {
		createdBeans.add(obj);

	}

	/**
	 * Returns the created beans.
	 * @return the created beans.
	 */
	public Set<Object> getCreatedBeans() {
		return createdBeans;
	}
	
	

}
