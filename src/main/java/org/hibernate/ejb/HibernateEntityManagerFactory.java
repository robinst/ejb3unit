//$Id: HibernateEntityManagerFactory.java,v 1.1 2006/04/17 12:11:08 daniel_wiese Exp $
package org.hibernate.ejb;

import java.io.Serializable;
import javax.persistence.EntityManagerFactory;

import org.hibernate.SessionFactory;

/**
 * @author Gavin King
 */
public interface HibernateEntityManagerFactory extends EntityManagerFactory, Serializable {
	public SessionFactory getSessionFactory();
}
