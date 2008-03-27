package com.bm.testsuite;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import com.bm.ejb3guice.inject.Inject;
import com.bm.ejb3guice.inject.Injector;
import com.bm.ejb3guice.inject.Provider;
import com.bm.jndi.Ejb3UnitJndiBinder;
import com.bm.testsuite.dataloader.EntityInitialDataSet;
import com.bm.testsuite.dataloader.InitialDataSet;
import com.bm.utils.injectinternal.InternalInjector;

/**
 * Baseclass for all Fixtures with Entity-Manager support.
 * 
 * @author Fabian Bauschulte
 * 
 */
public class BaseFixture extends BaseTest {

	/**
	 * Default costructor.
	 */
	public BaseFixture(InitialDataSet[] initalDataSet) {
		this.initalDataSet = initalDataSet;
	}

	/**
	 * Default costructor.
	 */
	public BaseFixture() {
		this(null);

	}

	@Inject
	private Ejb3UnitJndiBinder jndiBinder;

	@Inject
	private Provider<EntityManager> entityManagerProv;

	private Injector injector;

	private final InitialDataSet[] initalDataSet;

	/**
	 * If an exception during construction occurs, it is stored here to fail the
	 * tests.
	 */
	private EntityInitializationException initializationError;

	/**
	 * @author Fabian Bauschulte
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		fireExceptionIfNotInitialized();
		injector.injectMembers(this);
		this.jndiBinder.bind();
		entityManagerProv.get().clear();

		// In case there are Initialdatasets they are persited
		if (this.initalDataSet != null) {
			for (InitialDataSet current : this.initalDataSet) {
				// insert entity manager
				EntityManager em = getEntityManagerProv().get();
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

		// If there are Initaldatasets there have to be cleared up
		if (this.initalDataSet != null) {
			EntityManager em = getEntityManagerProv().get();
			// Is this necessary?
			em.clear();

			// be violated ??

			for (int i = this.initalDataSet.length - 1; i >= 0; i--) {
				this.initalDataSet[i].cleanup(em);
			}
		}

	}

	public boolean initFailed() {
		return injector == null;
	}

	/**
	 * Fires an Exception if not Initialised.
	 */
	private void fireExceptionIfNotInitialized() {
		if (initFailed()) {
			if (initializationError == null) {
				fail("Initialization failed.");
			} else {
				throw initializationError;
			}
		}
	}

	void initInjector(final List<Class<?>> entitiesToTest) {
		try {
			injector = InternalInjector.createInternalInjector(entitiesToTest);
		} catch (EntityInitializationException e) {
			initializationError = e;
		}
	}

	EntityInitializationException getInitializationError() {
		return initializationError;
	}

	Ejb3UnitJndiBinder getJndiBinder() {
		return jndiBinder;
	}

	Provider<EntityManager> getEntityManagerProv() {
		return entityManagerProv;
	}

	Injector getInjector() {
		return injector;
	}

}
