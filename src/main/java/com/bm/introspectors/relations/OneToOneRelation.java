package com.bm.introspectors.relations;

import java.util.Set;


import com.bm.introspectors.Property;
import javax.persistence.OneToOne;

/**
 * Represents a ManyToOne relation.
 * 
 * @author Daniel Wiese
 * 
 */
public class OneToOneRelation extends AbstractRelation implements
		EntityReleationInfo {

	Set<Property> targetKeyProperty;

	/**
	 * Default constructor.
	 * 
	 * @param sourceClass -
	 *            the type of the source entity bean
	 * @param targetClass -
	 *            the type of the target entity bean
	 * @param sourceProperty -
	 *            the property of the source entity bean
	 * @param targetProperty -
	 *            the property of the target entity bean
	 * 
	 * 
	 * @param annotation -
	 *            the annotation (with values)
	 */
	public OneToOneRelation(Class sourceClass, Class targetClass,
			Property sourceProperty, Property targetProperty,
			OneToOne annotation) {
		super(sourceClass, targetClass, sourceProperty, targetProperty,
				annotation.fetch(), annotation.cascade());
	}

	/**
	 * Returns the type of the relation.
	 * @return the type of the relation.
	 * @see com.bm.introspectors.relations.EntityReleationInfo#getReleationType()
	 */
	public RelationType getReleationType() {
		return RelationType.OneToOne;
	}

	/**
	 * Returns the primary key properties of the target class
	 * @return the primary key properties of the target class
	 */
	public Set<Property> getTargetKeyProperty() {
		return targetKeyProperty;
	}

	/**
	 * Sets the primary key properties of the target class
	 * @param targetKeyProp
	 */
	public void setTargetKeyProperty(Set<Property> targetKeyProp) {
		this.targetKeyProperty = targetKeyProp;
	}
}
