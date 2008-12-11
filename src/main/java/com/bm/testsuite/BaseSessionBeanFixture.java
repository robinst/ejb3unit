package com.bm.testsuite;

import java.util.Arrays;

import javax.persistence.EntityManager;
import javax.sql.DataSource;

import com.bm.cfg.Ejb3UnitCfg;
import com.bm.creators.SessionBeanFactory;
import com.bm.ejb3guice.inject.Ejb3UnitInternalInject;
import com.bm.introspectors.IIntrospector;
import com.bm.testsuite.dataloader.InitialDataSet;
import com.bm.testsuite.interfaces.IBaseSessionBeanFixture;
import com.bm.utils.BasicDataSource;

/**
 * This is the base class for all JUnit test - testing stateless/statefull
 * SessionBeans (EJB 3.0 Specification conform).
 * 
 * @author Daniel Wiese
 * @param <T> -
 *            the type of the session bean to test
 * @since 16.10.2005
 */
public abstract class BaseSessionBeanFixture<T> extends BaseFixture implements IBaseSessionBeanFixture<T> {

	private final Class<T> beanClass;

	private T beanToTest = null;

	@Ejb3UnitInternalInject
	private SessionBeanFactory<T> sbFactory;

	/**
	 * Constructor.
	 * 
	 * @param sessionBeanToTest -
	 *            the class of the session bean to test
	 * @param usedEntityBeans -
	 *            the used entity bens
	 */
	public BaseSessionBeanFixture(Class<T> sessionBeanToTest,
			Class<?>[] usedEntityBeans) {
		this(sessionBeanToTest, usedEntityBeans, (InitialDataSet[]) null);

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
	public BaseSessionBeanFixture(Class<T> sessionBeanToTest,
			Class<?>[] usedEntityBeans, InitialDataSet... initialData) {
		super(initialData);
		initInjector(Arrays.asList(usedEntityBeans));
		this.beanClass = sessionBeanToTest;

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
	protected BaseSessionBeanFixture(Class<T> sessionBeanToTest,
			IIntrospector<T> intro, Class<?>[] usedEntityBeans,
			InitialDataSet... initialData) {
		this(sessionBeanToTest, usedEntityBeans, initialData);
		// This is strange - what happens with the introspector intro?
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setUp() throws Exception {
            try {
		super.setUp();
		this.beanToTest = this.sbFactory.createSessionBean(this.beanClass);
            } catch (Exception e) {
                super.tearDown();
                throw e;
            }

	}

	/**
	 * {@inheritDoc}
	 */
	public EntityManager getEntityManager() {
		return getEntityManagerProv().get();
	}

	/**
	 * {@inheritDoc}
	 */
	public Class<T> getBeanClass() {
		return this.beanClass;
	}

	/**
	 * {@inheritDoc}
	 */
	public T getBeanToTest() {
		return this.beanToTest;
	}

	/**
	 * {@inheritDoc}
	 */
	public SessionBeanFactory<T> getSbFactory() {
		return this.sbFactory;
	}

	/**
	 * {@inheritDoc}
	 */
	public DataSource getDataSource() {
		return new BasicDataSource(Ejb3UnitCfg.getConfiguration());
	}
}
