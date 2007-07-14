package com.bm.creators;

import java.util.Map;

import javax.ejb.SessionContext;
import javax.persistence.EntityManager;
import javax.sql.DataSource;

import com.bm.cfg.Ejb3UnitCfg;
import com.bm.ejb3guice.inject.Binder;
import com.bm.ejb3guice.inject.Module;
import com.bm.utils.BasicDataSource;
import com.bm.utils.FakedSessionContext;

public class DynamicDIModuleCreator implements Module {

	private final Map<String, String> interface2implemantation;

	private final Ejb3UnitCfg conf;

	private final EntityManager manager;

	/**
	 * Constructor.
	 * 
	 * @param interface2implemantation
	 *            the set of interfact/imlementation pairs for the current jar
	 *            file Known limitation: Sassion beans can depend only on
	 *            session beans in the same jar file, maybe update always this
	 *            map it a interface is not found!
	 * @param manager
	 *            the entity manager instance which should be used for the
	 *            binding
	 */
	public DynamicDIModuleCreator(Map<String, String> interface2implemantation,
			Ejb3UnitCfg conf, EntityManager manager) {
		this.interface2implemantation = interface2implemantation;
		// TODO Auto-generated constructor stub
		this.conf = conf;
		this.manager = manager;
	}

	@SuppressWarnings("unchecked")
	public void configure(Binder binder) {
		// static standard bindings
		binder.bind(DataSource.class)
				.toInstance(new BasicDataSource(this.conf));
		binder.bind(EntityManager.class).toInstance(this.manager);
		binder.bind(SessionContext.class).to(FakedSessionContext.class);

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
