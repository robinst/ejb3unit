package com.bm.testsuite.dataloader;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.persistence.EntityManager;
import javax.sql.DataSource;

import org.apache.log4j.Logger;

import com.bm.introspectors.EntityBeanIntrospector;
import com.bm.utils.SQLUtils;
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

	private static final Logger log = Logger
			.getLogger(EntityInitialDataSet.class);

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
		undo = new UndoScriptGenerator<T>(new EntityBeanIntrospector<T>(
				entityType));
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
		em.merge(toAdd);
		// em.persist(toAdd);
	}

	/**
	 * Deletes the data.
	 * 
	 * @param ds -
	 *            the datasource.
	 * @author Daniel Wiese
	 * @since 17.04.2006
	 * @see com.bm.testsuite.dataloader.InitialDataSet#cleanup(javax.sql.DataSource)
	 */
	public void cleanup(DataSource ds) {
		Connection con = null;
		PreparedStatement ps = null;
		try {
			con = ds.getConnection();
			ps = con.prepareStatement(undo.getDeleteAllStatement());
			ps.execute();
		} catch (SQLException e) {
			log.error("Cant claenup", e);
			throw new RuntimeException(e);
		} finally {
			SQLUtils.cleanup(con, ps);
		}

	}

}
