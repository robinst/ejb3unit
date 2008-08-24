package com.bm.ejb3data.bo;

import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.sql.DataSource;

/**
 * Simple session bean for testing Local and Remote annotations on beans (not just on the interfaces).
 */
@Stateless
@Local(IMySessionBean.class)
@Remote(IMySessionBean.class)
public class MySessionBeanWithLocalRemoteAnnotation implements IMySessionBean {

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
   * @see com.bm.ejb3data.bo.IMySessionBean#getDs()
   */
  public DataSource getDs() {
    return this.ds;
  }

  /**
   * Returns the entity manager.
   * 
   * @return Returns the em.
   * @see com.bm.ejb3data.bo.IMySessionBean#getEm()
   */
  public EntityManager getEm() {
    return this.manager;
  }

  /**
   * Returns a testlist.
   * 
   * @see com.bm.ejb3data.bo.IMySessionBean#getAllStocks()
   */
  @SuppressWarnings("unchecked")
  public List<StockWKNBo> getAllStocks() {
    final Query query = this.manager.createNamedQuery("StockWKNBo.allStocks");
    List<StockWKNBo> loaded = Collections.checkedList(query.getResultList(),
        StockWKNBo.class);
    return loaded;
  }
}
