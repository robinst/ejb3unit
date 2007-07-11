package com.bm.ejb3guice.inject;

public interface CreationListner {
	
	/**
	 * Will be called every time after a Object is created.
	 * @param obj the created object
	 */
	void afterCreation(Object obj);

}
