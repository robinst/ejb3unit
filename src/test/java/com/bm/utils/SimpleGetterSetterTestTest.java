package com.bm.utils;

import com.bm.ejb3data.bo.StockWKNBo;

import junit.framework.TestCase;

/**
 * Testclass.
 * 
 * @author Daniel Wiese
 * @since 17.04.2006
 */
public class SimpleGetterSetterTestTest extends TestCase {

	/**
	 * Junit test.
	 * 
	 * @author Daniel Wiese
	 * @since 29.06.2006
	 */
	public void testSimpleGetterSetter() {
		StockWKNBo bean = new StockWKNBo(870737);
		final SimpleGetterSetterTest toTest = new SimpleGetterSetterTest(bean);
		toTest.testGetterSetter();
	}

}
