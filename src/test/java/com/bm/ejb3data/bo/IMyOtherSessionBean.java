package com.bm.ejb3data.bo;

import java.io.Serializable;

import javax.ejb.Remote;
import javax.persistence.EntityManager;
import javax.sql.DataSource;

/**
 * Local Interface.
 * 
 * @author Daniel Wiese
 * @since 08.11.2005
 */
@Remote
public interface IMyOtherSessionBean extends Serializable {

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

	/**
	 * rturns a reference to a other sb.
	 * 
	 * @return reference to a other sb.
	 */
	IMySessionBean getSessionBean();

}
