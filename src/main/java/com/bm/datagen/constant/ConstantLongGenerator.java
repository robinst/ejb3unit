package com.bm.datagen.constant;

import com.bm.datagen.Generator;


/**
 * Generatos constant Integer values.
 * @author Daniel Wiese
 *
 */
public class ConstantLongGenerator implements Generator<Long> {

	private final long constant;

	/**
	 * Constructor.
	 * 
	 * @param constant -
	 *            the constant value
	 */
	public ConstantLongGenerator(long constant) {
		this.constant = constant;
	}

	/**
	 * The value.
	 * @return the vaue
	 * 
	 * @see com.bm.datagen.Generator#getValue()
	 */
	public Long getValue() {
		return this.constant;
	}
	
}	
