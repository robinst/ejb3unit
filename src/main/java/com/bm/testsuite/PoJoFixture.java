package com.bm.testsuite;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import javax.sql.DataSource;

import com.bm.cfg.Ejb3UnitCfg;
import com.bm.jndi.Ejb3UnitJndiBinder;
import com.bm.testsuite.dataloader.EntityInitialDataSet;
import com.bm.testsuite.dataloader.InitialDataSet;
import com.bm.utils.BasicDataSource;

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

	private final Ejb3UnitJndiBinder jndiBinder;

	private InitialDataSet[] initalDataSet = null;

	private final Ejb3UnitCfg configuration;

	private EntityManager entityManager;

	/**
	 * Constructor.
	 * 
	 * @param usedEntityBeans -
	 *            the used entity bens
	 */
	public PoJoFixture(Class[] usedEntityBeans) {
		super();
		this.jndiBinder = new Ejb3UnitJndiBinder(usedEntityBeans);
		final List<Class<? extends Object>> usedEntityBeansC = new ArrayList<Class<? extends Object>>();

		for (Class<? extends Object> akt : usedEntityBeans) {
			usedEntityBeansC.add(akt);
		}
		Ejb3UnitCfg.addEntytiesToTest(usedEntityBeansC);
		this.configuration = Ejb3UnitCfg.getConfiguration();

	}

	/**
	 * Constructor.
	 * 
	 * @param usedEntityBeans -
	 *            the used entity beans
	 * @param initialData -
	 *            the inital data to create in the db
	 */
	public PoJoFixture(Class[] usedEntityBeans, InitialDataSet... initialData) {
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
		this.jndiBinder.bind();
		log.info("Creating entity manager instance for POJO test");
		entityManager = this.configuration.getEntityManagerFactory()
				.createEntityManager();

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
		Query query = manager.createQuery("select c from " + clazz.getName() + " c");
		return (List<T>) query.getResultList();
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
			tx.commit();
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
			for (InitialDataSet current : this.initalDataSet) {
				current.cleanup(entityManager);
			}
		}
		this.entityManager = null;
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
		return this.entityManager;
	}

}
