package com.bm.testsuite.metadata;

import javax.ejb.Remote;

/**
 * Testinterface.
 * @author Daniel Wiese
 * @since 24.02.2007
 */
@Remote
public interface IBusinessInterface {
	
	/**
	 * Testmethod.
	 */
	void doSomething();

}
