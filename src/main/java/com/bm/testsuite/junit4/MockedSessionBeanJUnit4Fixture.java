package com.bm.testsuite.junit4;

import org.jmock.Mockery;
import org.junit.After;
import org.junit.Before;

import com.bm.testsuite.interfaces.IMockedSessionBeanFixture;
import com.bm.testsuite.mocked.MockedSessionBeanFixture;

/**
 * JUnit4 enabled version of MockedSessionBeanJUnit4Fixture (JUnit 3.x).
 * @author Fabian Bauschulte
 *
 * @param <T>
 */
public class MockedSessionBeanJUnit4Fixture<T> implements
		IMockedSessionBeanFixture<T> {

	/**
	 * Helperclass. Needed because MockedSessionBeanFixture is abstract.
	 * @author Fabian Bauschulte
	 *
	 */
	private final class MockedSessionBeanFixtureExtension extends
			MockedSessionBeanFixture<T> {
		private MockedSessionBeanFixtureExtension(Class<T> beanToTest) {
			super(beanToTest);
		}
	}

	private final IMockedSessionBeanFixture<T> delegate;

	/**
	 * Default constructor.
	 * 
	 * @param beanToTest -
	 *            the bean to test.
	 */
	public MockedSessionBeanJUnit4Fixture(Class<T> beanToTest) {
		delegate = new MockedSessionBeanFixtureExtension(beanToTest);
	}

	/**
	 * {@inheritDoc}
	 */
	public T getBeanToTest() {
		return delegate.getBeanToTest();
	}

	/**
	 * {@inheritDoc}
	 */
	public Mockery getContext() {
		return delegate.getContext();
	}

	/**
	 * {@inheritDoc}
	 */
	public <M> M getMock(Class<M> interfaze) {
		return delegate.getMock(interfaze);
	}

	/**
	 * {@inheritDoc}
	 */
	@Before
	public void setUp() throws Exception {
		delegate.setUp();

	}

	/**
	 * {@inheritDoc}
	 */
	public void setValueForField(String fieldName, Object toSet) {
		delegate.setValueForField(fieldName, toSet);

	}

	/**
	 * {@inheritDoc}
	 */
	@After
	public void tearDown() throws Exception {
		delegate.tearDown();
	}

}
