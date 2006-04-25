//$Id: EmailValidator.java,v 1.1 2006/04/17 12:11:07 daniel_wiese Exp $
package org.hibernate.validator;

import java.io.Serializable;
import java.util.regex.Matcher;

/**
 * Check that a given string is a well-formed email address
 * @author Emmanuel Bernard
 */
public class EmailValidator implements Validator<Email>, Serializable {

	private java.util.regex.Pattern pattern;

	public boolean isValid(Object value) {
		if ( value == null ) return true;
		if ( !( value instanceof String ) ) return false;
		String string = (String) value;
		Matcher m = pattern.matcher( string );
		return m.matches();
	}

	public void initialize(Email parameters) {
		pattern = java.util.regex.Pattern.compile(
				"^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*$",
				java.util.regex.Pattern.CASE_INSENSITIVE
		);
	}
}
