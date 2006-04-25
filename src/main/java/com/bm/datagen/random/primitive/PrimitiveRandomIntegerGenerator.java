package com.bm.datagen.random.primitive;

import com.bm.datagen.Generator;
import com.bm.datagen.annotations.FieldType;
import com.bm.datagen.annotations.GeneratorType;
import com.bm.datagen.utils.BaseRandomDataGenerator;

/**
 * Generates random Integer values - distinguish between PK and non PK fields.
 * 
 * @author Daniel Wiese
 * 
 */
@GeneratorType(className = Integer.class, fieldType = FieldType.NON_PK_FIELDS)
public class PrimitiveRandomIntegerGenerator 
		implements Generator<Integer> {
	
	

	/**
	 * Returns the next Integer.
	 * @return - the generated value
	 * @see com.bm.datagen.Generator#getValue()
	 */
	public Integer getValue() {
		return BaseRandomDataGenerator.getValueInt();
	}

}
