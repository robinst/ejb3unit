package com.bm.testsuite.mocked;

import java.util.Arrays;
import java.util.Map;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.persistence.PersistenceContext;

import org.jmock.Mockery;

import com.bm.creators.BeanCreationListener;
import com.bm.creators.MockedDIModuleCreator;
import com.bm.ejb3guice.inject.Ejb3Guice;
import com.bm.ejb3guice.inject.Injector;
import com.bm.ejb3guice.inject.Module;
import com.bm.ejb3guice.inject.Stage;
import com.bm.ejb3metadata.annotations.metadata.MetaDataCache;
import com.bm.testsuite.BaseTest;

/**
 * This class enables the testing of Seteless/Statefull Session beans and JBoss
 * Service classes under Mock-Isolation control. All JSR 220 (EJB 3) fields will
 * be injected as JMock objects.
 * 
 * @author Daniel Wiese
 * 
 * @param <T> -
 *            the type of the bena to test.
 */
public abstract class MockedSessionBeanFixture<T> extends BaseTest {

	private final Class<T> beanUnderTestClass;

	private T beanToTest = null;

	protected final Mockery context = new Mockery();

	private Map<Class<?>, Object> mockedDependencies = null;

	/**
	 * Default constructor.
	 * 
	 * @param beanToTest -
	 *            the bean to test.
	 */
	public MockedSessionBeanFixture(
			Class<T> beanToTest) {
		this.beanUnderTestClass = beanToTest;
	}

	/**
	 * Returns the mock controll to the given property.
	 * 
	 * @param interfaze -
	 *            the name of the property
	 * @return - the mock controll
	 */
	@SuppressWarnings("unchecked")
	protected <M> M getMock(Class<M> interfaze) {
		if (this.mockedDependencies != null) {
			return (M) this.mockedDependencies.get(interfaze);
		} else {
			return null;
		}
	}

	/**
	 * Returns the interface.
	 * @param <M> the interface to mock
	 * @return the mocked intance
	 */
	protected <M> M mock(Class<M> interfaze) {
		return context.mock(interfaze);
	}

	/**
	 * Returns the bean to test.
	 * 
	 * @return - the bean to test
	 */
	protected T getBeanToTest() {
		return this.beanToTest;
	}

	/**
	 * Sets a value for a field in the tested-bean instance.
	 * 
	 * @author Daniel Wiese
	 * @since 02.05.2006
	 * @param fieldName -
	 *            the name of the field
	 * @param toSet -
	 *            the value to set
	 */
	protected void setValueForField(String fieldName, Object toSet) {
		this.setValueForField(this.beanToTest, fieldName, toSet);
	}

	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		MockedDIModuleCreator module = MetaDataCache.getMockModuleCreator(beanUnderTestClass,
				context);
		this.beanToTest = createBeanIstance(module);
		this.mockedDependencies = module.getMocks();
	}

	@SuppressWarnings("unchecked")
	private T createBeanIstance(Module module) {

		// final T back = Ejb3Utils.getNewInstance(toCreate);
		Module[] mods = { module };
		BeanCreationListener createdbeans = new BeanCreationListener();
		Injector injector = Ejb3Guice.createInjector(Stage.PRODUCTION, Arrays.asList(mods),
				Ejb3Guice.markerToArray(EJB.class, Resource.class, PersistenceContext.class),
				createdbeans);
		final T instance = injector.getInstance(beanUnderTestClass);
		return instance;
	}

	/**
	 * @see junit.framework.TestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		this.beanToTest = null;
	}

}
