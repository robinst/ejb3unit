package com.bm.junit4;

import static junit.framework.Assert.assertNotNull;

import javax.sql.DataSource;

import org.jmock.Expectations;
import org.junit.Test;

import com.bm.ejb3data.bo.AnnotatedFieldsSessionBean;
import com.bm.ejb3data.bo.IMySessionBean;
import com.bm.testsuite.junit4.MockedSessionBeanJUnit4Fixture;

public class MyOtherSessionBeanJUnit4Test extends
		MockedSessionBeanJUnit4Fixture<AnnotatedFieldsSessionBean> {

	public MyOtherSessionBeanJUnit4Test() {
		super(AnnotatedFieldsSessionBean.class);

	}

	/**
	 * Testmethod.
	 */
	@Test
	public void executeOperation() {
		AnnotatedFieldsSessionBean toTest = this.getBeanToTest();

		assertNotNull(toTest);
		final IMySessionBean mySessionBean = getMock(IMySessionBean.class);
		assertNotNull(mySessionBean);

		final DataSource ds = getMock(DataSource.class);

		getContext().checking(new Expectations() {
			{
				atLeast(1).of(mySessionBean).getDs();
				will(returnValue(ds));
				allowing(ds);
			}
		});

		// call the expected operation
		toTest.executeOperation();

	}

}
