//$Id: PropertyConstraint.java,v 1.1 2006/04/17 12:11:07 daniel_wiese Exp $
package org.hibernate.validator;

import org.hibernate.mapping.Property;

/**
 * Interface implemented by the validator
 * when a constraint may be represented in a
 * hibernate metadata property
 *
 * @author Gavin King
 */
public interface PropertyConstraint {

	public void apply(Property property);
}
