package com.bm.inject;

import java.util.Map;

import org.jmock.Mock;

import com.bm.data.bo.MyOtherSessionBean;
import com.bm.testsuite.mocked.JMockInjectionFactory;

import junit.framework.TestCase;

/**
 * JUnit test case.
 * 
 * @author Daniel Wiese
 * 
 */
public class DependencyInjectorTest extends TestCase {

	/**
	 * Test method.
	 */
	public void testInjector_injectMockObjects() {
		final JMockInjectionFactory defaultFactory = new JMockInjectionFactory();
		final DependencyInjector<Mock> injector = new DependencyInjector<Mock>(
				defaultFactory);

		final MyOtherSessionBean sessionBeanToTest = new MyOtherSessionBean();
		Map<String, Injection<Mock>> back = injector
				.injectDependecies(sessionBeanToTest);
		assertNotNull(sessionBeanToTest.getDs());
		assertNotNull(sessionBeanToTest.getEm());
		assertNotNull(sessionBeanToTest.getSessionBean());

		// test if a mock object for every field exists
		assertNotNull(back.get("manager"));
		assertNotNull(back.get("ds"));
		assertNotNull(back.get("mySessionBean"));

	}

}
