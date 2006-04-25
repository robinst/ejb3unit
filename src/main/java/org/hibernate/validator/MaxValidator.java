//$Id: MaxValidator.java,v 1.1 2006/04/17 12:11:07 daniel_wiese Exp $
package org.hibernate.validator;

import java.io.Serializable;

import org.hibernate.mapping.Column;
import org.hibernate.mapping.Property;

/**
 * Do check a max restriction on a numeric (whether and actual number or its string representation,
 * and apply expected contraints on hibernate metadata.
 *
 * @author Gavin King
 */
public class MaxValidator implements Validator<Max>, PropertyConstraint, Serializable {

	private int max;

	public void initialize(Max parameters) {
		max = parameters.value();
	}

	public boolean isValid(Object value) {
		if ( value == null ) return true;
		if ( value instanceof String ) {
			try {
				double dv = Double.parseDouble( (String) value );
				return dv <= max;
			}
			catch (NumberFormatException nfe) {
				return false;
			}
		}
		else if ( ( value instanceof Double ) || ( value instanceof Float ) ) {
			double dv = ( (Number) value ).doubleValue();
			return dv <= max;
		}
		else if ( value instanceof Number ) {
			long lv = ( (Number) value ).longValue();
			return lv <= max;
		}
		else {
			return false;
		}
	}

	public void apply(Property property) {
		Column col = (Column) property.getColumnIterator().next();
		col.setCheckConstraint( col.getName() + "<=" + max );
	}
}
