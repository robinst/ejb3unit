package com.bm.testsuite;

import java.util.Arrays;

import javax.persistence.EntityManager;

import com.bm.creators.SessionBeanFactory;
import com.bm.ejb3guice.inject.Inject;
import com.bm.introspectors.IIntrospector;
import com.bm.testsuite.dataloader.InitialDataSet;

/**
 * This is the base class for all JUnit test - testing stateless/statefull
 * SessionBeans (EJB 3.0 Specification conform).
 * 
 * @author Daniel Wiese
 * @param <T> -
 *            the type of the session bean to test
 * @since 16.10.2005
 */
public abstract class BaseSessionBeanFixture<T> extends BaseFixture {

	private final Class<T> beanClass;

	private T beanToTest = null;

	@Inject
	private SessionBeanFactory<T> sbFactory;

	/**
	 * Constructor.
	 * 
	 * @param sessionBeanToTest -
	 *            the class of the session bean to test
	 * @param usedEntityBeans -
	 *            the used entity bens
	 */
	public BaseSessionBeanFixture(Class<T> sessionBeanToTest,
			Class<?>[] usedEntityBeans) {
		this(sessionBeanToTest, usedEntityBeans, (InitialDataSet[]) null);

	}

	/**
	 * Constructor.
	 * 
	 * @param sessionBeanToTest -
	 *            the class of the session bean to test
	 * @param usedEntityBeans -
	 *            the used entity beans
	 * @param initialData -
	 *            the inital data to create in the db
	 */
	public BaseSessionBeanFixture(Class<T> sessionBeanToTest,
			Class<?>[] usedEntityBeans, InitialDataSet... initialData) {
		super(initialData);
		initInjector(Arrays.asList(usedEntityBeans));
		this.beanClass = sessionBeanToTest;

	}

	/**
	 * Protected Constructor> possible to pass another introspector
	 * 
	 * @param sessionBeanToTest -
	 *            the class of the session bean to test
	 * @param intro -
	 *            the introspector
	 * @param usedEntityBeans -
	 *            the used entity bens
	 * @param initialData -
	 *            the inital data to create in the db
	 */
	protected BaseSessionBeanFixture(Class<T> sessionBeanToTest,
			IIntrospector<T> intro, Class<?>[] usedEntityBeans,
			InitialDataSet... initialData) {
		this(sessionBeanToTest, usedEntityBeans, initialData);
		// This is strange - what happens with the introspector intro?
	}

	/**
	 * @author Daniel Wiese
	 * @since 16.10.2005
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.beanToTest = this.sbFactory.createSessionBean(this.beanClass);

	}

	/**
	 * Returns a isntance of a EntityManager.
	 * 
	 * @author Daniel Wiese
	 * @since 12.11.2005
	 * @return - a instance of an entity manager
	 */
	public EntityManager getEntityManager() {
		return getEntityManagerProv().get();
	}

	/**
	 * Returns the beanClass.
	 * 
	 * @return Returns the beanClass.
	 */
	public Class<T> getBeanClass() {
		return this.beanClass;
	}

	/**
	 * Returns the beanToTest.
	 * 
	 * @return Returns the beanToTest.
	 */
	public T getBeanToTest() {
		return this.beanToTest;
	}

	/**
	 * Returns the sbFactory.
	 * 
	 * @return Returns the sbFactory.
	 */
	public SessionBeanFactory<T> getSbFactory() {
		return this.sbFactory;
	}

}
