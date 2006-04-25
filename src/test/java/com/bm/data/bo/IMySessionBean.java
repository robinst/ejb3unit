package com.bm.data.bo;

import java.io.Serializable;
import java.util.List;

import javax.ejb.Local;
import javax.persistence.EntityManager;
import javax.sql.DataSource;

/**
 * Local Interface.
 * @author Daniel Wiese
 * @since 08.11.2005
 */
@Local
public interface IMySessionBean extends Serializable {
	
	/**
	 * Returns a testlist. 
	 * @author Daniel Wiese
	 * @since 17.04.2006
	 * @return - a test list
	 */
	List<StockWKNBo> getAllStocks();

	/**
	 * Returns the ds.
	 * 
	 * @return Returns the ds.
	 */
	DataSource getDs();

	/**
	 * Returns the em.
	 * 
	 * @return Returns the em.
	 */
	EntityManager getEm();

}
