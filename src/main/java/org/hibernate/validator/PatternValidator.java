//$Id: PatternValidator.java,v 1.1 2006/04/17 12:11:07 daniel_wiese Exp $
package org.hibernate.validator;

import java.util.regex.Matcher;
import java.io.Serializable;

/**
 * check if a given element match the regular expression
 *
 * @author Gavin King
 */
public class PatternValidator implements Validator<Pattern>, Serializable {

	private java.util.regex.Pattern pattern;

	public void initialize(Pattern parameters) {
		pattern = java.util.regex.Pattern.compile(
				parameters.regex(),
				parameters.flags()
		);
	}

	public boolean isValid(Object value) {
		if ( value == null ) return true;
		if ( !( value instanceof String ) ) return false;
		String string = (String) value;
		Matcher m = pattern.matcher( string );
		return m.matches();
	}

}
