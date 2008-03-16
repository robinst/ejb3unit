package com.bm.utils.injectinternal;

import java.io.IOException;
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

public class EntityManagerProvider implements Provider<EntityManager> {
	private static final Logger logger = Logger.getLogger(EntityManagerProvider.class);
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

	/**
	 * Adds new entities to test - we will create only a persistence manager
	 * factory if the setting has changed (new entites added).
	 * 
	 * @return true if new entites were added
	 * @param entyties
	 *            the entites to add
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
	 */
	public boolean addPersistenceClasses(Collection<Class<?>> entyties) {
		boolean reloadEmFactory = false;
		if (entyties != null) {
			for (Class<?> toAdd : entyties) {
				if (!this.entitiesToTestClassNames.contains(toAdd.getName())) {
					reloadEmFactory = true;
					registerEntity(toAdd);
				}
			}
		}
		if (reloadEmFactory) {
			createEmFactory();
		}
		return reloadEmFactory;
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
