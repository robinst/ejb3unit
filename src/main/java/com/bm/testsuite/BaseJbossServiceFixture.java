package com.bm.testsuite;

import com.bm.introspectors.JbossServiceIntrospector;
import com.bm.testsuite.dataloader.InitialDataSet;

/**
 * Base Jboss-Service test case.
 * 
 * @author Daniel Wiese
 * @param <T> -
 *            the type of the service
 * @since 12.11.2005
 */
public abstract class BaseJbossServiceFixture<T> extends BaseSessionBeanFixture<T> {

	/**
	 * Constructor.
	 * 
	 * @param sessionBeanToTest -
	 *            to test
	 * @param usedEntityBeans -
	 *            used
	 */
	public BaseJbossServiceFixture(Class<T> sessionBeanToTest,
			Class<?>[] usedEntityBeans) {
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
	public BaseJbossServiceFixture(Class<T> sessionBeanToTest,
			Class<?>[] usedEntityBeans, InitialDataSet... initialData) {
		super(sessionBeanToTest, new JbossServiceIntrospector<T>(
				sessionBeanToTest), usedEntityBeans, initialData);
	}

}
