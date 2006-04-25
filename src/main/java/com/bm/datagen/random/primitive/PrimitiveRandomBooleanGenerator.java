package com.bm.datagen.random.primitive;

import com.bm.datagen.Generator;
import com.bm.datagen.annotations.FieldType;
import com.bm.datagen.annotations.GeneratorType;
import com.bm.datagen.utils.BaseRandomDataGenerator;

/**
 * Generates boolean values (only for non PK fields).
 * 
 * @author Daniel Wiese
 * 
 */
@GeneratorType(className = Boolean.class, fieldType = FieldType.NON_PK_FIELDS)
public class PrimitiveRandomBooleanGenerator implements Generator<Boolean> {

	/**
	 * Returns the next boolean value.
	 * @return - the generated value
	 * @see com.bm.datagen.Generator#getValue()
	 */
	public Boolean getValue() {
		return BaseRandomDataGenerator.getValueBoolean();
	}

}
