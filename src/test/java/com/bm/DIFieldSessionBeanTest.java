package com.bm;

import com.bm.data.bo.AnnotatedFieldsSessionBean;
import com.bm.data.bo.StockWKNBo;
import com.bm.testsuite.BaseSessionBeanFixture;

/**
 * The Session bean.
 * 
 * @author Daniel Wiese
 * @since 08.11.2005
 */
public class DIFieldSessionBeanTest extends BaseSessionBeanFixture<AnnotatedFieldsSessionBean> {

	private static final Class[] usedBeans = { StockWKNBo.class };


	/**
	 * Constructor.
	 */
	public DIFieldSessionBeanTest() {
		super(AnnotatedFieldsSessionBean.class, usedBeans);
	}

	/**
	 * Test the dpendency injection.
	 * 
	 * @author Daniel Wiese
	 * @since 08.11.2005
	 */
	public void testDependencyInjection() {
		final AnnotatedFieldsSessionBean toTest = this.getBeanToTest();
		assertNotNull(toTest);
		assertNotNull(toTest.getDs());
		assertNotNull(toTest.getEm());
		assertNotNull(toTest.getSessionBean());
		assertNotNull(toTest.getTimer());
	}
	
	/**
	 * Test the dpendency injection.
	 * 
	 * @author Daniel Wiese
	 * @since 08.11.2005
	 */
	public void testJndiLookup() {
		final AnnotatedFieldsSessionBean toTest = this.getBeanToTest();
		assertNotNull(toTest);
		assertNotNull(toTest.getSessionBeanOverJndi());
	}

	

}
