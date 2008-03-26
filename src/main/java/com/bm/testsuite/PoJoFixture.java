package com.bm.testsuite;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import javax.sql.DataSource;

import com.bm.cfg.Ejb3UnitCfg;
import com.bm.ejb3guice.inject.Inject;
import com.bm.ejb3guice.inject.Provider;
import com.bm.jndi.Ejb3UnitJndiBinder;
import com.bm.testsuite.dataloader.EntityInitialDataSet;
import com.bm.testsuite.dataloader.InitialDataSet;
import com.bm.utils.BasicDataSource;
import com.bm.utils.injectinternal.InternalInjector;

/**
 * Supports entity manager and flat file db injection for pojos.
 * 
 * @author Daniel Wiese
 * @param <T> -
 *            the type of the service
 * @since 12.11.2005
 */
public abstract class PoJoFixture extends BaseTest {

	private static final org.apache.log4j.Logger log = org.apache.log4j.Logger
			.getLogger(PoJoFixture.class);

	private InitialDataSet[] initalDataSet = null;

	private final Ejb3UnitCfg configuration;

	@Inject
	private Provider<EntityManager> entityManagerProv;

	@Inject
	private Ejb3UnitJndiBinder jndiBinder;

	/**
	 * Constructor.
	 * 
	 * @param usedEntityBeans -
	 *            the used entity beans
	 */
	public PoJoFixture(Class<?>[] usedEntityBeans) {
		super();
		try {
			injector = InternalInjector.createInternalInjector(usedEntityBeans);

		} catch (EntityInitializationException e) {
			initializationError = e;

		}

		if (initializationError == null) {
			final List<Class<? extends Object>> usedEntityBeansC = new ArrayList<Class<? extends Object>>();

			for (Class<? extends Object> akt : usedEntityBeans) {
				usedEntityBeansC.add(akt);
			}
			this.configuration = Ejb3UnitCfg.getConfiguration();

		} else {
			this.configuration = null;
		}

	}

	/**
	 * Constructor.
	 * 
	 * @param usedEntityBeans -
	 *            the used entity beans
	 * @param initialData -
	 *            the initial data to create in the db
	 */
	public PoJoFixture(Class<?>[] usedEntityBeans,
			InitialDataSet... initialData) {
		this(usedEntityBeans);
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
		fireExceptionIfNotInitialized();
		injector.injectMembers(this);
		entityManagerProv.get().clear();
		this.jndiBinder.bind();
		log.debug("Creating entity manager instance for POJO test");
		if (this.initalDataSet != null) {
			EntityManager em = this.getEntityManager();

			for (InitialDataSet current : this.initalDataSet) {
				// insert entity manager
				if (current instanceof EntityInitialDataSet) {
					EntityInitialDataSet<?> curentEntDs = (EntityInitialDataSet<?>) current;
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
	 * Find all rows of the given class in the database.
	 * 
	 * @param <T>
	 *            the tyme of the persistent object
	 * @param clazz
	 *            the class of of the persistent object
	 * @return all DB instances
	 */
	@SuppressWarnings("unchecked")
	protected <T> List<T> findAll(Class<T> clazz) {
		EntityManager manager = this.getEntityManager();
		Query query = manager.createQuery("select c from " + clazz.getName()
				+ " c");
		return query.getResultList();
	}

	/**
	 * Deletes all rows of the given class in the database.
	 * 
	 * @param <T>
	 *            the tyme of the persistent object
	 * @param clazz
	 *            the class of of the persistent object
	 * @return all DB instances
	 */
	@SuppressWarnings("unchecked")
	protected <T> void deleteAll(Class<T> clazz) {
		EntityManager manager = this.getEntityManager();
		EntityTransaction tx = manager.getTransaction();
		tx.begin();
		Query query = manager.createQuery("delete from " + clazz.getName());
		query.executeUpdate();
		tx.commit();
	}

	/**
	 * Persists all objects in the database.
	 * 
	 * @param <T>
	 *            the tyme of the persistent object
	 * @param complexObjectGraph
	 *            th egraph to persist
	 * @return the persisted objects
	 */
	protected <T> List<T> persist(List<T> complexObjectGraph) {
		EntityManager manager = this.getEntityManager();
		EntityTransaction tx = manager.getTransaction();
		tx.begin();
		final List<T> persited = new ArrayList<T>();
		try {
			for (T order : complexObjectGraph) {
				persited.add(manager.merge(order));
			}
		} finally {
			if (tx.getRollbackOnly()) {
				log.warn("Not committing because we are in rollback-only mode");
			} else {
				tx.commit();
			}
		}

		return persited;
	}

	/**
	 * @author Daniel Wiese
	 * @since 16.10.2005
	 * @see junit.framework.TestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception {
		if (this.initalDataSet != null) {
			EntityManager entityManager = entityManagerProv.get();
			for (InitialDataSet current : this.initalDataSet) {
				current.cleanup(entityManager);
			}
		}
		super.tearDown();

	}

	/**
	 * Liefert die datasource.
	 * 
	 * @return die data source.
	 */
	public DataSource getDataSource() {
		return new BasicDataSource(this.configuration);
	}

	/**
	 * Returns a isntance of a EntityManager.
	 * 
	 * @author Daniel Wiese
	 * @since 12.11.2005
	 * @return - a instance of an entity manager
	 */
	public EntityManager getEntityManager() {
		return this.entityManagerProv.get();
	}

}
