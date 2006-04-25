//$Id: HibernateQuery.java,v 1.1 2006/04/17 12:11:08 daniel_wiese Exp $
package org.hibernate.ejb;

import javax.persistence.Query;

public interface HibernateQuery extends Query{
	public org.hibernate.Query getHibernateQuery();
}
