//$Id: PropertyInferredData.java,v 1.1 2006/04/17 12:11:10 daniel_wiese Exp $
package org.hibernate.cfg;

import org.hibernate.MappingException;
import org.hibernate.annotations.AccessType;
import org.hibernate.reflection.XClass;
import org.hibernate.reflection.XProperty;

/**
 * Retrieve all inferred data from an annnoted element
 * 
 * @author Emmanuel Bernard
 * @author Paolo Perrotta
 */
public class PropertyInferredData implements PropertyData {
	private final String defaultAccess;

	private final XProperty property;

	/**
	 * Take the annoted element for lazy process
	 */
	public PropertyInferredData( XProperty property, String propertyAccessor ) {
		this.property = property;
		this.defaultAccess = propertyAccessor;
	}

	public String getDefaultAccess() throws MappingException {
		// if(skip())
		// return defaultAccess;
		AccessType access = property.getAnnotation( AccessType.class );
		return access != null ? access.value() : defaultAccess;
	}

	public String getPropertyName() throws MappingException {
		return property.getName();
	}

	public XClass getPropertyClass() throws MappingException {
		return property.getType();
	}

	public XClass getClassOrElement() throws MappingException {
		return property.getClassOrElementClass();
	}

	public String getClassOrElementName() throws MappingException {
		return property.getClassOrElementClass().getName();
	}

	public String getTypeName() throws MappingException {
		return property.getType().getName();
	}

	public XProperty getProperty() {
		return property;
	}
}
