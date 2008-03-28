package com.bm.testsuite.interfaces;

import org.jmock.Mockery;

/**
 * @author Fabian Bauschulte
 *
 * @param <T>
 */
public interface IMockedSessionBeanFixture<T> {

	/**
	 * Returns the bean to test.
	 * 
	 * @return - the bean to test
	 */
	T getBeanToTest();

	/**
	 * @return
	 */
	Mockery getContext();

	/**
	 * Returns the mock controll to the given property. If this is a dependency
	 * injection field exactly this property will be injected to the session
	 * bean instance
	 * 
	 * @param interfaze -
	 *            the name of the property
	 * @return - the mock controll
	 */

	<M> M getMock(Class<M> interfaze);

	/**
	 * @throws Exception
	 */
	void setUp() throws Exception;

	/**
	 * Sets a value for a field in the tested-bean instance.
	 * 
	 * @author Daniel Wiese
	 * @since 02.05.2006
	 * @param fieldName -
	 *            the name of the field
	 * @param toSet -
	 *            the value to set
	 */
	void setValueForField(String fieldName, Object toSet);
	
	/**
	 * @throws Exception
	 */
	void tearDown() throws Exception;

}
