package com.bm.datagen.random.primitive;

import com.bm.datagen.Generator;
import com.bm.datagen.annotations.FieldType;
import com.bm.datagen.annotations.GeneratorType;
import com.bm.datagen.utils.BaseRandomDataGenerator;

/**
 * Generates Byte values (only for non PK fields).
 * 
 * @author Daniel Wiese
 * 
 */
@GeneratorType(className = Byte.class, fieldType = FieldType.NON_PK_FIELDS)
public class PrimitiveRandomByteGenerator implements Generator<Byte> {

	/**
	 * Returns the next Byte.
	 * 
	 * @return - the generated value
	 * @see com.bm.datagen.Generator#getValue()
	 */
	public Byte getValue() {
		return BaseRandomDataGenerator.getValueByte();
	}

}
