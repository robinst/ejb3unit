//$Id: MinValidator.java,v 1.1 2006/04/17 12:11:07 daniel_wiese Exp $
package org.hibernate.validator;

import java.io.Serializable;

import org.hibernate.mapping.Column;
import org.hibernate.mapping.Property;

/**
 * Do check a min restriction on a numeric (whether and actual number or its string representation,
 * and apply expected contraints on hibernate metadata.
 *
 * @author Gavin King
 */
public class MinValidator implements Validator<Min>, PropertyConstraint, Serializable {

	private int min;

	public void initialize(Min parameters) {
		min = parameters.value();
	}

	public boolean isValid(Object value) {
		if ( value == null ) return true;
		if ( value instanceof String ) {
			try {
				double dv = Double.parseDouble( (String) value );
				return dv >= min;
			}
			catch (NumberFormatException nfe) {
				return false;
			}
		}
		else if ( ( value instanceof Double ) || ( value instanceof Float ) ) {
			double dv = ( (Number) value ).doubleValue();
			return dv >= min;
		}
		else if ( value instanceof Number ) {
			long lv = ( (Number) value ).longValue();
			return lv >= min;
		}
		else {
			return false;
		}
	}

	public void apply(Property property) {
		Column col = (Column) property.getColumnIterator().next();
		col.setCheckConstraint( col.getName() + ">=" + min );
	}
}
