package com.bm.testsuite.dataloader;

import javax.persistence.EntityManager;

import com.bm.introspectors.EntityBeanIntrospector;
import com.bm.utils.UndoScriptGenerator;

/**
 * 
 * Represents an initial dataset for entity beans.
 * 
 * @author Daniel Wiese
 * @param <T> -
 *            der typ des Entity Beans welches initial angelegt wird.
 * 
 */
public abstract class EntityInitialDataSet<T> implements InitialDataSet {

	private EntityManager em = null;

	private final UndoScriptGenerator<T> undo;

	/**
	 * Constructor.
	 * 
	 * @param entityType -
	 *            die klasse der entity benas die initial angelegt werden
	 *            sollen.
	 */
	public EntityInitialDataSet(Class<T> entityType) {
		undo = new UndoScriptGenerator<T>(new EntityBeanIntrospector<T>(entityType));
	}

	/**
	 * Will be called by the testing framework.
	 * 
	 * @param em -
	 *            the entity manager
	 */
	public void setEntityManager(EntityManager em) {
		this.em = em;
	}

	/**
	 * The inherited class can call this method to add some data.
	 * 
	 * @param toAdd -
	 *            entyties to add;
	 */
	protected void add(T toAdd) {
		em.persist(toAdd);
	}

	/**
	 * Deletes the data.
	 * 
	 * @param ds -
	 *            the datasource.
	 * @author Daniel Wiese
	 * @since 17.04.2006
	 * @see com.bm.testsuite.dataloader.InitialDataSet#cleanup(EntityManager)
	 */
	public void cleanup(EntityManager em) {
		undo.deleteAllDataInAllUsedTables(em);

	}

}
