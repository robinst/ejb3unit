package com.bm.testsuite.metadata;

import javax.ejb.Stateless;

/**
 * Test session bean.
 * 
 * @author Daniel Wiese
 * @since 24.02.2007
 */
@Stateless
public class ExampleStatelessSessionBean2 implements IBusinessInterface2 {

	/**
	 * Testmethode.
	 * 
	 * @author Daniel Wiese
	 * @since 24.02.2007
	 * @see com.bm.testsuite.metadata.IBusinessInterface2#doSomething()
	 */
	public void doSomething() {
		System.out.println("Halooooo...");

	}

}
