package com.bm.testsuite;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import javax.sql.DataSource;

import com.bm.cfg.Ejb3UnitCfg;
import com.bm.creators.EntityBeanCreator;
import com.bm.testsuite.dataloader.InitialDataSet;
import com.bm.testsuite.interfaces.IPoJoFixture;
import com.bm.utils.BasicDataSource;

/**
 * Supports entity manager and flat file db injection for pojos.
 * 
 * @author Daniel Wiese
 * @param <T> -
 *            the type of the service
 * @since 12.11.2005
 */
public abstract class PoJoFixture extends BaseFixture implements IPoJoFixture {

	private static final org.apache.log4j.Logger log = org.apache.log4j.Logger
			.getLogger(PoJoFixture.class);

	private final Map<Class<?>, EntityBeanCreator<?>> creators = new HashMap<Class<?>, EntityBeanCreator<?>>();

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
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setUp() throws Exception {
		super.setUp();
		this.creators.clear();
	}

	/**
	 * Creates a random bean filled with values for the primitive fields which
	 * can be used for test purposes
	 * 
	 * @param <T>
	 *            the type
	 * @param toGenerate
	 *            the instance to generate
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected <T> T generateRandomInstance(Class<T> toGenerate) {
		EntityBeanCreator<T> creator = null;
		if (this.creators.containsKey(toGenerate)) {
			creator = (EntityBeanCreator<T>) creators.get(toGenerate);
		} else {
			creator = new EntityBeanCreator<T>(this.getEntityManager(),
					toGenerate);
			this.creators.put(toGenerate, creator);
		}
		return creator.createBeanInstance();
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public <T> List<T> findAll(Class<T> clazz) {
		EntityManager manager = this.getEntityManager();
		Query query = manager.createQuery("select c from " + clazz.getName()
				+ " c");
		return query.getResultList();
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public <T> void deleteAll(Class<T> clazz) {
		EntityManager manager = this.getEntityManager();
		EntityTransaction tx = manager.getTransaction();
		tx.begin();
		Query query = manager.createQuery("delete from " + clazz.getName());
		query.executeUpdate();
		tx.commit();
	}

	/**
	 * {@inheritDoc}
	 */
	public <T> List<T> persist(List<T> complexObjectGraph) {
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
	 * {@inheritDoc}
	 */
	public DataSource getDataSource() {
		return new BasicDataSource(Ejb3UnitCfg.getConfiguration());
	}

	/**
	 * {@inheritDoc}
	 */
	public EntityManager getEntityManager() {
		return getEntityManagerProv().get();
	}


}
