package com.bm.utils.injectinternal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.apache.log4j.Logger;
import org.hibernate.ejb.Ejb3Configuration;

import com.bm.PersistenceXml;
import com.bm.cfg.Ejb3UnitCfg;
import com.bm.ejb3guice.inject.Provider;
import com.bm.testsuite.EntityInitializationException;

public class EntityManagerProvider implements Provider<EntityManager> {
	private static final Logger logger = Logger
			.getLogger(EntityManagerProvider.class);
	private final Set<Class<?>> entitiesToTest;
	private final Set<String> entitiesToTestClassNames;
	private final Ejb3UnitCfg configuration;
	private EntityManagerFactory entityManagerFactory;
	private EntityManager em;

	public EntityManagerProvider() {
		this((Collection<Class<?>>) null);
	}

	public EntityManagerProvider(Class<?>... entytiesToTest) {
		this(Arrays.asList(entytiesToTest));
	}

	public EntityManagerProvider(Collection<Class<?>> entyties) {
		entitiesToTest = new HashSet<Class<?>>();
		entitiesToTestClassNames = new HashSet<String>();
		if (entyties != null) {
			registerEntities(entyties);
		}
		if (Ejb3UnitCfg.getConfiguration().isLoadPeristenceXML()) {
			try {
				Class<?>[] classesFromPersistenceXML = PersistenceXml
						.getClasses(Ejb3UnitCfg.getConfiguration().getValue(
								Ejb3UnitCfg.KEY_PERSISTENCE_UNIT_NAME));
				registerEntities(Arrays.asList(classesFromPersistenceXML));
			} catch (IOException e) {
				logger.error(e);
				throw new IllegalArgumentException(e);
			} catch (ClassNotFoundException e) {
				logger.error(e);
				throw new IllegalArgumentException(e);
			}
		}
		configuration = Ejb3UnitCfg.getConfiguration();
		createEmFactory();
	}

	private void registerEntities(Collection<Class<?>> toregister) {
		for (Class<?> current : toregister) {
			registerEntity(current);
		}

	}

	private void registerEntity(Class<?> current) {
		this.entitiesToTest.add(current);
		this.entitiesToTestClassNames.add(current.getName());
	}

	private void unregisterEntity(Class<?> current) {
		this.entitiesToTest.remove(current);
		this.entitiesToTestClassNames.remove(current.getName());
	}

	/**
	 * Adds new entities to test - we will create only a persistence manager
	 * factory if the setting has changed (new entites added).
	 * 
	 * @return true if new entites were added
	 * @param entyties
	 *            the entites to add
	 * @throws EntityInitializationException
	 *             if not initialized
	 */
	public boolean addPersistenceClasses(Class<?>... entytiesToTest) {
		return addPersistenceClasses(Arrays.asList(entytiesToTest));
	}

	/**
	 * Adds new entities to test - we will create only a persistence manager
	 * factory if the setting has changed (new entites added).
	 * 
	 * @param entyties
	 *            the entites to add
	 * @return true if new entites were added
	 * @throws EntityInitializationException
	 *             if not initialized
	 */
	public boolean addPersistenceClasses(Collection<Class<?>> entyties)
			throws EntityInitializationException {
		boolean reloadEmFactory = false;

		HashSet<Class<?>> entytiesAdded = new HashSet<Class<?>>();
		if (entyties != null) {
			for (Class<?> toAdd : entyties) {
				if (!this.entitiesToTestClassNames.contains(toAdd.getName())) {
					reloadEmFactory = true;
					entytiesAdded.add(toAdd);
					registerEntity(toAdd);
				}
			}
		}
		if (reloadEmFactory) {
			try {
				createEmFactory();
			} catch (RuntimeException e) {
				RuntimeException rollbackException = rollbackChange(entytiesAdded);
				bumpException(entytiesAdded, e, rollbackException);
			}
		}
		return reloadEmFactory;
	}

	private void bumpException(HashSet<Class<?>> entytiesAdded,
			RuntimeException e, RuntimeException rollbackException) {
		// Rollback sucessful!
		String message = "";
		if (em != null) {
			message = "EntityManager could not be reinitialized to load the added enities "
					+ new ArrayList<Class<?>>(entytiesAdded) + ". ";
		} else {
			HashSet<String> fullList = new HashSet<String>(
					entitiesToTestClassNames);
			for (Class<?> toAdd : entytiesAdded) {
				fullList.add(toAdd.getName());
			}
			message = "EntityManager could not be initialized to load the enities "
					+ new ArrayList<String>(fullList) + ". ";
		}
	
		if (rollbackException == null) {
			throw new EntityInitializationException(message, e);

		} else {
			throw new EntityInitializationException(
					"Warning following test may fail because no rollback of this error was possible. "
							+ message, e);

		}
	}

	private RuntimeException rollbackChange(Set<Class<?>> entytiesAdded) {
		// If a Exception is caused my a malformed Entity we will
		// rollback this change
		// so that the other test are not influeced by this.
		if (!entytiesAdded.isEmpty()) {
			for (Class<?> toRemove : entytiesAdded) {
				unregisterEntity(toRemove);
			}
			try {
				createEmFactory();
			} catch (RuntimeException e) {
				return e;
			}

		}
		return null;
	}

	private void createEmFactory() {
		Ejb3Configuration cfg = configuration.getEJB3Configuration();

		for (Class<?> entityToTest : this.entitiesToTest) {
			cfg.addAnnotatedClass(entityToTest);
		}

		if (em != null && em.isOpen()) {
			try {
				em.close();
			} catch (Exception e) {
				// intetionally left blank
			}
		}
		this.em = null;
		this.entityManagerFactory = cfg.createEntityManagerFactory();
	}

	/**
	 * {@inheritDoc}
	 */
	public EntityManager get() {
		if (em == null || !em.isOpen()) {
			em = entityManagerFactory.createEntityManager();
		}
		return em;
	}

}
