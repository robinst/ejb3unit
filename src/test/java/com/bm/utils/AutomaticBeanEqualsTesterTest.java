package com.bm.utils;

import junit.framework.TestCase;

import com.bm.data.bo.LineItem;

/**
 * JUnit test.
 * 
 * @author Daniel Wiese
 * @since 09.02.2006
 */
public class AutomaticBeanEqualsTesterTest extends TestCase {

	/**
	 * Test method.
	 * 
	 * @author Daniel Wiese
	 * @since 09.02.2006
	 */
	public void testEqualsImplementation_happyPath() {
		AutomaticBeanEqualsTester.assertEqualsImplementation(LineItem.class,
				"subtotal", "id", "product", "quantity");
	}

}
