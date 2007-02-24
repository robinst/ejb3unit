package com.bm.testsuite;

import com.bm.introspectors.JbossServiceIntrospector;
import com.bm.testsuite.fixture.InitialDataSet;

/**
 * Base Jboss-Service test case.
 * 
 * @author Daniel Wiese
 * @param <T> -
 *            the type of the service
 * @since 12.11.2005
 */
public class BaseJbossServiceTest<T> extends BaseSessionBeanTest<T> {

	/**
	 * Constructor.
	 * 
	 * @param sessionBeanToTest -
	 *            to test
	 * @param usedEntityBeans -
	 *            used
	 */
	public BaseJbossServiceTest(Class<T> sessionBeanToTest,
			Class[] usedEntityBeans) {
		super(sessionBeanToTest, new JbossServiceIntrospector<T>(
				sessionBeanToTest), usedEntityBeans);
	}

	/**
	 * Constructor.
	 * 
	 * @param sessionBeanToTest -
	 *            to test
	 * @param usedEntityBeans -
	 *            used
	 * @param initialData -
	 *            the inital data to create in the db
	 */
	public BaseJbossServiceTest(Class<T> sessionBeanToTest,
			Class[] usedEntityBeans, InitialDataSet... initialData) {
		super(sessionBeanToTest, new JbossServiceIntrospector<T>(
				sessionBeanToTest), usedEntityBeans, initialData);
	}

}
