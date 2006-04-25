//$Id: CacheConcurrencyStrategy.java,v 1.1 2006/04/17 12:11:07 daniel_wiese Exp $
package org.hibernate.annotations;

/**
 * Cache concurrency strategy
 * @author Emmanuel Bernard
 */
public enum CacheConcurrencyStrategy {
	NONE,
	READ_ONLY,
	NONSTRICT_READ_WRITE,
	READ_WRITE,
	TRANSACTIONAL
}
