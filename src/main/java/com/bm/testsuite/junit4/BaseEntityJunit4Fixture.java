package com.bm.testsuite.junit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.bm.datagen.Generator;
import com.bm.jndi.Ejb3UnitJndiBinder;
import com.bm.testsuite.BaseEntityFixture;
import com.bm.testsuite.interfaces.IBaseEntityFixture;

/**
 * JUnit4 enabled version of BaseEntityFixture (JUnit 3.x).
 * 
 * @author Fabian Bauschulte
 *
 * @param <T>
 */
public class BaseEntityJunit4Fixture<T> implements IBaseEntityFixture<T> {

	private final class BaseEntityFixtureExtension extends BaseEntityFixture<T> {
		public BaseEntityFixtureExtension(Class<T> entityToTest) {
			super(entityToTest);

		}

		public BaseEntityFixtureExtension(Class<T> entityToTest,
				Generator<?>[] additionalGenerators) {
			super(entityToTest, additionalGenerators);

		}

		private BaseEntityFixtureExtension(Class<T> entityToTest,
				Generator<?>[] additionalGenerators,
				Class<?>[] referencedEntities) {
			super(entityToTest, additionalGenerators, referencedEntities);
		}
	}

	private final IBaseEntityFixture<T> delegate;

	/**
	 * Default constructor.
	 * 
	 * @param entityToTest -
	 *            the entity to test
	 */
	public BaseEntityJunit4Fixture(Class<T> entityToTest) {
		delegate = new BaseEntityFixtureExtension(entityToTest);
	}

	/**
	 * Additional constructor.
	 * 
	 * @param entityToTest -
	 *            the entity to test
	 * @param additionalGenerators
	 *            -a dditional generators (plug in)
	 */
	public BaseEntityJunit4Fixture(Class<T> entityToTest,
			Generator<?>[] additionalGenerators) {
		delegate = new BaseEntityFixtureExtension(entityToTest,
				additionalGenerators);
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
	public BaseEntityJunit4Fixture(Class<T> entityToTest,
			Generator<?>[] additionalGenerators, Class<?>[] referencedEntities) {
		delegate = new BaseEntityFixtureExtension(entityToTest,
				additionalGenerators, referencedEntities);
	}

	/**
	 * {@inheritDoc}
	 */
	public Ejb3UnitJndiBinder getJndiBinder() {

		return delegate.getJndiBinder();
	}

	/**
	 * {@inheritDoc}
	 */
	@Before
	public void setUp() throws Exception {
		delegate.setUp();

	}

	/**
	 * {@inheritDoc}
	 */
	public void setValueForField(Object forObject, String fieldName,
			Object toSet) {
		delegate.setValueForField(forObject, fieldName, toSet);

	}

	/**
	 * {@inheritDoc}
	 */
	@After
	public void tearDown() throws Exception {
		delegate.tearDown();

	}

	/**
	 * {@inheritDoc}
	 */
	@Test
	public void testGetterSetter() {
		delegate.testGetterSetter();

	}

	/**
	 * {@inheritDoc}
	 */
	@Test
	public void testWrite() throws Exception {
		delegate.testWrite();

	}

	/**
	 * {@inheritDoc}
	 */
	@Test
	public void testWriteRead() throws Exception {
		delegate.testWriteRead();

	}

	/**
	 * {@inheritDoc}
	 */
	@Test
	public void testWriteWithNullFields() throws Exception {
		delegate.testWriteWithNullFields();

	}

}
