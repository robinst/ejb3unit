package com.bm.testsuite.interfaces;

import com.bm.jndi.Ejb3UnitJndiBinder;

/**
 * @author Fabian Bauschulte
 *
 */
public interface IBaseFixture {
	

	Ejb3UnitJndiBinder getJndiBinder();

	/**
	 * @author Daniel Wiese
	 * @since 16.10.2005
	 * @see junit.framework.TestCase#setUp()
	 */
	void setUp() throws Exception;
	
	/**
	 * @param forObject
	 * @param fieldName
	 * @param toSet
	 */
	void setValueForField(Object forObject, String fieldName,
			Object toSet) ;
	
	/**
	 * @throws Exception
	 */
	void tearDown() throws Exception;

}
