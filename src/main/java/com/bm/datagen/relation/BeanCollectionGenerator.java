package com.bm.datagen.relation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;

import com.bm.creators.EntityBeanCreator;
import com.bm.datagen.Generator;
import com.bm.datagen.annotations.ForInstance;
import com.bm.datagen.annotations.ForProperty;
import com.bm.datagen.annotations.UsedIntrospector;
import com.bm.introspectors.EntityBeanIntrospector;
import com.bm.introspectors.Introspector;
import com.bm.introspectors.PersistentPropertyInfo;
import com.bm.introspectors.Property;
import com.bm.introspectors.releations.EntityReleationInfo;
import com.bm.introspectors.releations.OneToManyReleation;
import com.bm.introspectors.releations.RelationType;

/**
 * Generates <code>N</code> entity beans (using own configurator settings) to
 * enable ben creation for beans wich are using 1:M or N:M relations.
 * 
 * @author Daniel Wiese
 * @param <T> -
 *            the type of the generator
 * 
 */
public class BeanCollectionGenerator<T> implements Generator<Collection<T>>,
		EntityRelation<T> {

	private static final Logger log = Logger
			.getLogger(BeanCollectionGenerator.class);

	private final EntityBeanIntrospector<T> intro;

	private final EntityBeanCreator<T> creator;

	private final int beansToCreate;

	private final Class<T> beanType;

	@ForInstance
	private T entityInstance;

	@ForProperty
	private Property forProperty;

	@UsedIntrospector
	private Introspector<Object> introspector;

	/**
	 * Contructor using additional generator (for this generator).
	 * 
	 * @param beanToCreate -
	 *            the bean to create
	 * @param additionalGenerators -
	 *            if the beand creation sould have some special rules (creators)
	 * @param beansToCreate -
	 *            beans to create
	 */
	public BeanCollectionGenerator(Class<T> beanToCreate,
			Generator[] additionalGenerators, int beansToCreate) {
		this.beanType = beanToCreate;
		// register additional generators
		final List<Generator> currentGenList = new ArrayList<Generator>();
		for (Generator aktGen : additionalGenerators) {
			currentGenList.add(aktGen);
		}

		this.intro = new EntityBeanIntrospector<T>(beanToCreate);
		this.creator = new EntityBeanCreator<T>(intro, beanToCreate,
				currentGenList);
		this.beansToCreate = beansToCreate;
	}

	/**
	 * Default constructor.
	 * 
	 * @param beanToCreate -
	 *            the bean to create
	 * @param beansToCreate -
	 *            bean to create
	 */
	public BeanCollectionGenerator(Class<T> beanToCreate, int beansToCreate) {
		this.intro = new EntityBeanIntrospector<T>(beanToCreate);
		this.creator = new EntityBeanCreator<T>(intro, beanToCreate);
		this.beanType = beanToCreate;
		this.beansToCreate = beansToCreate;
	}

	/**
	 * Returns the next entity bean of type T.
	 * 
	 * @see com.bm.datagen.Generator#getValue()
	 */
	public Collection<T> getValue() {
		final List<T> back = new ArrayList<T>();
		final PersistentPropertyInfo pi = introspector
				.getPresistentFieldInfo(forProperty);
		for (int i = 0; i < this.beansToCreate; i++) {
			T created = this.creator.createBeanInstance();
			if (pi.isReleation()) {
				final EntityReleationInfo er = pi.getEntityReleationInfo();
				if (er.getReleationType() == RelationType.OneToMany) {

					OneToManyReleation o2m = (OneToManyReleation) er;
					try {
						o2m.getTargetProperty().setField(created,
								this.entityInstance);
					} catch (IllegalAccessException e) {
						log.error("OneToMany rel.: Can�t set the property: "
								+ o2m.getTargetProperty().getName());
						throw new RuntimeException(
								"OneToMany rel.: Can�t set the property: "
										+ o2m.getTargetProperty().getName());
					}
				}

			}
			back.add(created);
		}
		return back;
	}

	/**
	 * Returns the releated bean: The Type of the bean wich is generated by this
	 * generator.
	 * 
	 * @see com.bm.datagen.relation.EntityRelation#getUsedBeans()
	 */
	public Class<T> getUsedBeans() {
		// only one in this case
		return this.beanType;
	}
}
