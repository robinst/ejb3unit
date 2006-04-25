package com.bm.datagen.random.primitive;

import com.bm.datagen.Generator;
import com.bm.datagen.annotations.FieldType;
import com.bm.datagen.annotations.GeneratorType;
import com.bm.datagen.utils.BaseRandomDataGenerator;

/**
 * Generates random string values - distinguish between PK and non PK fields.
 * @author Daniel Wiese
 *
 */
@GeneratorType(className = String.class, fieldType = FieldType.NON_PK_FIELDS)
public class PrimitiveRandomStringGenerator
		implements Generator<String> {

	/**
	 * Returns the next String.
	 * @return - the generated value
	 * @see com.bm.datagen.Generator#getValue()
	 */
	public String getValue() {
		return BaseRandomDataGenerator.getValueString(255, false);

	}

}
