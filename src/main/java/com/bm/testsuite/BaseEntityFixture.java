package com.bm.testsuite;

import java.sql.DataTruncation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.apache.log4j.Logger;
import org.hibernate.exception.GenericJDBCException;

import com.bm.cfg.Ejb3UnitCfg;
import com.bm.creators.EntityBeanCreator;
import com.bm.datagen.Generator;
import com.bm.datagen.relation.EntityRelation;
import com.bm.ejb3guice.inject.Inject;
import com.bm.ejb3guice.inject.Injector;
import com.bm.ejb3guice.inject.Provider;
import com.bm.introspectors.EntityBeanIntrospector;
import com.bm.introspectors.Property;
import com.bm.utils.BeanEqualsTester;
import com.bm.utils.NullableSetter;
import com.bm.utils.SimpleGetterSetterTest;
import com.bm.utils.UndoScriptGenerator;
import com.bm.utils.injectinternal.InternalInjector;

/**
 * This class is the base executes for all entity beans the automated test
 * cases.
 * 
 * @author Daniel Wiese
 * @param <T> -
 *            the type of the entity bean
 * @since 07.10.2005
 */
public abstract class BaseEntityFixture<T> extends BaseTest {

	private static final Logger log = Logger.getLogger(BaseEntityFixture.class);

	private static final int BEANS_TO_CREATE = 50;

	private Class<T> baseClass;

	private final EntityBeanIntrospector<T> intro;

	private EntityBeanCreator<T> creator;

	/** this field is set before every test * */
	private UndoScriptGenerator<T> undo = null;

	private boolean lastTestRollbacked = false;

	private final List<Generator<?>> currentGenList;

	private final Injector injector;

	@Inject
	private Provider<EntityManager> manager;
	
	/**
	 * Default constructor.
	 * 
	 * @param entityToTest -
	 *            the entity to test
	 */
	public BaseEntityFixture(Class<T> entityToTest) {
		this(entityToTest, null, null);
	}

	/**
	 * Additional constructor.
	 * 
	 * @param entityToTest -
	 *            the entity to test
	 * @param additionalGenerators
	 *            -a dditional generators (plug in)
	 */
	@SuppressWarnings("unchecked")
	public BaseEntityFixture(Class<T> entityToTest, Generator[] additionalGenerators) {
		this(entityToTest, additionalGenerators, null);
	}

