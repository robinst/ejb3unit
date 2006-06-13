package com.bm.creators;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.EJB;
import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;

import org.apache.log4j.Logger;

import com.bm.cfg.Ejb3UnitCfg;
import com.bm.introspectors.JbossServiceIntrospector;
import com.bm.introspectors.Property;
import com.bm.introspectors.SessionBeanIntrospector;
import com.bm.utils.BasicDataSource;
import com.bm.utils.Ejb3Utils;
import com.bm.utils.FakedSessionContext;
import com.bm.utils.ImplementationDiscoverer;

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

	/**
	 * use a Thread-Global map for already constructed beans (interface,
	 * object).
	 */
	public static final ThreadLocal<Map<Class, Object>> alreadyConstructedBeans = new ThreadLocal<Map<Class, Object>>();

	/** remember the current entity manager * */
	private EntityManager manager = null;

	private static final Logger log = Logger
			.getLogger(SessionBeanFactory.class);

	private final SessionBeanIntrospector<T> introspector;

	private final ImplementationDiscoverer dicoverer = new ImplementationDiscoverer();

	private final Ejb3UnitCfg configuration;

	private final Class[] usedEntityBeans;

	/**
	 * Default constructor.
	 * 
	 * @param intro -
	 *            the introspector
	 * 
	 * @param usedEntityBeans -
	 *            the used entity beans for this test
	 */
	public SessionBeanFactory(SessionBeanIntrospector<T> intro,
			Class[] usedEntityBeans) {
		final List<Class<? extends Object>> usedEntityBeansC = new ArrayList<Class<? extends Object>>();
		this.usedEntityBeans = usedEntityBeans;
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
	public T createSessionBean(Class<T> toCreate) {
		try {
			final T back = toCreate.newInstance();

			// to avoid circular dependencies register this bean
			// as already constructed
			final List<Class> businessInterfaces = Ejb3Utils
					.getLocalRemoteInterfaces(toCreate);
			for (Class interf : businessInterfaces) {
				Map<Class, Object> map = alreadyConstructedBeans.get();
				if (map != null) {
					map.put(interf, back);
				} else {
					map = new HashMap<Class, Object>();
					map.put(interf, back);
					alreadyConstructedBeans.set(map);
				}
			}

			// now inject other instances
			this.injectFields(back);
			return back;
		} catch (InstantiationException e) {
			log.error("InstantiationException", e);
		} catch (IllegalAccessException e) {
			log.error("IllegalAccessException", e);
		}
		throw new RuntimeException("Could not create the session bean");
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
	 * This method injects all relevant dependencies
	 * 
	 * @author Daniel Wiese
	 * @since 18.09.2005
	 * @param toCreate -
	 *            the new session bean
	 */
	@SuppressWarnings("unchecked")
	private void injectFields(T toCreate) {

		final Set<Property> toInject = this.introspector.getFieldsToInject();

		try {
			for (Property akt : toInject) {
				if (this.introspector.getAnnotationForField(akt) instanceof PersistenceContext) {
					// remember the injected manager
					manager = this.createEntityManager();
					akt.setField(toCreate, manager);
				} else if (this.introspector.getAnnotationForField(akt) instanceof Resource) {
					// analyse different ressource types
					if (akt.getType().equals(DataSource.class)) {
						akt.setField(toCreate, new BasicDataSource(
								this.configuration));
					} else if (akt.getType().equals(SessionContext.class)){
						akt.setField(toCreate, new FakedSessionContext());
					}else {
						throw new RuntimeException(
								"Can´t inject a rossource of type: "
										+ akt.getType());
					}
				} else if (this.introspector.getAnnotationForField(akt) instanceof EJB) {
					// inject other EJB beans --> avoid circular dependencies
					// using a thread-global map (one map for every thread)
					Map<Class, Object> alreadyConstructedBeansMap = alreadyConstructedBeans
							.get();
					if (alreadyConstructedBeansMap == null) {
						alreadyConstructedBeansMap = new HashMap<Class, Object>();
						alreadyConstructedBeans.set(alreadyConstructedBeansMap);
					}
					if (alreadyConstructedBeansMap.containsKey(akt.getType())) {
						akt.setField(toCreate, alreadyConstructedBeansMap
								.get(akt.getType()));
					} else {
						Class implementation = this.dicoverer
								.findImplementation(akt.getType());
						log.debug("Using: Local/Remote Interface ("
								+ akt.getType() + ") -->Implemetation ("
								+ implementation + ")");
						// get the right introspector
						final SessionBeanIntrospector myIntro = this
								.getRightIntrospector(implementation);
						final SessionBeanFactory mySbFac = new SessionBeanFactory(
								myIntro, this.usedEntityBeans);
						final Object createdSessionBean = mySbFac
								.createSessionBean(implementation);
						alreadyConstructedBeansMap.put(implementation,
								createdSessionBean);
						akt.setField(toCreate, createdSessionBean);

					}

				}
			}
		} catch (IllegalAccessException e) {
			log.error("Can´t create session bean", e);
			throw new RuntimeException("Can´t create session bean", e);
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

	@SuppressWarnings("unchecked")
	private SessionBeanIntrospector getRightIntrospector(Class forClass) {
		if (SessionBeanIntrospector.accept(forClass)) {
			return new SessionBeanIntrospector(forClass);
		} else if (JbossServiceIntrospector.accept(forClass)) {
			return new JbossServiceIntrospector(forClass);
		} else {
			throw new RuntimeException("No introspector fond for class: "
					+ forClass);
		}
	}

}
