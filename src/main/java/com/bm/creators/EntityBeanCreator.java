package com.bm.creators;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.apache.log4j.Logger;

import com.bm.datagen.Generator;
import com.bm.datagen.empty.EmptyCollection;
import com.bm.datagen.random.RandomDateGenerator;
import com.bm.datagen.random.RandomIntegerGenerator;
import com.bm.datagen.random.RandomLongGenerator;
import com.bm.datagen.random.RandomStringGenerator;
import com.bm.datagen.random.primitive.PrimitiveRandomBooleanGenerator;
import com.bm.datagen.random.primitive.PrimitiveRandomDoubleGenerator;
import com.bm.datagen.random.primitive.PrimitiveRandomFloatGenerator;
import com.bm.datagen.random.primitive.PrimitiveRandomShortGenerator;
import com.bm.introspectors.EmbeddedClassIntrospector;
import com.bm.introspectors.EntityBeanIntrospector;

/**
 * This class creates entity beans with random data.
 * 
 * @author Daniel Wiese
 * @param <T> -
 *            the type of the entity beans which will be created
 * @since 07.10.2005
 */
public class EntityBeanCreator<T> {

	private static final Logger log = Logger.getLogger(EntityBeanCreator.class);

	private static final List<Generator<?>> DEFAULT_GENERATORS = new ArrayList<Generator<?>>();

	private final EntityBeanIntrospector<T> intro;

	private final EntityInstanceCreator<T> baseCreator;

	private final List<Generator<?>> currentGeneratorList = new ArrayList<Generator<?>>();

	private final EntityManager em;

	static {
		// dafault configuration
		DEFAULT_GENERATORS.add(new RandomDateGenerator());
		DEFAULT_GENERATORS.add(new PrimitiveRandomBooleanGenerator());
		DEFAULT_GENERATORS.add(new RandomIntegerGenerator());
		DEFAULT_GENERATORS.add(new RandomLongGenerator());
		DEFAULT_GENERATORS.add(new PrimitiveRandomFloatGenerator());
		DEFAULT_GENERATORS.add(new PrimitiveRandomShortGenerator());
		DEFAULT_GENERATORS.add(new RandomStringGenerator());
		DEFAULT_GENERATORS.add(new PrimitiveRandomDoubleGenerator());
		DEFAULT_GENERATORS.add(new EmptyCollection());
	}

	/**
	 * Deafult constructor.
	 * 
	 * @param toCreate -
	 *            the class to create
	 */
	public EntityBeanCreator(EntityManager em, Class<T> toCreate) {
		this(em, new EntityBeanIntrospector<T>(toCreate), toCreate);
	}

	/**
	 * Deafult constructor.
	 * 
	 * @param em
	 *            the current entity manager.
	 * @param intro -
	 *            a isntance of bean introspection
	 * @param toCreate -
	 *            the class to create
	 */
	public EntityBeanCreator(EntityManager em, EntityBeanIntrospector<T> intro,
			Class<T> toCreate) {
		this.em = em;
		this.intro = intro;
		this.currentGeneratorList.addAll(DEFAULT_GENERATORS);
		this.baseCreator = new EntityInstanceCreator<T>(em, intro, toCreate,
				this.currentGeneratorList);
	}

	/**
	 * Constructor with special generator list.
	 * 
	 * @param em
	 *            the current entity manager.
	 * @param intro -
	 *            a isntance of bean introspection
	 * @param toCreate -
	 *            the class to create
	 * @param additionalGenerators -
	 *            additional generators
	 */
	public EntityBeanCreator(EntityManager em, EntityBeanIntrospector<T> intro,
			Class<T> toCreate, List<Generator<?>> additionalGenerators) {
		this.em = em;
		this.intro = intro;
		this.currentGeneratorList.addAll(DEFAULT_GENERATORS);
		if (additionalGenerators != null) {
			this.currentGeneratorList.addAll(additionalGenerators);
		}
		this.baseCreator = new EntityInstanceCreator<T>(this.em, intro, toCreate,
				this.currentGeneratorList);
	}

	/**
	 * This method should be called every time before an entity creation task >
	 * this method tells every generator to call his prepare method(annotation).
	 */
	public void prepare() {
		this.baseCreator.prepare();
	}

	/**
	 * This method should be called every time before an entity creation task >
	 * this method tells every generator to call his clenup method (annotation).
	 */
	public void cleanup() {
		this.baseCreator.cleanup();
	}

	/**
	 * Creates a instance of the entyty bean (filled with random data) without
	 * any persistence operations.
	 * 
	 * @return -a random instance of the entity bean
	 */
	@SuppressWarnings("unchecked")
	public T createBeanInstance() {
		// for error messages
		final T back = this.baseCreator.createInstance();
		if (this.intro.hasEmbeddedPKClass()) {
			try {
				final EmbeddedClassIntrospector emInspector = this.intro
						.getEmbeddedPKClass();
				final EntityInstanceCreator<T> embCreator = new EntityInstanceCreator<T>(
						em, emInspector, emInspector.getEmbeddedClassName(),
						this.currentGeneratorList);
				final Object embeddedInstance = embCreator.createInstance();
				this.intro
						.setField(back, emInspector.getAttibuteName(), embeddedInstance);
			} catch (IllegalAccessException e) {
				log.error("Cannot create the Embedded PK-Class", e);
				throw new RuntimeException("Cannot create the Embedded PK-Class", e);
			}

		}

		return back;

	}

}
