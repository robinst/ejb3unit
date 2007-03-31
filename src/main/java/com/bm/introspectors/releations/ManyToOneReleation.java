package com.bm.introspectors.releations;

import javax.persistence.ManyToOne;

import com.bm.introspectors.Property;

/**
 * Represents a ManyToOne relelation.
 * 
 * @author Daniel Wiese
 * 
 */
public class ManyToOneReleation extends AbstractRelation implements
		EntityReleationInfo {

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
	public ManyToOneReleation(Class sourceClass, Class targetClass,
			Property sourceProperty, Property targetProperty,
			ManyToOne annotation) {
		super(sourceClass, targetClass, sourceProperty, targetProperty,
				annotation.fetch(), annotation.cascade());
	}

	/**
	 * Returns the type of the relation.
	 * @return the type of the relation.
	 * @see com.bm.introspectors.releations.EntityReleationInfo#getReleationType()
	 */
	public RelationType getReleationType() {
		return RelationType.ManyToOne;
	}

}
