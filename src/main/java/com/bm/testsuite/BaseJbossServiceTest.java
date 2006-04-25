package com.bm.testsuite;

import com.bm.introspectors.JbossServiceIntrospector;

/**
 * Base Jboss-Service test case.
 * 
 * @author Daniel Wiese
 * @param <T> - the type of the service
 * @since 12.11.2005
 */
public class BaseJbossServiceTest<T> extends BaseSessionBeanTest<T> {

	/**
	 * Constructor.
	 * 
	 * @param sessionBeanToTest - to test
	 * @param usedEntityBeans - used 
	 */
	public BaseJbossServiceTest(Class<T> sessionBeanToTest,
			Class[] usedEntityBeans) {
		super(sessionBeanToTest,
				new JbossServiceIntrospector<T>(sessionBeanToTest),
				usedEntityBeans);
	}

}
