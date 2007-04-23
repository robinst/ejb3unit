package com.bm.creators;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;

import org.apache.log4j.Logger;

import com.bm.cfg.Ejb3UnitCfg;
import com.bm.ejb3metadata.annotations.metadata.MethodAnnotationMetadata;
import com.bm.introspectors.AbstractIntrospector;
import com.bm.introspectors.JbossServiceIntrospector;
import com.bm.introspectors.MDBIntrospector;
import com.bm.introspectors.Property;
import com.bm.introspectors.SessionBeanIntrospector;
import com.bm.utils.BasicDataSource;
import com.bm.utils.Ejb3Utils;
import com.bm.utils.FakedSessionContext;

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

	private final AbstractIntrospector<T> introspector;

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
	public SessionBeanFactory(AbstractIntrospector<T> intro,
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
		final T back = Ejb3Utils.getNewInstance(toCreate);

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
		this.executeLifeCycleCreateMethods(back);
		return back;
	}
	
	/**
	 * Factory method to create stateless session beans with no dependencies.
	 * 
	 * @author Daniel Wiese
	 * @since 18.09.2005
	 * @param toCreate -
	 *            the class to create
	 * @return - the created session bean class for local usage
	 */
	public T createSessionBeanNoDependencies(Class<T> toCreate) {
		final T back = Ejb3Utils.getNewInstance(toCreate);

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

		return back;
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
	 * This method injects all relevant dependencies
	 * 
	 * @author Daniel Wiese
	 * @since 18.09.2005
	 * @param toCreate -
	 *            the new session bean
	 */
	@SuppressWarnings("unchecked")
	public void injectFields(T toCreate) {

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
					} else if (akt.getType().equals(SessionContext.class)) {
						akt.setField(toCreate, new FakedSessionContext());
					} else {
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
						String implementationName = this.introspector
								.getClassMetaData()
								.getEjbJarAnnotationMetadata()
								.getBeanImplementationForInterface(
										akt.getType());
						log.debug("Using: Local/Remote Interface ("
								+ akt.getType() + ") -->Implemetation ("
								+ implementationName + ")");
						Class<?> implementation = null;
						try {
							implementation = Thread.currentThread()
									.getContextClassLoader().loadClass(
											implementationName
													.replace('/', '.'));
						} catch (ClassNotFoundException e) {
							throw new RuntimeException("Class ("
									+ implementationName + ") not found");
						}
						// get the right introspector
						final AbstractIntrospector<T> myIntro = this
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
	private AbstractIntrospector<T> getRightIntrospector(Class forClass) {
		if (SessionBeanIntrospector.accept(forClass)) {
			return new SessionBeanIntrospector(forClass);
		} else if (JbossServiceIntrospector.accept(forClass)) {
			return new JbossServiceIntrospector(forClass);
		} else if (MDBIntrospector.accept(forClass)) {
			return new MDBIntrospector(forClass);
		} else {
			throw new RuntimeException("No introspector fond for class: "
					+ forClass);
		}
	}

}
