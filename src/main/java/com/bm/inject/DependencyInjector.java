package com.bm.inject;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.bm.introspectors.AbstractIntrospector;
import com.bm.introspectors.IntrospectorFactory;
import com.bm.introspectors.Property;

/**
 * Injects objects specified by JSR 220 (EJB 3 specification). This class can be
 * used e.g. for isolated unit test injecting MOCK objects.
 * 
 * @author Daniel Wiese
 * @param <H> -
 *            the type of the handle
 * 
 */
public class DependencyInjector<H> {

	private static final Logger log = Logger
			.getLogger(DependencyInjector.class);

	/**
	 * If no specialized factory was found, the default factry will be used.
	 */
	private final DependencyInjectionFactory<H> defaultFactory;

	/**
	 * Additional factories are stored here.
	 */
	private final Map<Class, DependencyInjectionFactory<H>> additionalFactories = new HashMap<Class, DependencyInjectionFactory<H>>();

	/**
	 * Default constructor with the default dependency injection factory.
	 * 
	 * @param defaultFactory -
	 *            the dafault factory
	 */
	public DependencyInjector(DependencyInjectionFactory<H> defaultFactory) {
		this.defaultFactory = defaultFactory;
	}

	/**
	 * This method injects all relevant dependencies to the passed object. The
	 * method returs a set of handels, which can be accesed by the property name
	 * as key.
	 * 
	 * @param toInject -
	 *            the object where the dependency injection should be applied.
	 * @return a set with the injected property names as keys and a value of thy
	 *         H - the type of the handle
	 */
	public Map<String, Injection<H>> injectDependecies(Object toInject) {
		//TODOs: change to use guice
		final Map<String, Injection<H>> back = new HashMap<String, Injection<H>>();
		if (toInject != null) {
			Set<Property> fieldsToInject = null;
			AbstractIntrospector<?> introspector = IntrospectorFactory.createIntrospector(toInject.getClass());
			fieldsToInject = introspector.getFieldsToInject();

			if (fieldsToInject != null) {
				// there are fields to inject
				for (Property act : fieldsToInject) {
					this.injectSingleProperty(act, toInject, back);
				}
			}
		}

		return back;
	}

	/**
	 * Adds a another factory for a special type. If this type is found for
	 * injection this factory will produce the objects for injection. The handel
	 * must be of the same type like the dafault factory.
	 * 
	 * @author Daniel Wiese
	 * @since 07.02.2006
	 * @param forType -
	 *            the type for which the factory should be applied
	 * @param toAdd -
	 *            the factory to add.
	 */
	public void addFactory(Class forType, DependencyInjectionFactory<H> toAdd) {
		this.additionalFactories.put(forType, toAdd);
	}

	private void injectSingleProperty(Property prop, Object target,
			Map<String, Injection<H>> handels) {

		final DependencyInjectionFactory<H> factoryToUse = (this.additionalFactories
				.containsKey(prop.getType())) ? this.additionalFactories
				.get(prop.getType()) : this.defaultFactory;
		final Injection<H> toInject = factoryToUse.create(prop.getType());
		try {
			prop.setField(target, toInject.getCratedObject());
			handels.put(prop.getPropertyName(), toInject);
		} catch (IllegalAccessException e) {
			log.error("Can´t inject the field (" + prop.getPropertyName()
					+ ") of the class (" + prop.getType() + ")");
		}

	}
}
