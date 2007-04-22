package com.bm;

import com.bm.data.bo.MyOtherSessionBean;
import com.bm.data.bo.StockWKNBo;
import com.bm.testsuite.BaseSessionBeanFixture;

/**
 * The Session bean.
 * 
 * @author Daniel Wiese
 * @since 08.11.2005
 */
public class MyOtherSessionBeanTest extends BaseSessionBeanFixture<MyOtherSessionBean> {

	private static final Class[] usedBeans = { StockWKNBo.class };


	/**
	 * Constructor.
	 */
	public MyOtherSessionBeanTest() {
		super(MyOtherSessionBean.class, usedBeans);
	}

	/**
	 * Test the dpendency injection.
	 * 
	 * @author Daniel Wiese
	 * @since 08.11.2005
	 */
	public void testDependencyInjection() {
		final MyOtherSessionBean toTest = this.getBeanToTest();
		assertNotNull(toTest);
		assertNotNull(toTest.getDs());
		assertNotNull(toTest.getEm());
		assertNotNull(toTest.getSessionBean());
	}
	
	/**
	 * Test the dpendency injection.
	 * 
	 * @author Daniel Wiese
	 * @since 08.11.2005
	 */
	public void testJndiLookup() {
		final MyOtherSessionBean toTest = this.getBeanToTest();
		assertNotNull(toTest);
		assertNotNull(toTest.getSessionBeanOverJndi());
	}

	

}
