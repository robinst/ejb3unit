package com.bm.testsuite.interfaces;

import java.util.List;

import javax.persistence.EntityManager;
import javax.sql.DataSource;

/**
 * @author Fabian Bauschulte
 *
 */
public interface IPoJoFixture extends IBaseFixture {

	
	/**
	 * Deletes all rows of the given class in the database.
	 * 
	 * @param <T>
	 *            the tyme of the persistent object
	 * @param clazz
	 *            the class of of the persistent object
	 * @return all DB instances
	 */
	<T> void deleteAll(Class<T> clazz);

	/**
	 * Find all rows of the given class in the database.
	 * 
	 * @param <T>
	 *            the tyme of the persistent object
	 * @param clazz
	 *            the class of of the persistent object
	 * @return all DB instances
	 */
	<T> List<T> findAll(Class<T> clazz);

	/**
	 * Liefert die datasource.
	 * 
	 * @return die data source.
	 */
	DataSource getDataSource();
	
	/**
	 * Returns a isntance of a EntityManager.
	 * 
	 * @author Daniel Wiese
	 * @since 12.11.2005
	 * @return - a instance of an entity manager
	 */
	EntityManager getEntityManager();

	/**
	 * Persists all objects in the database.
	 * 
	 * @param <T>
	 *            the tyme of the persistent object
	 * @param complexObjectGraph
	 *            th egraph to persist
	 * @return the persisted objects
	 */
	<T> List<T> persist(List<T> complexObjectGraph);

}