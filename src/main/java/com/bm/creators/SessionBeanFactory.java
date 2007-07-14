package com.bm.creators;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;

import com.bm.cfg.Ejb3UnitCfg;
import com.bm.ejb3guice.inject.Ejb3Guice;
import com.bm.ejb3guice.inject.Injector;
import com.bm.ejb3guice.inject.Module;
import com.bm.ejb3guice.inject.Stage;
import com.bm.ejb3metadata.MetadataAnalyzer;
import com.bm.ejb3metadata.annotations.metadata.MethodAnnotationMetadata;
import com.bm.introspectors.AbstractIntrospector;
import com.bm.introspectors.Property;
import com.bm.utils.Ejb3Utils;

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

	/** remember the current entity manager * */
	private EntityManager manager = null;

	private static final Logger log = Logger
			.getLogger(SessionBeanFactory.class);

	private final AbstractIntrospector<T> introspector;

	private final Ejb3UnitCfg configuration;

	/**
	 * Default constructor.
	 * 
	 * @param intro -
	 *            the introspector
	 * 
	 * @param usedEntityBeans -
	 *            the used entity beans for this test
	 */
	public SessionBeanFactory(AbstractIntrospector<T> intro,
			Class[] usedEntityBeans) {
		final List<Class<? extends Object>> usedEntityBeansC = new ArrayList<Class<? extends Object>>();
		if (usedEntityBeans != null) {

			for (Class<? extends Object> akt : usedEntityBeans) {
				usedEntityBeansC.add(akt);
			}
		}
		Ejb3UnitCfg.addEntytiesToTest(usedEntityBeansC);
		this.configuration = Ejb3UnitCfg.getConfiguration();
		this.introspector = intro;

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
		Module module =  MetadataAnalyzer.getGuiceBindingModule(toCreate, configuration, this.createEntityManager());
		
		//final T back = Ejb3Utils.getNewInstance(toCreate);
		Module[] mods = { module };
		BeanCreationListener createdbeans=new BeanCreationListener();
		Injector injector = Ejb3Guice.createInjector(Stage.PRODUCTION, Arrays
				.asList(mods), Ejb3Guice.markerToArray(EJB.class,
				Resource.class, PersistenceContext.class), createdbeans);
		final T instance = injector.getInstance(toCreate);


		// now inject other instances
		//FIXME: invoke the lifecycle in the created beans
		for (Object created: createdbeans.getCreatedBeans()){
			
		}
		this.executeLifeCycleCreateMethods(instance);
		return instance;
	}

	public void executeLifeCycleCreateMethods(T back) {
		
		final Set<MethodAnnotationMetadata> lifeCycleMethods = this.introspector
				.getLifecycleMethods();
		for (MethodAnnotationMetadata current : lifeCycleMethods) {
			if (current.isPostConstruct() || current.isPostActivate()) {
				if (current.getJMethod().getSignature() != null) {
					throw new IllegalArgumentException(
							"The life cycle method (" + current.getJMethod()
									+ ") has arguments");
				}
				Method toInvoke = Ejb3Utils.getParameterlessMethodByName(
						current.getMethodName(), back.getClass());
				toInvoke.setAccessible(true);
				try {
					toInvoke.invoke(back, (Object[]) null);
				} catch (Exception e) {
					throw new IllegalArgumentException("Can't invoke method ("
							+ current.getJMethod() + ")", e);
				}
			}
		}

	}

	/**
	 * Factory method to close the entity manager in stateless session beans.
	 * 
	 * @author Daniel Wiese
	 * @since 18.09.2005
	 * @param toClose -
	 *            the session bean class with the entity manager to close
	 */
	public void destroySessionBean(T toClose) {
		try {
			if (this.introspector != null
					&& this.introspector.hasEntityManager()) {
				Property em = this.introspector.getEntityManagerField();
				EntityManager toCloseEM = (EntityManager) em.getField(toClose);
				log.info("Closing EntityManager");
				if (toCloseEM != null) {
					toCloseEM.close();
					this.manager = null;
				}
			}

		} catch (IllegalAccessException e) {
			log.error("IllegalAccessException", e);
			throw new RuntimeException(
					"Could not close the Entyty-Manager in the session bean");
		}
	}


	/**
	 * Creates an instance of the entity manager.
	 * 
	 * @author Daniel Wiese
	 * @since 18.09.2005
	 * @return - the entity manager
	 */
	public EntityManager createEntityManager() {
		if (manager != null) {
			return this.manager;
		} else {
			EntityManager manager = this.configuration
					.getEntityManagerFactory().createEntityManager();
			return manager;
		}
	}

}