	/**
	 * Additional constructor.
	 * 
	 * @param entityToTest -
	 *            the entity to test
	 * @param additionalGenerators
	 *            -additional generators (plug in)
	 * @param referencedEntities
	 *            referenced persitence classes
	 */
	@SuppressWarnings("unchecked")
	public BaseEntityFixture(Class<T> entityToTest, Generator<?>[] additionalGenerators,
			Class<?>[] referencedEntities) {
		currentGenList = new ArrayList<Generator<?>>();
		final List<Class<?>> entitiesToTest = new ArrayList<Class<?>>();
		// add the current class
		entitiesToTest.add(entityToTest);
		if (referencedEntities != null && referencedEntities.length > 0) {
			entitiesToTest.addAll(Arrays.asList(referencedEntities));
		}

		// register additional generators
		if (additionalGenerators != null && additionalGenerators.length > 0) {
			for (Generator aktGen : additionalGenerators) {
				currentGenList.add(aktGen);
				// look for additional, related entity beans
				if (aktGen instanceof EntityRelation) {
					final EntityRelation<Object> er = (EntityRelation<Object>) aktGen;
					entitiesToTest.add(er.getUsedBeans());
				}
			}
		}

		injector = InternalInjector.createInternalInjector(entitiesToTest);
		this.baseClass = entityToTest;
		this.intro = new EntityBeanIntrospector<T>(this.baseClass);

	}

	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		injector.injectMembers(this);
		this.manager.get().clear();
		log.debug("Setting up BaseEntityTest");
		this.undo = new UndoScriptGenerator<T>(intro);
		this.lastTestRollbacked = false;
		this.creator = new EntityBeanCreator<T>(this.manager.get(), intro,
				this.baseClass, currentGenList);
		this.creator.prepare();
	}

	/**
	 * @see junit.framework.TestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		this.creator.cleanup();
		log.debug("Cleaning database from previous test");
		try {
			// only delete objects if the test was not rollbacked
			if (!this.lastTestRollbacked) {
				EntityManager entityManager = manager.get();
				entityManager.clear();
				if (Ejb3UnitCfg.getConfiguration().isInMemory()) {
					this.undo.deleteAllDataInAllUsedTables(entityManager);
				} else {
					// delete all beans in one single transaction
					EntityTransaction tx = entityManager.getTransaction();
					tx.begin();
					List<Object> createdObjects = this.undo.getCreatedObjects();
					for (Object beanToDelete : createdObjects) {
						Object attached = entityManager.merge(beanToDelete);
						entityManager.remove(attached);
					}

					// the transaction must be committed
					tx.commit();
				}
			} else {
				log.info("Nothing to cleanup because last test was rollbacked");
			}
			// close the manager
		} catch (RuntimeException e) {
			log.error("ATTENTION Could not delete all contents!!!!");
			log.error("Please execute the following SQL scripte manually:");
			log.error("-----------SQL Script begin-----------------");
			StringBuilder sb = new StringBuilder();
			sb.append("\n");
			List<String> undoStatements = (Ejb3UnitCfg.getConfiguration()
					.isInMemory()) ? this.undo.getOneDeleteAllStatement()
							: this.undo.getSQLUndoStatements();
			for (String sql : undoStatements) {
				sb.append(sql).append("\n");
			}
			log.error(sb.toString());
			log.error("-----------SQL Script end-------------------");
			// rethrow the error
			throw e;
		}
	}

	/**
	 * This test writes n random generated beans into the database.
	 * 
	 * @throws Exception -
	 *             in an error case
	 */
	public void testWrite() throws Exception {
		EntityManager entityManager = this.manager.get();
		EntityTransaction tx = entityManager.getTransaction();
		T created = null;
		try {
			tx.begin();

			for (int i = 0; i < BEANS_TO_CREATE; i++) {
				created = creator.createBeanInstance();
				entityManager.persist(created);
				undo.protokollCreate(created);
			}

			// the transaction must be committed
			tx.commit();
		} catch (RuntimeException e) {
			tx.rollback();
			this.lastTestRollbacked = true;
			// throw error only in an unknown exception case
			if (!this.analyseException(e, created)) {
				throw e;
			}
		}

	}

	/**
	 * This test tests the simpe getter-/ setter methods.
	 * 
	 */
	public void testGetterSetter() {
		EntityManager entityManager = this.manager.get();
		EntityTransaction tx = entityManager.getTransaction();
		T created = null;
		try {
			tx.begin();
			created = creator.createBeanInstance();
			final SimpleGetterSetterTest test = new SimpleGetterSetterTest(created);
			test.testGetterSetter();
		} finally {
			tx.rollback();
		}

	}

	/**
	 * This test writes n random generated beans into the database - all
	 * null-able fields are set to null.
	 * 
	 * @throws Exception -
	 *             in an error case
	 */
	public void testWriteWithNullFields() throws Exception {
		EntityManager entityManager = this.manager.get();
		EntityTransaction tx = entityManager.getTransaction();
		T created = null;
		try {
			tx.begin();

			for (int i = 0; i < BEANS_TO_CREATE; i++) {
				created = creator.createBeanInstance();
				NullableSetter.setFieldsToNull(created, this.intro);
				entityManager.persist(created);
				undo.protokollCreate(created);
			}

			// the transaction must be committed
			tx.commit();
		} catch (RuntimeException e) {
			tx.rollback();
			this.lastTestRollbacked = true;
			// throwan error only in an unknown exception case
			if (!this.analyseException(e, created)) {
				throw e;
			}
		}

	}

	/**
	 * This test writes n random generated beans into the database Reads all
	 * beans agin form the database and test if the reded beans are equal 1) by
	 * introspectin all persistent fields 2) by callung the eqals method.
	 * 
	 * Additionaly the hash-code implementation will be checked
	 * 
	 * @throws Exception -
	 *             in an error case
	 */
	public void testWriteRead() throws Exception {
		EntityManager entityManager = this.manager.get();
		EntityTransaction tx = entityManager.getTransaction();
		final List<T> beansCreated = new ArrayList<T>();
		final List<T> beansReaded = new ArrayList<T>();
		T created = null;
		try {
			tx.begin();

			for (int i = 0; i < BEANS_TO_CREATE; i++) {
				created = creator.createBeanInstance();
				entityManager.persist(created);
				undo.protokollCreate(created);
				beansCreated.add(created);
			}

			// the transaction must be committed
			tx.commit();

		} catch (RuntimeException e) {
			if (tx.isActive()) {
				tx.rollback();
			}
			this.lastTestRollbacked = true;
			// throwan error only in an unknown exception case
			if (!this.analyseException(e, created)) {
				throw e;
			}
		}

		// now read all the beans

		// now read the beans again from the database
		for (T toRead : beansCreated) {
			final T readed = entityManager.find(this.baseClass, this.intro
					.getPrimaryKey(toRead));
			beansReaded.add(readed);
		}

		// test if the readed collection is equal
		BeanEqualsTester.testEqualsOnSize(beansCreated, beansReaded);
		BeanEqualsTester.testEqualsOnPersistentFields(beansCreated, beansReaded,
				this.intro);
		BeanEqualsTester
				.testEqualsImplementationForEntityBeans(beansCreated, beansReaded);

	}

	/**
	 * Anylse the exception and react. If the exception is known generate a
	 * assert fail exception
	 * 
	 * @param e
	 *            the exception
	 * @param lastBean -
	 *            last bean or null
	 * @return true if the exception was anylyzed
	 */
	private boolean analyseException(Exception e, T lastBean) {
		boolean back = false;
		if (lastBean != null) {
			try {
				StringBuilder sb = new StringBuilder();
				List<Property> props = this.intro.getPersitentProperties();
				for (Property prop : props) {
					sb.append("Field (").append(prop.getName()).append(") Value: ")
							.append(prop.getField(lastBean));
					sb.append("\n");
				}
				log.info("Error writing bean: (" + lastBean.getClass().getName() + ")");
				log.info("Persistent fields values");
				log.info(sb.toString());
				log.error("Reason: ", e);
			} catch (Exception ignore) {
				log.info("Error writing a bean");
			}
		}
		if (e instanceof GenericJDBCException) {
			final GenericJDBCException eC = (GenericJDBCException) e;
			if (eC.getCause() instanceof DataTruncation) {
				log.info("Some fields in your entity bean class");
				log.info("are to long (or to short in the database)!");
			}
			assertTrue(eC.getLocalizedMessage(), false);
			back = true;
		}

		return back;
	}

}
