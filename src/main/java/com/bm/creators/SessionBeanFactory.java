package com.bm.creators;

import java.util.Arrays;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.bm.cfg.Ejb3UnitCfg;
import com.bm.ejb3guice.inject.CreationListner;
import com.bm.ejb3guice.inject.Ejb3Guice;
import com.bm.ejb3guice.inject.Inject;
import com.bm.ejb3guice.inject.Injector;
import com.bm.ejb3guice.inject.Module;
import com.bm.ejb3guice.inject.Stage;
import com.bm.ejb3metadata.annotations.metadata.MetaDataCache;
import com.bm.utils.LifeCycleMethodExecuter;

/**
 * This class will create session bean instances without an application server.
 * The dependency injection is done here
 * 
 * @param <T> -
 *            the type of the session bean to create (class name)
 * @author Daniel Wiese
 * @since 18.09.2005
 */
public final class SessionBeanFactory<T> {

	private final LifeCycleMethodExecuter lifeCycleMethodExecuter = new LifeCycleMethodExecuter();

	private final EntityManager em;

	/**
	 * Default constructor.
	 * 
	 * @param em -
	 *            the current entity manager
	 * 
	 */
	@Inject
	public SessionBeanFactory(EntityManager em) {
		this.em = em;
	}

	/**
	 * Factory method to create stateless session beans.
	 * 
	 * @author Daniel Wiese
	 * @since 18.09.2005
	 * @param toCreate -
	 *            the class to create
	 * @return - the created session bean class for local usage
	 */
	@SuppressWarnings("unchecked")
	public T createSessionBean(Class<T> toCreate) {

		BeanCreationListener createdbeans = new BeanCreationListener();
		Injector injector = getInjector(toCreate, createdbeans);
		final T instance = injector.getInstance(toCreate);
		// now inject other instances
		for (Object created : createdbeans.getCreatedBeans()) {
			lifeCycleMethodExecuter.executeLifeCycleMethodsForCreate(created);
		}
		return instance;
	}

	/**
	 * Creates the injector to create session beans and inject fields
	 * 
	 * @param toCreate -
	 *            the class to create
	 * @return - the injector
	 */
	@SuppressWarnings("unchecked")
	public Injector getInjector(Class<T> toCreate, CreationListner creationListener) {
		Module module = MetaDataCache.getDynamicModuleCreator(Ejb3UnitCfg
				.getConfiguration(), em, toCreate);
		Module[] mods = { module };
		Injector injector = Ejb3Guice.createInjector(Stage.PRODUCTION, Arrays
				.asList(mods), Ejb3Guice.markerToArray(EJB.class, Resource.class,
				PersistenceContext.class), creationListener);
		return injector;

	}

}
