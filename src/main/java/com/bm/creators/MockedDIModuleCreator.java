package com.bm.creators;

import com.bm.ejb3guice.inject.Binder;
import com.bm.ejb3guice.inject.Module;

public class MockedDIModuleCreator implements Module {

	/**
	 * Constructor.
	 * 
	 * @param manager
	 *            the entity manager instance which should be used for the
	 *            binding
	 */
	public MockedDIModuleCreator() {

	}

	@SuppressWarnings("unchecked")
	public void configure(Binder binder) {
		// static standard bindings
		// FIXME: implement

	}
}
