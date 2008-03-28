package com.bm.testsuite.junit4;

import javax.persistence.EntityManager;
import javax.sql.DataSource;

import org.junit.After;
import org.junit.Before;

import com.bm.creators.SessionBeanFactory;
import com.bm.introspectors.IIntrospector;
import com.bm.jndi.Ejb3UnitJndiBinder;
import com.bm.testsuite.BaseSessionBeanFixture;
import com.bm.testsuite.dataloader.InitialDataSet;
import com.bm.testsuite.interfaces.IBaseSessionBeanFixture;

/**
 * JUnit4 enabled version of BaseSessionBeanJUnit4Fixture (JUnit 3.x).
 * @author Fabian Bauschulte
 *
 * @param <T>
 */
public class BaseSessionBeanJUnit4Fixture<T> implements
		IBaseSessionBeanFixture<T> {

	private final class BaseSessionBeanFixtureExtension extends
			BaseSessionBeanFixture<T> {
		private BaseSessionBeanFixtureExtension(Class<T> sessionBeanToTest,
				Class<?>[] usedEntityBeans) {
			super(sessionBeanToTest, usedEntityBeans);
		}

		public BaseSessionBeanFixtureExtension(Class<T> sessionBeanToTest,
				Class<?>[] usedEntityBeans, InitialDataSet... initialData) {
			super(sessionBeanToTest, usedEntityBeans, initialData);
			
		}

		public BaseSessionBeanFixtureExtension(Class<T> sessionBeanToTest,
				IIntrospector<T> intro, Class<?>[] usedEntityBeans,
				InitialDataSet... initialData) {
			super(sessionBeanToTest, intro, usedEntityBeans, initialData);
			
		}
	}

	private final IBaseSessionBeanFixture<T> delegate;

	/**
	 * Constructor.
	 * 
	 * @param sessionBeanToTest -
	 *            the class of the session bean to test
	 * @param usedEntityBeans -
	 *            the used entity bens
	 */
	public BaseSessionBeanJUnit4Fixture(Class<T> sessionBeanToTest,
			Class<?>[] usedEntityBeans) {
		delegate = new BaseSessionBeanFixtureExtension(sessionBeanToTest, usedEntityBeans);

	}

	/**
	 * Constructor.
	 * 
	 * @param sessionBeanToTest -
	 *            the class of the session bean to test
	 * @param usedEntityBeans -
	 *            the used entity beans
	 * @param initialData -
	 *            the inital data to create in the db
	 */
	public BaseSessionBeanJUnit4Fixture(Class<T> sessionBeanToTest,
			Class<?>[] usedEntityBeans, InitialDataSet... initialData) {
		delegate = new BaseSessionBeanFixtureExtension(sessionBeanToTest,
				usedEntityBeans, initialData);

	}

	/**
	 * Protected Constructor> possible to pass another introspector
	 * 
	 * @param sessionBeanToTest -
	 *            the class of the session bean to test
	 * @param intro -
	 *            the introspector
	 * @param usedEntityBeans -
	 *            the used entity bens
	 * @param initialData -
	 *            the inital data to create in the db
	 */
	protected BaseSessionBeanJUnit4Fixture(Class<T> sessionBeanToTest,
			IIntrospector<T> intro, Class<?>[] usedEntityBeans,
			InitialDataSet... initialData) {
		delegate = new BaseSessionBeanFixtureExtension(sessionBeanToTest, intro,
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
