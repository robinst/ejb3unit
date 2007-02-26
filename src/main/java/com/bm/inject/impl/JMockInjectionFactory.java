package com.bm.inject.impl;

import org.jmock.Mock;
import org.jmock.core.CoreMock;
import org.jmock.core.Formatting;

import com.bm.inject.DependencyInjectionFactory;
import com.bm.inject.Injection;

/**
 * This factory creates JMock objects to anable isolated bean testing.
 * 
 * @author Daniel Wiese
 * 
 */
public class JMockInjectionFactory implements DependencyInjectionFactory<Mock> {

	/**
	 * Gets called if the injector has identified a field or method for
	 * dependency injection.
	 * @param type the type to inject
	 * @see com.bm.inject.DependencyInjectionFactory#create(java.lang.Class)
	 * @return the specified injection - mock object
	 */
	public Injection<Mock> create(Class type) {
		final String roleName = "mock" + Formatting.classShortName(type);
		Mock newMock = new Mock(new CoreMock(type, roleName));

		// create the proxy
		final Object proxy = newMock.proxy();
		// return the injection
		return new MockInjection(proxy, newMock);
	}

	/**
	 * Implementation of a Mock object injection.
	 * 
	 * @author Daniel Wiese
	 * 
	 */
	public static class MockInjection implements Injection<Mock> {
		private final Object createdObject;

		private final Mock cratedMockControl;

		/**
		 * Default constructor.
		 * 
		 * @param createdObject -
		 *            the crated object
		 * @param cratedMockControl -
		 *            the created mock conntrol.
		 */
		public MockInjection(Object createdObject, Mock cratedMockControl) {
			this.cratedMockControl = cratedMockControl;
			this.createdObject = createdObject;
		}

		/**
		 * Returns the created object.
		 * @return the created object
		 * @see com.bm.inject.Injection#getCratedObject()
		 */
		public Object getCratedObject() {
			return this.createdObject;
		}

		/**
		 * Returns a handle for the previosly crated object. A handle can be any
		 * object of type H. One use case if for example a Mock controll object.
		 * @return the mock handle
		 * @see com.bm.inject.Injection#getHandle()
		 */
		public Mock getHandle() {
			return this.cratedMockControl;
		}

	}

}
