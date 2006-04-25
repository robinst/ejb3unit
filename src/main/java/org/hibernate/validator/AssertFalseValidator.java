//$Id: AssertFalseValidator.java,v 1.1 2006/04/17 12:11:07 daniel_wiese Exp $
package org.hibernate.validator;

import java.io.Serializable;


/**
 * Check if a given object is false or not
 *
 * @author Gavin King
 */
public class AssertFalseValidator implements Validator<AssertFalse>, Serializable {

	public boolean isValid(Object value) {
		return !(Boolean) value;
	}

	public void initialize(AssertFalse parameters) {
	}

}
