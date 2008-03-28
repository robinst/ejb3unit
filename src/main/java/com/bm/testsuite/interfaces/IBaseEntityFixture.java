package com.bm.testsuite.interfaces;

/**
 * @author Fabian Bauschulte
 *
 * @param <T>
 */
public interface IBaseEntityFixture<T> extends IBaseFixture{

	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	void setUp() throws Exception;

	/**
	 * @see junit.framework.TestCase#tearDown()
	 */
	void tearDown() throws Exception;

	/**
	 * This test tests the simpe getter-/ setter methods.
	 * 
	 */
	void testGetterSetter();

	/**
	 * This test writes n random generated beans into the database.
	 * 
	 * @throws Exception -
	 *             in an error case
	 */
	void testWrite() throws Exception;

	/**
	 * This test writes n random generated beans into the database Reads all
	 * beans agin form the database and test if the reded beans are equal 1) by
	 * introspectin all persistent fields 2) by callung the eqals method.
	 * 
	 * Additionaly the hash-code implementation will be checked
	 * 
	 * @throws Exception -
	 *             in an error case
	 */
	void testWriteRead() throws Exception;

	/**
	 * This test writes n random generated beans into the database - all
	 * null-able fields are set to null.
	 * 
	 * @throws Exception -
	 *             in an error case
	 */
	void testWriteWithNullFields() throws Exception;

}