//$Id: RangeValidator.java,v 1.1 2006/04/17 12:11:07 daniel_wiese Exp $
package org.hibernate.validator;

import java.io.Serializable;

import org.hibernate.mapping.Column;
import org.hibernate.mapping.Property;

/**
 * The value has to be in a defined range, the constraint is also applied on DB
 *
 * @author Gavin King
 */
public class RangeValidator implements Validator<Range>, PropertyConstraint, Serializable {
	private long max;
	private long min;

	public void initialize(Range parameters) {
		max = parameters.max();
		min = parameters.min();
	}

	public boolean isValid(Object value) {
		if ( value == null ) return true;
		if ( value instanceof String ) {
			try {
				double dv = Double.parseDouble( (String) value );
				return dv >= min && dv <= max;
			}
			catch (NumberFormatException nfe) {
				return false;
			}
		}
		else if ( ( value instanceof Double ) || ( value instanceof Float ) ) {
			double dv = ( (Number) value ).doubleValue();
			return dv >= min && dv <= max;
		}
		else if ( value instanceof Number ) {
			long lv = ( (Number) value ).longValue();
			return lv >= min && lv <= max;
		}
		else {
			return false;
		}
	}

	public void apply(Property property) {
		Column col = (Column) property.getColumnIterator().next();
		String check = "";
		if ( min != Long.MIN_VALUE ) check += col.getName() + ">=" + min;
		if ( max != Long.MAX_VALUE && min != Long.MIN_VALUE ) check += " and ";
		if ( max != Long.MAX_VALUE ) check += col.getName() + "<=" + max;
		col.setCheckConstraint( check );
	}

}
