package com.bm.datagen;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;

import org.apache.log4j.Logger;

import com.bm.datagen.annotations.FieldType;
import com.bm.datagen.annotations.GeneratorType;
import com.bm.datagen.utils.GeneratorDependencyInjector;
import com.bm.introspectors.Introspector;
import com.bm.introspectors.Property;
import com.bm.utils.Ejb3Utils;

/**
 * This is the base class for generating data. All custom generators are
 * registered here dynamically
 * 
 * @author Daniel Wiese
 * 
 */
public class DataGenerator {

	private static final String ALL_FIELDS = "$all";

	private static final Logger log = Logger.getLogger(DataGenerator.class);

	private final GeneratorDependencyInjector injector = new GeneratorDependencyInjector();

	private final Map<Class<?>, List<Generator<?>>> generators = new HashMap<Class<?>, List<Generator<?>>>();

	private final EntityManager em;
	
	public DataGenerator(EntityManager em) {
		this.em = em;
	}

	/**
	 * Register one generator to the set of generators.
	 * 
	 * @param toRegister -
	 *            the generator to register
	 */
	public void registerGen(Generator<?> toRegister) {
		if (toRegister == null) {
			throw new RuntimeException("The Generator to register is null");
		}

		GeneratorType gtype = this.getGeneratorTypeAnnotation(toRegister);

		if (gtype != null) {
			if (generators.containsKey(gtype.className())) {
				final List<Generator<?>> aktList = generators.get(gtype.className());
				aktList.add(toRegister);
			} else {
				final List<Generator<?>> aktList = new ArrayList<Generator<?>>();
				aktList.add(toRegister);
				generators.put(gtype.className(), aktList);
			}
		}
	}

	/**
	 * This method should be called every time before an entity creation task >
	 * this method tells every generator to call his prepare method(annotation).
	 */
	public void prepare() {
		this.injector.setEntityManager(this.em);
		Collection<List<Generator<?>>> tmp = this.generators.values();
		for (List<Generator<?>> allGenerators : tmp) {
			for (Generator<?> generator : allGenerators) {
				//first inject the current injectable fields
				this.injector.inject(generator);
				this.injector.callPrepare(generator);
			}
		}

	}

	/**
	 * This method should be called every time before an entity creation task >
	 * this method tells every generator to call his cleanup method
	 * (annotation).
	 */
	public void cleanup() {
		Collection<List<Generator<?>>> tmp = this.generators.values();
		for (List<Generator<?>> allGenerators : tmp) {
			for (Generator<?> generator : allGenerators) {
				this.injector.callCleanup(generator);
			}
		}
	}

	/**
	 * Return the value form the generator.
	 * 
	 * @param <T> -
	 *            the type of the bean
	 * @param em
	 *            the entity manager
	 * @param property -
	 *            the field/property for what the value should be generated
	 * @param introspector -
	 *            the current introspector (belonging to the class with the
	 *            field)
	 * @param instance -
	 *            the current instance
	 * @return - the new generated value
	 */
	public <T> Object getNextValue(Property property,
			Introspector<T> introspector, Object instance) {
		final Class<?> type = this.getType(property);
		if (generators.containsKey(type)) {
			final List<Generator<?>> actList = generators.get(type);
			int highestSpecialisation = -1;
			Generator<?> choosedGen = null;
			// search for the best generator > highest specialisation
			for (Generator<?> actGen : actList) {
				GeneratorType gtype = this.getGeneratorTypeAnnotation(actGen);
				int actSpecialisation = this.generatorMatcher(property, gtype,
						introspector);
				if (actSpecialisation > highestSpecialisation) {
					highestSpecialisation = actSpecialisation;
					choosedGen = actGen;
				}
			}
			// check if one generator was found
			if (choosedGen != null && highestSpecialisation >= 0) {
				// prepare everything for dependency injection in the generator
				this.injector.setInstance(instance);
				this.injector.setProperty(property);
				this.injector.setIntrospector(introspector);
				this.injector.setEntityManager(this.em);

				// inject
				this.injector.inject(choosedGen);

				// call the choosen generator
				return choosedGen.getValue();
			}
			log.error("The type (" + type.getName() + ") is registered, but the field ("
					+ property.getName() + ") has no generator");
			throw new RuntimeException("The type (" + type.getName()
					+ ") is registered, but the field (" + property.getName()
					+ ") has no generator");

		}
		// if no generator is registered, the value is null
		if (log.isDebugEnabled()) {
			log.debug("The type (" + type.getName()
					+ ") is not registered to a generator(Property:" + property.getName()
					+ ")");
		}

		return null;

	}

	/**
	 * This method checks if the choosen generator should be used for this field
	 * The highest integer return value will be used - the return value is a
	 * value for the specialisation of the generator
	 * 
	 * @param <T> -
	 *            the type of the bean
	 * 
	 * @param property -
	 *            the current field
	 * @param gtype -
	 *            the current generator type annotation (belongs to thecurent
	 *            generator)
	 * @param introspector -
	 *            the current instrospector (belonging to the class with the
	 *            field)
	 * @return -the specialisation metric (-1 no match, 0=basic specialization ,
	 *         e.g 3 high specialisation The highist specialisation of a
	 *         generator will be called
	 */
	private <T> int generatorMatcher(Property property, GeneratorType gtype,
			Introspector<T> introspector) {
		int back = -1;
		final Set<Property> pkFields = introspector.getPkFields();
		if (gtype.fieldType() == FieldType.ALL_TYPES) {
			if (gtype.field().length == 1 && gtype.field()[0].equals(ALL_FIELDS)) {
				// all types/ all fields
				back = 0;
			} else if (this.isFieldInList(property, gtype)) {
				// all types, conrete fields
				back = 1;
			}
		} else if (gtype.fieldType() == FieldType.PK_FIELDS
				&& pkFields.contains(property)) {
			if (gtype.field().length == 1 && gtype.field()[0].equals(ALL_FIELDS)) {
				// all types/ pk fields
				back = 2;
			} else if (this.isFieldInList(property, gtype)) {
				// all types, conrete fields
				back = 3;
			}
		} else if (gtype.fieldType() == FieldType.NON_PK_FIELDS
				&& !pkFields.contains(property)) {
			if (gtype.field().length == 1 && gtype.field()[0].equals(ALL_FIELDS)) {
				// all types/ all fields
				back = 2;
			} else if (this.isFieldInList(property, gtype)) {
				// all types, conrete fields
				back = 3;
			}
		}

		return back;
	}

	private boolean isFieldInList(Property property, GeneratorType gtype) {
		for (String aktFieldName : gtype.field()) {
			if (aktFieldName.equals(property.getName())) {
				return true;
			}
		}
		// not found
		return false;

	}

	private GeneratorType getGeneratorTypeAnnotation(Generator<?> actGenerator) {
		return Ejb3Utils.getGeneratorTypeAnnotation(actGenerator);
	}

	/**
	 * This method will do the transformation of primitive types if neccessary
	 * 
	 * @param aktField -
	 *            the field to inspect
	 * @return the declaring type (or primitive representant)
	 */
	private Class<?> getType(Property aktField) {
		return Ejb3Utils.getNonPrimitiveType(aktField);

	}

}
