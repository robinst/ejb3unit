package com.bm.utils.injectinternal;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.hibernate.ejb.Ejb3Configuration;

import com.bm.cfg.Ejb3UnitCfg;
import com.bm.ejb3guice.inject.Provider;

public class EntityManagerProvider implements Provider<EntityManager> {

	private final Set<Class<?>> entitiesToTest;
	private final Ejb3UnitCfg configuration;
	private EntityManagerFactory entityManagerFactory;
	private EntityManager em;

	public EntityManagerProvider(Class<?>... entytiesToTest) {
		this(Arrays.asList(entytiesToTest));
	}

	public EntityManagerProvider(Collection<Class<?>> entyties) {
		entitiesToTest = new HashSet<Class<?>>();
		entitiesToTest.addAll(entyties);
		configuration = Ejb3UnitCfg.getConfiguration();
		createEmFactory();
	}

	private void createEmFactory() {
		Ejb3Configuration cfg = configuration.getEJB3Configuration();

		for (Class<?> entityToTest : this.entitiesToTest) {
			cfg.addAnnotatedClass(entityToTest);
		}

		// fix later for now it´s working
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
