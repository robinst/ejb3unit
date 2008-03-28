package com.bm.testsuite.junit4;

import java.util.List;

import javax.persistence.EntityManager;
import javax.sql.DataSource;

import org.junit.After;
import org.junit.Before;

import com.bm.jndi.Ejb3UnitJndiBinder;
import com.bm.testsuite.PoJoFixture;
import com.bm.testsuite.dataloader.InitialDataSet;
import com.bm.testsuite.interfaces.IPoJoFixture;

/**
 * JUnit4 enabled version of PoJoJUnit4Fixture (JUnit 3.x).
 * @author Fabian Bauschulte
 *
 */
public class PoJoJUnit4Fixture implements IPoJoFixture {

	private final class PoJoFixtureExtension extends PoJoFixture {
		private PoJoFixtureExtension(Class<?>[] usedEntityBeans) {
			super(usedEntityBeans);
		}

		private PoJoFixtureExtension(Class<?>[] usedEntityBeans,
				InitialDataSet... initialData) {
			super(usedEntityBeans, initialData);
		}

	}

	private final IPoJoFixture delegate;

	/**
	 * Constructor.
	 * 
	 * @param usedEntityBeans -
	 *            the used entity beans
	 */
	public PoJoJUnit4Fixture(Class<?>[] usedEntityBeans) {
		delegate = new PoJoFixtureExtension(usedEntityBeans);

	}

	/**
	 * Constructor.
	 * 
	 * @param usedEntityBeans -
	 *            the used entity beans
	 * @param initialData -
	 *            the initial data to create in the db
	 */
	public PoJoJUnit4Fixture(Class<?>[] usedEntityBeans,
			InitialDataSet... initialData) {
		delegate = new PoJoFixtureExtension(usedEntityBeans, initialData);

	}

	/**
	 * {@inheritDoc}
	 */
	public <T> void deleteAll(Class<T> clazz) {
		delegate.deleteAll(clazz);

	}

	/**
	 * {@inheritDoc}
	 */
	public <T> List<T> findAll(Class<T> clazz) {

		return delegate.findAll(clazz);
	}

	/**
	 * {@inheritDoc}
	 */
	public DataSource getDataSource() {

		return delegate.getDataSource();
	}

	/**
	 * {@inheritDoc}
	 */
	public EntityManager getEntityManager() {

		return delegate.getEntityManager();
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
	public <T> List<T> persist(List<T> complexObjectGraph) {

		return delegate.persist(complexObjectGraph);
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

}
