package com.bm.testsuite.metadata;

import javax.ejb.EJB;

import org.jboss.annotation.ejb.Service;

/**
 * Testklasse.
 * 
 * @author Daniel Wiese
 * @since 24.02.2007
 */
@Service
public class TestServiceBean implements IBusinessInterface3 {

	@EJB
	IBusinessInterface2 reference;

	/**
	 * Testmethode.
	 * 
	 * @author Daniel Wiese
	 * @since 24.02.2007
	 * @see com.bm.testsuite.metadata.IBusinessInterface3#doSomething()
	 */
	public void doSomething() {
		reference.doSomething();
	}

}
