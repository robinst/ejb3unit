package com.bm;

import com.bm.ejb3data.bo.InterceptedSessionBean;
import com.bm.testsuite.BaseSessionBeanFixture;

/**
 * Tests correct analysis of interceptor annotations. 
 */
public class InterceptedSessionBeanTest extends BaseSessionBeanFixture<InterceptedSessionBean> {

	public InterceptedSessionBeanTest() {
		super(InterceptedSessionBean.class, new Class[0]); 
	}

}
