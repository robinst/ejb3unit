package com.bm.creators;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.ejb.SessionContext;
import javax.ejb.TimerService;
import javax.persistence.EntityManager;
import javax.sql.DataSource;

import com.bm.cfg.Ejb3UnitCfg;
import com.bm.ejb3guice.inject.Binder;
import com.bm.ejb3guice.inject.Module;
import com.bm.utils.BasicDataSource;
import com.bm.utils.substitues.FakedSessionContext;
import com.bm.utils.substitues.MockedTimerService;

public class DynamicDIModuleCreator implements Module {

	private final Map<String, String> interface2implemantation;

	private final Ejb3UnitCfg conf;

	private final EntityManager manager;

	/**
	 * Constructor.
	 * 
	 * @param manager
	 *            the entity manager instance which should be used for the
	 *            binding
	 */
	public DynamicDIModuleCreator(Ejb3UnitCfg conf, EntityManager manager) {
		this.interface2implemantation = new HashMap<String, String>();
		this.conf = conf;
		this.manager = manager;
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
		// static standard bindings
		binder.bind(DataSource.class)
				.toInstance(new BasicDataSource(this.conf));
		binder.bind(EntityManager.class).toInstance(this.manager);
		binder.bind(SessionContext.class).to(FakedSessionContext.class);
		binder.bind(TimerService.class).to(MockedTimerService.class);

		for (String interfaze : interface2implemantation.keySet()) {
			try {
				Class<Object> interfazeCl = (Class<Object>) Thread
						.currentThread().getContextClassLoader().loadClass(
								interfaze.replace('/', '.'));
				Class<Object> implementationCl = (Class<Object>) Thread
						.currentThread().getContextClassLoader().loadClass(
								interface2implemantation.get(interfaze)
										.replace('/', '.'));
				binder.bind(interfazeCl).to(implementationCl);
			} catch (ClassNotFoundException e) {
				throw new RuntimeException("Can't load Local/Remote interface "
						+ interfaze);
			}
		}

	}
}
