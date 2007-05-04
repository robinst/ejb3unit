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
@GeneratorType(className = Float.class, fieldType = FieldType.NON_PK_FIELDS)
public class PrimitiveRandomFloatGenerator implements Generator<Float> {

	/**
	 * Returns the next Double.
	 * 
	 * @return - the generated value
	 * @see com.bm.datagen.Generator#getValue()
	 */
	public Float getValue() {
		return BaseRandomDataGenerator.getValueFloat();
	}

}
