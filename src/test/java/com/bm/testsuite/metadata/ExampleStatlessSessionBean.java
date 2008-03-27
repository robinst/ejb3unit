package com.bm.testsuite.metadata;

import javax.ejb.EJB;
import javax.ejb.Stateless;

/**
 * Test seeion bean.
 * 
 * @author Daniel Wiese
 * @since 24.02.2007
 */
@Stateless
public class ExampleStatlessSessionBean implements IBusinessInterface {

	@EJB
	IBusinessInterface2 reference;

	/**
	 * Testmethode.
	 * 
	 * @author Daniel Wiese
	 * @since 24.02.2007
	 * @see com.bm.testsuite.metadata.IBusinessInterface#doSomething()
	 */
	public void doSomething() {
		reference.doSomething();
	}

}
