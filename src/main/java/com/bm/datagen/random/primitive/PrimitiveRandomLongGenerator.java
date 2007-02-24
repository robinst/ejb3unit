package com.bm.datagen.random.primitive;

import com.bm.datagen.Generator;
import com.bm.datagen.annotations.FieldType;
import com.bm.datagen.annotations.GeneratorType;
import com.bm.datagen.utils.BaseRandomDataGenerator;

/**
 * Generates random Long values - distinguish between PK and non PK fields.
 * 
 * @author Daniel Wiese
 * 
 */
@GeneratorType(className = Long.class, fieldType = FieldType.ALL_TYPES)
public class PrimitiveRandomLongGenerator implements Generator<Long> {

	/**
	 * Returns the next Long.
	 * 
	 * @return - the generated value
	 * @see com.bm.datagen.Generator#getValue()
	 */
	public Long getValue() {
		return BaseRandomDataGenerator.getValueLong();
	}

}
