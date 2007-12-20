package com.bm.introspectors.relations;

import java.util.Set;

import com.bm.introspectors.Property;

/**
 * EntityReleationInfo.
 * 
 * @author Daniel Wiese
 * 
 */
public interface EntityReleationInfo {

	/**
	 * Returns the type of the relation.
	 * 
	 * @return the type of the relation
	 */
	RelationType getReleationType();
    
    /**
     * If the property is unidirectional.
     * @return the isUnidirectional
     */
    boolean isUnidirectional();
    
    /**
     * True when the delete operation is cascading.
     * @return when the delete operation is cascading
     */
    public boolean isCascadeOnDelete();

    /**
     * Returns the primary key property (or properties, in case of a composite key) for the 
     * class that is target of the relation
     * @return	primary key property / properties, or null.
     */
	public Set<Property> getTargetKeyProperty();

}
