package com.bm.testsuite.interfaces;

import javax.persistence.EntityManager;
import javax.sql.DataSource;

import com.bm.creators.SessionBeanFactory;

/**
 * @author Fabian Bauschulte
 *
 * @param <T>
 */
public interface IBaseSessionBeanFixture<T> extends IBaseFixture{


	/**
	 * Returns the beanClass.
	 * 
	 * @return Returns the beanClass.
	 */
	Class<T> getBeanClass();

	/**
	 * Returns the beanToTest.
	 * 
	 * @return Returns the beanToTest.
	 */
	T getBeanToTest();

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
	 * Returns the sbFactory.
	 * 
	 * @return Returns the sbFactory.
	 */
	SessionBeanFactory<T> getSbFactory();

}