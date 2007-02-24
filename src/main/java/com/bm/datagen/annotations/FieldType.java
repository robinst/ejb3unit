package com.bm.datagen.annotations;

/**
 * This enum specify to which field types a data generator shuld by applied.
 * 
 * @author Daniel Wiese
 */
public enum FieldType {

	/** all field types. * */
	ALL_TYPES,

	/** only pk fields. * */
	PK_FIELDS,

	/** all non pk field types. * */
	NON_PK_FIELDS

}
