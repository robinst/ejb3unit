package com.bm.utils.injectinternal;

import java.util.Arrays;
import java.util.Collection;

import javax.persistence.EntityManager;

import com.bm.ejb3guice.inject.AbstractModule;
import com.bm.ejb3guice.inject.Ejb3Guice;
import com.bm.ejb3guice.inject.Injector;
import com.bm.testsuite.EntityInitializationException;

public final class InternalInjector extends AbstractModule {

	private final EntityManagerProvider prov;
	private final static EntityManagerProvider entityManagerProvider = new EntityManagerProvider();
	private static Injector injector;

	private InternalInjector(EntityManagerProvider prov) {
		this.prov = prov;
	}

	@Override
	protected void configure() {
		this.bind(EntityManager.class).toProvider(prov);

	}

	/**
	 * The injector used internally by ejb3unit.
	 * 
	 * @param entytiesToTest
	 *            the current test case entities
	 * @return the injector
	 */
	public static Injector createInternalInjector(Class<?>... entytiesToTest)
			throws EntityInitializationException {
		return createInternalInjector(Arrays.asList(entytiesToTest));
	}

	/**
	 * The injector used internally by ejb3unit.
	 * 
	 * @param entyties
	 *            the current test case entities
	 * @return the injector
	 */
	public static synchronized Injector createInternalInjector(
			Collection<Class<?>> entyties) throws EntityInitializationException {

		if (entityManagerProvider.addPersistenceClasses(entyties)
				|| injector == null) {
			injector = Ejb3Guice.createInjector(new InternalInjector(
					entityManagerProvider));
		}

		return injector;
	}
}
