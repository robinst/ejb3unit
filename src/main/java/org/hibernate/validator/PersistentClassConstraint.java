//$Id: PersistentClassConstraint.java,v 1.1 2006/04/17 12:11:07 daniel_wiese Exp $
package org.hibernate.validator;

import org.hibernate.mapping.PersistentClass;

/**
 * Interface implemented by the validator
 * when a constraint may be represented in the
 * hibernate metadata
 *
 * @author Gavin King
 */
public interface PersistentClassConstraint {
	/**
	 * Apply the constraint in the hibernate metadata
	 *
	 * @param persistentClass
	 */
	public void apply(PersistentClass persistentClass);
}
