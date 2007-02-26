package com.bm.datagen.constant;

import com.bm.datagen.Generator;

/**
 * Generatos constant Integer values.
 * @author Daniel Wiese
 *
 */
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
	 * The value.
	 * @return the vaue
	 * @see com.bm.datagen.Generator#getValue()
	 */
	public Integer getValue() {
		return this.constant;
	}
	
}	
