package com.bm.testsuite;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
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

	private final Ejb3UnitJndiBinder jndiBinder;

	private InitialDataSet[] initalDataSet = null;

	private final Ejb3UnitCfg configuration;

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
		return this.configuration.getEntityManagerFactory()
				.createEntityManager();
	}

}