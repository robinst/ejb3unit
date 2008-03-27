package com.bm.testsuite;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import javax.sql.DataSource;

import com.bm.cfg.Ejb3UnitCfg;
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
public abstract class PoJoFixture extends BaseFixture {

	private static final org.apache.log4j.Logger log = org.apache.log4j.Logger
			.getLogger(PoJoFixture.class);

	/**
	 * Constructor.
	 * 
	 * @param usedEntityBeans -
	 *            the used entity beans
	 */
	public PoJoFixture(Class<?>[] usedEntityBeans) {
		this(usedEntityBeans, (InitialDataSet[]) null);

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
		super(initialData);
		initInjector(Arrays.asList(usedEntityBeans));

		final List<Class<? extends Object>> usedEntityBeansC = new ArrayList<Class<? extends Object>>();

		for (Class<? extends Object> akt : usedEntityBeans) {
			usedEntityBeansC.add(akt);
		}

	}

	/**
	 * @author Daniel Wiese
	 * @since 16.10.2005
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		super.setUp();
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
	 * Liefert die datasource.
	 * 
	 * @return die data source.
	 */
	public DataSource getDataSource() {
		return new BasicDataSource(Ejb3UnitCfg.getConfiguration());
	}

	/**
	 * Returns a isntance of a EntityManager.
	 * 
	 * @author Daniel Wiese
	 * @since 12.11.2005
	 * @return - a instance of an entity manager
	 */
	public EntityManager getEntityManager() {
		return getEntityManagerProv().get();
	}

}
