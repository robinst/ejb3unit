package com.bm.datagen.random.primitive;

import java.util.Date;

import com.bm.datagen.Generator;
import com.bm.datagen.annotations.FieldType;
import com.bm.datagen.annotations.GeneratorType;
import com.bm.datagen.utils.BaseRandomDataGenerator;

/**
 * Generates random Date values - distinguish between PK and non PK fields.
 * 
 * @author Daniel Wiese
 * 
 */
@GeneratorType(className = Date.class, fieldType = FieldType.NON_PK_FIELDS)
public class PrimitiveRandomDateGenerator
		implements Generator<Date> {
	

	/**
	 * Returns the date.
	 * @return - the generated value
	 * @see com.bm.datagen.Generator#getValue()
	 */
	public Date getValue() {
		return BaseRandomDataGenerator.getValueDate();
	}

	

}
