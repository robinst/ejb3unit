package com.bm.inject;

import java.util.Arrays;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.persistence.PersistenceContext;

import junit.framework.TestCase;

import com.bm.creators.BeanCreationListener;
import com.bm.creators.MockedDIModuleCreator;
import com.bm.data.bo.AnnotatedFieldsSessionBean;
import com.bm.ejb3guice.inject.Ejb3Guice;
import com.bm.ejb3guice.inject.Injector;
import com.bm.ejb3guice.inject.Module;
import com.bm.ejb3guice.inject.Stage;
import com.bm.introspectors.MetaDataCache;

/**
 * JUnit test case.
 * 
 * @author Daniel Wiese
 * 
 */
public class DependencyInjectorTest extends TestCase {

	@SuppressWarnings("unchecked")
	private AnnotatedFieldsSessionBean createBeanIstance(Module module) {

		// final T back = Ejb3Utils.getNewInstance(toCreate);
		Module[] mods = { module };
		BeanCreationListener createdbeans = new BeanCreationListener();
		Injector injector = Ejb3Guice.createInjector(Stage.PRODUCTION, Arrays
				.asList(mods), Ejb3Guice.markerToArray(EJB.class,
				Resource.class, PersistenceContext.class), createdbeans);
		final AnnotatedFieldsSessionBean instance = injector
				.getInstance(AnnotatedFieldsSessionBean.class);
		return instance;
	}

	/**
	 * Test method.
	 */
	public void testInjector_injectMockObjects() {
		MockedDIModuleCreator module = MetaDataCache
				.getMockModuleCreator(AnnotatedFieldsSessionBean.class);
		final AnnotatedFieldsSessionBean sessionBeanToTest = createBeanIstance(module);
		assertNotNull(sessionBeanToTest.getDs());
		assertNotNull(sessionBeanToTest.getEm());
		assertNotNull(sessionBeanToTest.getSessionBean());

	}

}
