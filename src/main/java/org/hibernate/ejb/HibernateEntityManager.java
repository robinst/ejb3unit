//$Id: HibernateEntityManager.java,v 1.1 2006/04/17 12:11:08 daniel_wiese Exp $
package org.hibernate.ejb;

import javax.persistence.EntityManager;

import org.hibernate.Session;

/**
 * @author Gavin King
 */
public interface HibernateEntityManager extends EntityManager {
	public Session getSession();
}
