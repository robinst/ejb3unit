package com.bm.datagen.constant;

import com.bm.datagen.Generator;

/**
 * ConstantStringGenerator.
 * @author Daniel Wiese
 *
 */
public class ConstantStringGenerator implements Generator<String> {

	private final String constant;

	/**
	 * Constructor.
	 * 
	 * @param constant -
	 *            the constant value
	 */
	public ConstantStringGenerator(String constant) {
		this.constant = constant;
	}

	/**
	 * Returns the value.
	 * @see com.bm.datagen.Generator#getValue()
	 */
	public String getValue() {
		return this.constant;
	}
	
}	
