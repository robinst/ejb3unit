package com.bm.testsuite;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import com.bm.cfg.Ejb3UnitCfg;
import com.bm.creators.SessionBeanFactory;
import com.bm.introspectors.SessionBeanIntrospector;
import com.bm.testsuite.dataloader.EntityInitialDataSet;
import com.bm.testsuite.dataloader.InitialDataSet;
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
public abstract class BaseSessionBeanFixture<T> extends BaseTest {

	private final SessionBeanFactory<T> sbFactory;

	private final Class<T> beanClass;

	private T beanToTest = null;

	private InitialDataSet[] initalDataSet = null;

	/**
	 * Constructor.
	 * 
	 * @param sessionBeanToTest -
	 *            the class of the session bean to test
	 * @param usedEntityBeans -
	 *            the used entity bens
	 */
	public BaseSessionBeanFixture(Class<T> sessionBeanToTest,
			Class[] usedEntityBeans) {
		super();
		SessionBeanIntrospector<T> intro = new SessionBeanIntrospector<T>(
				sessionBeanToTest);
		this.sbFactory = new SessionBeanFactory<T>(intro, usedEntityBeans);
		this.beanClass = sessionBeanToTest;

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
			Class[] usedEntityBeans, InitialDataSet... initialData) {
		this(sessionBeanToTest, usedEntityBeans);
		this.initalDataSet = initialData;
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
			SessionBeanIntrospector<T> intro, Class[] usedEntityBeans,
			InitialDataSet... initialData) {
		super();
		this.sbFactory = new SessionBeanFactory<T>(intro, usedEntityBeans);
		this.beanClass = sessionBeanToTest;
		this.initalDataSet = initialData;
	}

	/**
	 * @author Daniel Wiese
	 * @since 16.10.2005
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.beanToTest = this.sbFactory.createSessionBean(this.beanClass);

		if (this.initalDataSet != null) {
			EntityManager em = this.getEntityManager();

			for (InitialDataSet current : this.initalDataSet) {
				// insert entity manager
				if (current instanceof EntityInitialDataSet) {
					EntityInitialDataSet curentEntDs = (EntityInitialDataSet) current;
					curentEntDs.setEntityManager(em);
					EntityTransaction tx = em.getTransaction();
					tx.begin();
					current.create();
					tx.commit();
				} else {
					current.create();
				}
			}
		}
	}

	/**
	 * @author Daniel Wiese
	 * @since 16.10.2005
	 * @see junit.framework.TestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		// DO NOT DESTRROY SESSION BEANS, BECAUSE WE ARE USING THEM
		// IN SINGLE THREADS CURRENTLY TOGETHER
		// this.sbFactory.destroySessionBean(this.beanToTest);
		// this.beanToTest = null;

		// schutdown hybernate if in memory
		// if (Ejb3UnitCfg.getConfiguration().isInMemory()) {
		// final BasicDataSource ds = new BasicDataSource(Ejb3UnitCfg
		// .getConfiguration());
		// ds.shutdownInMemoryDatabase();
		// }

		// delete all objects (faster than shutdown and restart everything)
		final BasicDataSource ds = new BasicDataSource(Ejb3UnitCfg
				.getConfiguration());
		if (this.initalDataSet != null) {
			for (InitialDataSet current : this.initalDataSet) {
				current.cleanup(ds);
			}
		}

	}

	/**
	 * Returns the beanClass.
	 * 
	 * @return Returns the beanClass.
	 */
	public Class<T> getBeanClass() {
		return this.beanClass;
	}

	/**
	 * Returns the beanToTest.
	 * 
	 * @return Returns the beanToTest.
	 */
	public T getBeanToTest() {
		return this.beanToTest;
	}

	/**
	 * Returns a isntance of a EntityManager.
	 * 
	 * @author Daniel Wiese
	 * @since 12.11.2005
	 * @return - a instance of an entity manager
	 */
	public EntityManager getEntityManager() {
		return this.sbFactory.createEntityManager();
	}

	/**
	 * Returns the sbFactory.
	 * 
	 * @return Returns the sbFactory.
	 */
	public SessionBeanFactory<T> getSbFactory() {
		return this.sbFactory;
	}

}
