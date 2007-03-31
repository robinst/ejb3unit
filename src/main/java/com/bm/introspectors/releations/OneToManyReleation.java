package com.bm.introspectors.releations;

import javax.persistence.OneToMany;

import com.bm.introspectors.Property;

/**
 * Represents a OneToMany relelation.
 * @author Daniel Wiese
 *
 */
public class OneToManyReleation extends AbstractRelation implements EntityReleationInfo {

	

	/**
	 * Default constructor.
	 * @param sourceClass - the type of the source entity bean
	 * @param targetClass - the type of the target entity bean
	 * @param sourceProperty - the property of the source entity bean
	 * @param targetProperty - the property of the target entity bean
	 * 
	 * 
	 * @param annotation -
	 *            the annotation (with values)
	 */
	public OneToManyReleation(Class sourceClass, Class targetClass, Property sourceProperty, Property targetProperty, OneToMany annotation) {
		
		super(sourceClass, targetClass, sourceProperty, targetProperty,
				annotation.fetch(), annotation.cascade());
	}

	/**
	 * Returns the type of the relation.
	 * @return the relation type
	 * @see com.bm.introspectors.releations.EntityReleationInfo#getReleationType()
	 */
	public RelationType getReleationType() {
		return RelationType.OneToMany;
	}
}
