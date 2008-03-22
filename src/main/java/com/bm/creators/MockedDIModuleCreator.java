package com.bm.creators;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.ejb.SessionContext;
import javax.ejb.TimerService;
import javax.persistence.EntityManager;
import javax.sql.DataSource;

import org.jmock.Mockery;

import com.bm.ejb3guice.inject.Binder;
import com.bm.ejb3guice.inject.Module;

public class MockedDIModuleCreator implements Module {

	private final Mockery context;
	private Map<Class<?>, Object> interfacesMockControlls = new HashMap<Class<?>, Object>();

	private final Map<String, String> interface2implemantation;

	/**
	 * Constructor.
	 * 
	 * @param manager
	 *            the entity manager instance which should be used for the
	 *            binding
	 */
	public MockedDIModuleCreator(
			Mockery context) {
		this.context = context;
		this.interface2implemantation = new HashMap<String, String>();
	}

	/**
	 * Adds a map with interface impl. to the structure.
	 * 
	 * @author Daniel Wiese
	 * @since Jul 19, 2007
	 * @param toAdd
	 *            the map to add
	 */
	public void addInteface2ImplMap(Map<String, String> toAdd) {
		final Set<String> keySet = toAdd.keySet();
		for (String interfaze : keySet) {
			this.interface2implemantation.put(interfaze, toAdd.get(interfaze));
		}
	}

	@SuppressWarnings("unchecked")
	public void configure(Binder binder) {
		// do the standard bindings
		binder.bind(DataSource.class).toInstance(createMock(DataSource.class));
		binder.bind(EntityManager.class).toInstance(createMock(EntityManager.class));
		binder.bind(SessionContext.class).toInstance(createMock(SessionContext.class));
		binder.bind(TimerService.class).toInstance(createMock(TimerService.class));

		for (String interfaze : interface2implemantation.keySet()) {
			try {
				Class<Object> interfazeCl = (Class<Object>) Thread.currentThread()
						.getContextClassLoader().loadClass(interfaze.replace('/', '.'));
				binder.bind(interfazeCl).toInstance(createMock(interfazeCl));
			} catch (ClassNotFoundException e) {
				throw new RuntimeException("Can't load Local/Remote interface " + interfaze);
			}
		}

	}

	/**
	 * Liefert die map mit den interfaces und den mock controlls die erzeugt
	 * wurden
	 * 
	 * @return die interfaces und die mock controlls
	 */
	public Map<Class<?>, Object> getMocks() {
		return interfacesMockControlls;
	}

	public <T> T createMock(Class<T> forIterface) {
		T mock = this.context.mock(forIterface);
		this.interfacesMockControlls.put(forIterface, mock);
		return mock;
	}
}
