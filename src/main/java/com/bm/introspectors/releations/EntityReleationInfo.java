package com.bm.introspectors.releations;

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
     * If the preperty is unidirectional.
     * @return the isUnidirectional
     */
    boolean isUnidirectional();
    
    /**
     * True wenn the delete operatio is cascading.
     * @return when the delete operation is cascading
     */
    public boolean isCascadeOnDelete();

}
