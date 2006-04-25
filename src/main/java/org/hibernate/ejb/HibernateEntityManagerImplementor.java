//$Id: HibernateEntityManagerImplementor.java,v 1.1 2006/04/17 12:11:08 daniel_wiese Exp $
package org.hibernate.ejb;

import javax.persistence.PersistenceException;

import org.hibernate.HibernateException;

/**
 * @author Emmanuel Bernard
 */
public interface HibernateEntityManagerImplementor extends HibernateEntityManager {
	boolean isTransactionInProgress();
	public void throwPersistenceException(PersistenceException e);
	public void throwPersistenceException(HibernateException e);
}
