package com.bm.datagen.random.primitive;

import com.bm.datagen.Generator;
import com.bm.datagen.annotations.FieldType;
import com.bm.datagen.annotations.GeneratorType;
import com.bm.datagen.utils.BaseRandomDataGenerator;

/**
 * Generates random Short values - distinguish between PK and non PK fields.
 * 
 * @author Daniel Wiese
 * 
 */
@GeneratorType(className = Short.class, fieldType = FieldType.NON_PK_FIELDS)
public class PrimitiveRandomShortGenerator implements Generator<Short> {

	/**
	 * Returns the next Short.
	 * @return - the generated value
	 * @see com.bm.datagen.Generator#getValue()
	 */
	public Short getValue() {
		return BaseRandomDataGenerator.getValueShort();
	}

}
