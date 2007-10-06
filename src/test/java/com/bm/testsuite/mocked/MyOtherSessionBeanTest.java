package com.bm.testsuite.mocked;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.jmock.Mock;

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
		final Mock mySessionBean = this.getMockControl(IMySessionBean.class);
		assertNotNull(mySessionBean);

		final DataSource ds = new DataSource() {

			public Connection getConnection() throws SQLException {
				return null;
			}

			public Connection getConnection(String arg0, String arg1)
					throws SQLException {
				return null;
			}

			public PrintWriter getLogWriter() throws SQLException {
				return null;
			}

			public void setLogWriter(PrintWriter arg0) throws SQLException {

			}

			public void setLoginTimeout(int arg0) throws SQLException {

			}

			public int getLoginTimeout() throws SQLException {
				return 0;
			}

			@SuppressWarnings("unused")
			public boolean isWrapperFor(Class<?> arg0) throws SQLException {
				return false;
			}

			@SuppressWarnings("unused")
			public <T> T unwrap(Class<T> arg0) throws SQLException {
				return null;
			}

		};

		mySessionBean.expects(once()).method("getDs").will(returnValue(ds));

		// call the expected operation
		toTest.executeOperation();

	}

}
