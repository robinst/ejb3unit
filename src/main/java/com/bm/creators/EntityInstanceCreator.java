package com.bm.creators;

import java.util.List;

import org.apache.log4j.Logger;

import com.bm.datagen.DataGenerator;
import com.bm.datagen.Generator;
import com.bm.introspectors.Introspector;
import com.bm.introspectors.PersistentPropertyInfo;
import com.bm.introspectors.PrimaryKeyInfo;
import com.bm.introspectors.Property;
import com.bm.utils.Ejb3Utils;

/**
 * This class creates instances of different classes with random data, like
 * entity beans, inner classes etc..
 * 
 * @author Daniel Wiese
 * @param <T> -
 *            the type of the class to create
 * @since 07.10.2005
 */
public class EntityInstanceCreator<T> {

	private static final Logger log = Logger.getLogger(EntityInstanceCreator.class);

	private final Introspector<T> intro;

	private final Class<T> toCreate;

	private final DataGenerator dataGen;

	/**
	 * Default constructor.
	 * 
	 * @param intro -
	 *            different types of introspectors
	 * @param toCreate -
	 *            the class to create
	 * @param generators -
	 *            the generator list
	 */
	public EntityInstanceCreator(Introspector<T> intro, Class<T> toCreate,
			List<Generator> generators) {
		this.intro = intro;
		this.toCreate = toCreate;
		this.dataGen = new DataGenerator();
		// register current generators
		for (Generator actGen : generators) {
			this.dataGen.registerGen(actGen);
		}
	}

	/**
	 * This method should be called every time before an entity creation task >
	 * this method tells every generator to call his prepare method(annotation).
	 */
	public void prepare() {
		this.dataGen.prepare();
	}

	/**
	 * This method should be called every time before an entity creation task >
	 * this method tells every generator to call his clenup method (annotation).
	 */
	public void cleanup() {
		this.dataGen.cleanup();
	}

	/**
	 * Creates a instance of the class (filled with random data).
	 * 
	 * @return -a filled instance of the class (@see DataGenerator)
	 */
	public T createInstance() {
		// for error messages
		String aktFieldName = "unknown";
		try {
			final T back = Ejb3Utils.getNewInstance(this.toCreate);
			// iterate over all fields
			for (Property aktProperty : this.intro.getPersitentProperties()) {
				aktFieldName = aktProperty.getName();
				final PersistentPropertyInfo pfi = this.intro
						.getPresistentFieldInfo(aktProperty);
				// don�t not generate values for embedded class fields
				// and not for TABLE, SEQUENCE, IDENTITY, AUTO pk fields
				if (!pfi.isEmbeddedClass()
						&& shouldPkValueBeGenerated(aktProperty, this.intro)) {
					// delegate to the data generator
					this.setField(back, aktProperty, this.dataGen.getNextValue(
							aktProperty, this.intro, back));
				}

			}

			return back;
		} catch (IllegalAccessException e) {
			log.error("Can�t set the value to the NON TANSIENT field: "
					+ aktFieldName + "\n(Class: " + toCreate.getName()
					+ ") Perhaps it�s not marked as @Tansient!");
			log.error("Can�t create the entity bean", e);
			throw new RuntimeException("Can�t create the entity bean", e);
		} catch (IllegalArgumentException e) {
			log.error("Can�t create the entity bean", e);
			throw new RuntimeException("Can�t create the entity bean", e);
		} catch (SecurityException e) {
			throw new RuntimeException(
					"Insufficient access rights to create the entity bean ("
							+ this.toCreate.getName() + ")");
		}

	}

	/**
	 * Check if the property is a autogenerated PK class The TABLE strategy
	 * indicates that the persistence provider should assign identifiers using
	 * an underlying database table to ensure uniqueness. The SEQUENCE and
	 * IDENTITY strategies specify the use of a database sequence or identity
	 * column, respectively. AUTO indicates that the persistence provider should
	 * pick an appropriate strategy for the particular database. Specifying NONE
	 * indicates that no primary key generation by the persistence provider
	 * should occur, and that the application will be responsible for assigning
	 * the primary key--> ONLY IN THIS CASE A VALUE IS GENERATED
	 * 
	 * @param toCheck -
	 *            the property to check
	 * @param intro -
	 *            the introspector
	 * 
	 * @return - false if not autogenerated
	 */
	private boolean shouldPkValueBeGenerated(Property toCheck,
			Introspector<T> intro) {
		boolean back = true;
		if (intro.getPkFields().contains(toCheck)) {
			final PrimaryKeyInfo info = intro.getPrimaryKeyInfo(toCheck);
			if (info != null) {
				// is a pk field
				// Only pk wher he @GeneratedValue tag is present will not be
				// generatet
				if (info.getGenValue() != null) {
					back = false;
				}
			}

		}
		return back;
	}

	private void setField(T instance, Property toSet, Object value)
			throws IllegalAccessException {
		toSet.setField(instance, value);
	}

}
