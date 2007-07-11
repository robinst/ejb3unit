package com.bm.creators;

import java.util.Map;

import com.bm.ejb3guice.inject.Binder;
import com.bm.ejb3guice.inject.Module;

public class DynamicDIModuleCreator implements Module{
	
	private final Map<String, String> interface2implemantation;

	/**
	 * Constructor.
	 * @param interface2implemantation the set of interfact/imlementation pairs for the 
	 * current jar file
	 * Known limitation: Sassion beans can depend only on session beans in the same 
	 * jar file, maybe update always this map it a interface is not found!
	 */
	public DynamicDIModuleCreator(Map<String, String> interface2implemantation) {
		this.interface2implemantation = interface2implemantation;
		// TODO Auto-generated constructor stub
	}

	public void configure(Binder binder) {
		// TODO Auto-generated method stub
		
	}

}
