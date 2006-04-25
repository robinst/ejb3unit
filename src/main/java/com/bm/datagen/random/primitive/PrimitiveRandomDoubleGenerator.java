package com.bm.datagen.random.primitive;

import com.bm.datagen.Generator;
import com.bm.datagen.annotations.FieldType;
import com.bm.datagen.annotations.GeneratorType;
import com.bm.datagen.utils.BaseRandomDataGenerator;

/**
 * Generates Double values (only for non PK fields).
 * 
 * @author Daniel Wiese
 * 
 */
@GeneratorType(className = Double.class, fieldType = FieldType.NON_PK_FIELDS)
public class PrimitiveRandomDoubleGenerator implements Generator<Double> {

	/**
	 * Returns the next Double.
	 * @return - the generated value
	 * @see com.bm.datagen.Generator#getValue()
	 */
	public Double getValue() {
		return BaseRandomDataGenerator.getValueDouble();
	}

}
