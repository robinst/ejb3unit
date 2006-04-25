//$Id: PropertyPreloadedData.java,v 1.1 2006/04/17 12:11:10 daniel_wiese Exp $
package org.hibernate.cfg;

import org.hibernate.MappingException;
import org.hibernate.reflection.XClass;
import org.hibernate.reflection.XProperty;

public class PropertyPreloadedData implements PropertyData {
	private final String defaultAccess;

	private final String propertyName;

	private final XClass returnedClass;

	public PropertyPreloadedData( String defaultAccess, String propertyName, XClass returnedClass ) {
		this.defaultAccess = defaultAccess;
		this.propertyName = propertyName;
		this.returnedClass = returnedClass;
	}

	public String getDefaultAccess() throws MappingException {
		return defaultAccess;
	}

	public String getPropertyName() throws MappingException {
		return propertyName;
	}

	public XClass getClassOrElement() throws MappingException {
		return getPropertyClass();
	}

	public XClass getPropertyClass() throws MappingException {
		return returnedClass;
	}

	public String getClassOrElementName() throws MappingException {
		return getTypeName();
	}

	public String getTypeName() throws MappingException {
		return returnedClass == null ? null : returnedClass.getName();
	}

	public XProperty getProperty() {
		return null; //instead of UnsupportedOperationException
	}
}
