//$Id: PropertyBinder.java,v 1.1 2006/04/17 12:11:11 daniel_wiese Exp $
package org.hibernate.cfg.annotations;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.cfg.Ejb3Column;
import org.hibernate.cfg.Mappings;
import org.hibernate.cfg.PropertyHolder;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.SimpleValue;
import org.hibernate.mapping.Value;
import org.hibernate.reflection.XClass;
import org.hibernate.reflection.XProperty;

/**
 * @author Emmanuel Bernard
 */
public class PropertyBinder {
	private static Log log = LogFactory.getLog( PropertyBinder.class );
	private String name;
	private String returnedClassName;
	private boolean lazy;
	private String propertyAccessorName;
	private Ejb3Column[] columns;
	private PropertyHolder holder;
	private Mappings mappings;
	private Value value;
	private boolean insertable = true;
	private boolean updatable = true;
	private String cascade;
	private XProperty property;
	private XClass returnedClass;

	public void setInsertable(boolean insertable) {
		this.insertable = insertable;
	}

	public void setUpdatable(boolean updatable) {
		this.updatable = updatable;
	}


	public void setName(String name) {
		this.name = name;
	}

	public void setReturnedClassName(String returnedClassName) {
		this.returnedClassName = returnedClassName;
	}

	public void setLazy(boolean lazy) {
		this.lazy = lazy;
	}

	public void setPropertyAccessorName(String propertyAccessorName) {
		this.propertyAccessorName = propertyAccessorName;
	}

	public void setColumns(Ejb3Column[] columns) {
		insertable = columns[0].isInsertable();
		updatable = columns[0].isUpdatable();
		//concsistency is checked later when we know the proeprty name
		this.columns = columns;
	}

	public void setHolder(PropertyHolder holder) {
		this.holder = holder;
	}

	public void setValue(Value value) {
		this.value = value;
	}

	public void setCascade(String cascadeStrategy) {
		this.cascade = cascadeStrategy;
	}

	public void setMappings(Mappings mappings) {
		this.mappings = mappings;
	}

	private void validateBind() {
		//TODO check necessary params for a bind		
	}

	private void validateMake() {
		//TODO check necessary params for a make
	}

	public Property bind() {
		validateBind();
		if ( log.isDebugEnabled() ) {
			log.debug( "binding property " + name + " with lazy=" + lazy );
		}
		String containerClassName = holder == null ? null : holder.getClassName();
		SimpleValueBinder value = new SimpleValueBinder();
		value.setPropertyName( name );
		value.setReturnedClassName( returnedClassName );
		value.setColumns( columns );
		value.setPersistentClassName( containerClassName );
		value.setType( property, returnedClass );
		value.setMappings( mappings );
		SimpleValue propertyValue = value.make();
		setValue( propertyValue );
		Property prop = make();
		holder.addProperty(prop, columns);
		return prop;
	}

	public Property make() {
		validateMake();
		log.debug( "Building property " + name );
		Property prop = new Property();
		prop.setName( name );
		prop.setValue( value );
		prop.setInsertable( insertable );
		prop.setUpdateable( updatable );
		prop.setLazy( lazy );
		prop.setCascade( cascade );
		prop.setPropertyAccessorName( propertyAccessorName );
		log.debug( "Cascading " + name + " with " + cascade );
		return prop;
	}

	public void setProperty(XProperty property) {
		this.property = property;
	}

	public void setReturnedClass(XClass returnedClass) {
		this.returnedClass = returnedClass;
	}
}
