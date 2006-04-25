package com.bm.introspectors.releations;

/**
 * Represents the type of a entity releation.
 * @author Daniel Wiese
 */
public enum RelationType {
	
	/** all field types. **/
	OneToMany,
	
	/** only pk fields. **/
	ManyToOne,
	
	/** all non pk field types. **/
	OneToOne

}
