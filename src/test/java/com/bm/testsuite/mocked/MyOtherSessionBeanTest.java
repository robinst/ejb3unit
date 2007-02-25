package com.bm.testsuite.mocked;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.jmock.Mock;

import com.bm.data.bo.MyOtherSessionBean;

/**
 * Testcase.
 * 
 * @author Daniel Wiese
 * 
 */
public class MyOtherSessionBeanTest extends
		MockedSessionBeanFixture<MyOtherSessionBean> {

	/**
	 * Constructor.
	 */
	public MyOtherSessionBeanTest() {
		super(MyOtherSessionBean.class);
	}

	/**
	 * Testmethod.
	 */
	public void test_executeOperation() {
		MyOtherSessionBean toTest = this.getBeanToTest();
		assertNotNull(toTest);
		final Mock mySessionBean = this.getMockControl("mySessionBean");
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
				// TODO Auto-generated method stub
				return 0;
			}

		};

		mySessionBean.expects(once()).method("getDs").will(returnValue(ds));

		// call the expected operation
		toTest.executeOperation();

	}

}
