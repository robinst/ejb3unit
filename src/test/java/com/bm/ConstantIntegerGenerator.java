package com.bm;

import com.bm.datagen.Generator;
import com.bm.datagen.annotations.FieldType;
import com.bm.datagen.annotations.GeneratorType;

/**
 * Generates static Integer values.
 * 
 * @author Daniel Wiese
 * @since 16.10.2005
 * 
 */
@GeneratorType(className = Integer.class, field = "wkn", fieldType = FieldType.ALL_TYPES)
public class ConstantIntegerGenerator implements Generator<Integer> {

	private final int constant;

	/**
	 * Constructor.
	 * 
	 * @param constant -
	 *            the constant value
	 */
	public ConstantIntegerGenerator(int constant) {
		this.constant = constant;
	}

	/**
	 * Generates a value for a distinct type T for a specified field - the field
	 * is used as an additional information an can be the same for all calls.
	 * @return the value generated
	 * @see com.bm.datagen.Generator#getValue()
	 */
	public Integer getValue() {
		return this.constant;
	}

}
