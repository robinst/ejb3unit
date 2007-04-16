package com.bm.data.bo;

import java.sql.SQLException;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;

/**
 * Example stateless session baen with a reference to a datasource.
 * 
 * @author Daniel Wiese
 * @since 08.11.2005
 */
@Stateless
public class MyOtherSessionBean implements IMyOtherSessionBean {

	private static final long serialVersionUID = 1L;

	@Resource(name = "java:/MSSqlDS")
	private DataSource ds;

	@PersistenceContext
	private EntityManager manager;

	@EJB
	private IMySessionBean mySessionBean;

	/**
	 * Returns the DataSource.
	 * 
	 * @author Daniel Wiese
	 * @since 08.11.2005
	 * @see IMyOtherSessionBean#getDs()
	 * @return the DataSource
	 */
	public DataSource getDs() {
		return this.ds;
	}

	/**
	 * Returns the entity manager.
	 * 
	 * @author Daniel Wiese
	 * @since 08.11.2005
	 * @see IMyOtherSessionBean#getEm()
	 * @return the entity manager.
	 */
	public EntityManager getEm() {
		return this.manager;
	}

	/**
	 * Reference to another session bean via dependency injection.
	 * 
	 * @see com.bm.data.bo.IMyOtherSessionBean#getSessionBean()
	 * @return the injected session bean.
	 */
	public IMySessionBean getSessionBean() {
		return this.mySessionBean;
	}

	/**
	 * Operation which do somethig internal.
	 */
	public void executeOperation() {
		DataSource otherDS = this.mySessionBean.getDs();
		try {
			otherDS.getConnection();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

	}

}
