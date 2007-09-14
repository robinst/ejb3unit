package com.bm.data.bo;

import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.sql.DataSource;

/**
 * Session bean impl.
 * 
 * @author Daniel Wiese
 * @since 08.11.2005
 */
@Stateless
public class MySessionBean implements IMySessionBean {

	private static final long serialVersionUID = 1L;

	@Resource(name = "java:/MSSqlDS")
	private DataSource ds;

	@Resource
	private SessionContext ctx;

	@PersistenceContext
	private EntityManager manager;

	/**
	 * Returns the DataSource.
	 * 
	 * @return Returns the ds.
	 * @author Daniel Wiese
	 * @since 08.11.2005
	 * @see com.bm.data.bo.IMySessionBean#getDs()
	 */
	public DataSource getDs() {
		return this.ds;
	}

	/**
	 * Returns the entity manager.
	 * 
	 * @return Returns the em.
	 * @author Daniel Wiese
	 * @since 08.11.2005
	 * @see com.bm.data.bo.IMySessionBean#getEm()
	 */
	public EntityManager getEm() {
		return this.manager;
	}

	/**
	 * Returns a testlist.
	 * 
	 * @author Daniel Wiese
	 * @since 17.04.2006
	 * @return - a test list
	 * @see com.bm.data.bo.IMySessionBean#getAllStocks()
	 */
	@SuppressWarnings("unchecked")
	public List<StockWKNBo> getAllStocks() {
		final Query query = this.manager.createNamedQuery("StockWKNBo.allStocks");
		List<StockWKNBo> loaded = Collections.checkedList(query.getResultList(),
				StockWKNBo.class);
		return loaded;
	}

	/**
	 * Returns the ctx.
	 * 
	 * @return Returns the ctx.
	 */
	public SessionContext getCtx() {
		return ctx;
	}

	/**
	 * Pesists the passed instance.
	 * 
	 * @param createBeanInstance
	 *            entity manager instance
	 */
	public void saveEntityBean(ExpertiseAreas createBeanInstance) {
		manager.persist(createBeanInstance);
	}

}
