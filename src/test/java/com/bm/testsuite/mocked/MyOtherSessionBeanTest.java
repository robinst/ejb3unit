package com.bm.testsuite.mocked;

import javax.sql.DataSource;

import org.jmock.Expectations;

import com.bm.ejb3data.bo.AnnotatedFieldsSessionBean;
import com.bm.ejb3data.bo.IMySessionBean;

/**
 * Testcase.
 * 
 * @author Daniel Wiese
 * 
 */
public class MyOtherSessionBeanTest extends
		MockedSessionBeanFixture<AnnotatedFieldsSessionBean> {

	/**
	 * Constructor.
	 */
	public MyOtherSessionBeanTest() {
		super(AnnotatedFieldsSessionBean.class);
	}

	/**
	 * Testmethod.
	 */
	public void test_executeOperation() { 
		AnnotatedFieldsSessionBean toTest = this.getBeanToTest();
		assertNotNull(toTest);
		final IMySessionBean mySessionBean = getMock(IMySessionBean.class);
		assertNotNull(mySessionBean); 

		final DataSource ds = getMock(DataSource.class);

		getContext().checking(new Expectations() {{
			atLeast(1).of(mySessionBean).getDs();
			will(returnValue(ds));
			allowing(ds);
	    }});
		
		
		// call the expected operation
		toTest.executeOperation();

	}

}
