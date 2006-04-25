package com.bm.datagen.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation describes the metadata of an generator.
 * 
 * @author Daniel Wiese
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface GeneratorType {

	/**
	 * The type of the generator. E.g. java.util.Date-> will generate Date
	 * instances.
	 */
	Class className();

	/**
	 * The name of the fields (optional).
	 * 
	 */
	String[] field() default { "$all" };

	/**
	 * The field type to which the generator should be applied.
	 * 
	 */
	FieldType fieldType() default FieldType.ALL_TYPES;

}
