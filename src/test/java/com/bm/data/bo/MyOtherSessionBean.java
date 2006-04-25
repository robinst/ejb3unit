package com.bm.data.bo;

import javax.annotation.EJB;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;

/**
 * Session bean impl.
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
	 */
	public EntityManager getEm() {
		return this.manager;
	}

	/**
	 * Reference.
	 * 
	 * @see com.bm.data.bo.IMyOtherSessionBean#getSessionBean()
	 */
	public IMySessionBean getSessionBean() {
		return this.mySessionBean;
	}

	/**
	 * Operation which do somethig internal.
	 */
	public void executeOperation() {
		DataSource otherDS = this.mySessionBean.getDs();
		

	}

}
