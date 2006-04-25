package com.bm.datagen.random;

import com.bm.datagen.Generator;
import com.bm.datagen.annotations.FieldType;
import com.bm.datagen.annotations.ForProperty;
import com.bm.datagen.annotations.GeneratorType;
import com.bm.datagen.annotations.UsedIntrospector;
import com.bm.datagen.utils.BaseRandomDataGenerator;
import com.bm.datagen.utils.BaseUniqueValueGenerator;
import com.bm.introspectors.Introspector;
import com.bm.introspectors.Property;

/**
 * Generates random Long values - distinguish between PK and non PK fields.
 * @author Daniel Wiese
 *
 */
@GeneratorType(className = Long.class, fieldType = FieldType.ALL_TYPES)
public class RandomLongGenerator extends BaseUniqueValueGenerator<Long>
		implements Generator<Long> {
	
	@ForProperty private Property forProperty;
	@UsedIntrospector private Introspector<Object> introspector;

	/**
	 * Returns the next Long.
	 * @return - the generated value
	 * @see com.bm.datagen.Generator#getValue()
	 */
	public Long getValue() {
		return this.getUniqueValueForEachPkField(forProperty, introspector);
	}

	@Override
	protected Long generateCadidate() {
		return BaseRandomDataGenerator.getValueLong();
	}
	
	

}
