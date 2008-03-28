package com.bm.testsuite.junit4;

import javax.persistence.EntityManager;
import javax.sql.DataSource;

import org.junit.After;
import org.junit.Before;

import com.bm.creators.SessionBeanFactory;
import com.bm.jndi.Ejb3UnitJndiBinder;
import com.bm.testsuite.BaseJbossServiceFixture;
import com.bm.testsuite.dataloader.InitialDataSet;
import com.bm.testsuite.interfaces.IBaseJbossServiceFixture;

/**
 * JUnit4 enabled version of BaseJbossServiceJUnit4Fixture (JUnit 3.x).
 * @author Fabian Bauschulte
 *
 * @param <T>
 */
public class BaseJbossServiceJUnit4Fixture<T> implements
		IBaseJbossServiceFixture<T> {
	private final class BaseJbossServiceFixtureExtension extends
			BaseJbossServiceFixture<T> {
		private BaseJbossServiceFixtureExtension(Class<T> sessionBeanToTest,
				Class<?>[] usedEntityBeans) {
			super(sessionBeanToTest, usedEntityBeans);
		}

		public BaseJbossServiceFixtureExtension(Class<T> sessionBeanToTest,
				Class<?>[] usedEntityBeans, InitialDataSet... initialData) {
			super(sessionBeanToTest, usedEntityBeans, initialData);

		}
	}

	private final IBaseJbossServiceFixture<T> delegate;

	/**
	 * Constructor.
	 * 
	 * @param sessionBeanToTest -
	 *            to test
	 * @param usedEntityBeans -
	 *            used
	 */
	public BaseJbossServiceJUnit4Fixture(Class<T> sessionBeanToTest,
			Class<?>[] usedEntityBeans) {
		delegate = new BaseJbossServiceFixtureExtension(sessionBeanToTest,
				usedEntityBeans);
	}

	/**
	 * Constructor.
	 * 
	 * @param sessionBeanToTest -
	 *            to test
	 * @param usedEntityBeans -
	 *            used
	 * @param initialData -
	 *            the inital data to create in the db
	 */
	public BaseJbossServiceJUnit4Fixture(Class<T> sessionBeanToTest,
			Class<?>[] usedEntityBeans, InitialDataSet... initialData) {
		delegate = new BaseJbossServiceFixtureExtension(sessionBeanToTest,
				usedEntityBeans, initialData);

	}

	/**
	 * {@inheritDoc}
	 */
	public Class<T> getBeanClass() {

		return delegate.getBeanClass();
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
	public DataSource getDataSource() {

		return delegate.getDataSource();
	}

	/**
	 * {@inheritDoc}
	 */
	public EntityManager getEntityManager() {

		return delegate.getEntityManager();
	}

	/**
	 * {@inheritDoc}
	 */
	public Ejb3UnitJndiBinder getJndiBinder() {

		return delegate.getJndiBinder();
	}

	/**
	 * {@inheritDoc}
	 */
	public SessionBeanFactory<T> getSbFactory() {

		return delegate.getSbFactory();
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
	public void setValueForField(Object forObject, String fieldName,
			Object toSet) {
		delegate.setValueForField(forObject, fieldName, toSet);

	}

	/**
	 * {@inheritDoc}
	 */
	@After
	public void tearDown() throws Exception {
		delegate.tearDown();

	}

}
