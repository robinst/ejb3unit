package com.bm.testsuite;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import com.bm.creators.SessionBeanFactory;
import com.bm.ejb3guice.inject.Inject;
import com.bm.ejb3guice.inject.Injector;
import com.bm.ejb3guice.inject.Provider;
import com.bm.introspectors.IIntrospector;
import com.bm.jndi.Ejb3UnitJndiBinder;
import com.bm.testsuite.dataloader.EntityInitialDataSet;
import com.bm.testsuite.dataloader.InitialDataSet;
import com.bm.utils.injectinternal.InternalInjector;

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

	private final Class<T> beanClass;

	private T beanToTest = null;

	private InitialDataSet[] initalDataSets = null;

	@Inject
	private SessionBeanFactory<T> sbFactory;

	@Inject
	private Ejb3UnitJndiBinder jndiBinder;

	@Inject
	private Provider<EntityManager> emProv;

	private final Injector injector;

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
	public BaseSessionBeanFixture(Class<T> sessionBeanToTest, Class<?>[] usedEntityBeans,
			InitialDataSet... initialData) {
		this(sessionBeanToTest, usedEntityBeans);
		this.initalDataSets = initialData;
	}

	/**
	 * Constructor.
	 * 
	 * @param sessionBeanToTest -
	 *            the class of the session bean to test
	 * @param usedEntityBeans -
	 *            the used entity bens
	 */
	public BaseSessionBeanFixture(Class<T> sessionBeanToTest, Class<?>[] usedEntityBeans) {
		super();
		injector = InternalInjector.createInternalInjector(usedEntityBeans);
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
	protected BaseSessionBeanFixture(Class<T> sessionBeanToTest, IIntrospector<T> intro,
			Class<?>[] usedEntityBeans, InitialDataSet... initialData) {
		super();
		injector = InternalInjector.createInternalInjector(usedEntityBeans);
		this.beanClass = sessionBeanToTest;
		this.initalDataSets = initialData;
	}

	/**
	 * Sets a CSV inital data set to prepopylate the database with data.
	 * 
	 * @param initalDataSets
	 *            the array of initial data sets
	 */
	public void setInitalDataSets(InitialDataSet... initalDataSets) {
		this.initalDataSets = initalDataSets;
	}

	/**
	 * @author Daniel Wiese
	 * @since 16.10.2005
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		injector.injectMembers(this);
		this.jndiBinder.bind();
		this.beanToTest = this.sbFactory.createSessionBean(this.beanClass);

		if (this.initalDataSets != null) {
			for (InitialDataSet current : this.initalDataSets) {
				// insert entity manager
				EntityManager em = emProv.get();
				if (current instanceof EntityInitialDataSet) {
					EntityInitialDataSet<?> curentEntDs = (EntityInitialDataSet<?>) current;
					curentEntDs.setEntityManager(em);
					EntityTransaction tx = em.getTransaction();
					try {
						tx.begin();
						current.create();
						tx.commit();
					} catch (Exception e) {
						tx.rollback();
						throw e;
					}
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
		EntityManager em = emProv.get();
		em.clear();
		if (this.initalDataSets != null) {
			// be violated
			for (int i = this.initalDataSets.length - 1; i >= 0; i--) {
				this.initalDataSets[i].cleanup(em);
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
		return emProv.get();
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
