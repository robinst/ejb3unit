package com.bm.inject;


import com.bm.testsuite.BaseSessionBeanFixture;


/**
 * Test to evaluate issue 2802736. 
 */
public class InjectMailSessionTest extends BaseSessionBeanFixture<MailSessionTestServiceBean>
{
	public InjectMailSessionTest() {
		super(MailSessionTestServiceBean.class, new Class[] {});
	}
	
}
