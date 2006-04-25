package com.bm.utils;

import com.bm.data.bo.StockWKNBo;

import junit.framework.TestCase;

/**
 * Testclass.
 * @author Daniel Wiese
 * @since 17.04.2006
 */
public class SimpleGetterSetterTestTest extends TestCase {
	
	public void testSimpleGetterSetter(){
		StockWKNBo bean=new StockWKNBo(870737);
		final SimpleGetterSetterTest toTest=new SimpleGetterSetterTest(bean);
		toTest.testGetterSetter();
	}

}
