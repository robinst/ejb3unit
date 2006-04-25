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
 * Generates random Integer values - distinguish between PK and non PK fields.
 * 
 * @author Daniel Wiese
 * 
 */
@GeneratorType(className = Integer.class, fieldType = FieldType.ALL_TYPES)
public class RandomIntegerGenerator extends BaseUniqueValueGenerator<Integer>
		implements Generator<Integer> {
	
	@ForProperty private Property forProperty;
	@UsedIntrospector private Introspector<Object> introspector;

	/**
	 * Returns the next Integer.
	 * @return - the generated value
	 * @see com.bm.datagen.Generator#getValue()
	 */
	public Integer getValue() {
		return this.getUniqueValueForEachPkField(forProperty, introspector);
	}

	@Override
	protected Integer generateCadidate() {
		return BaseRandomDataGenerator.getValueInt();
	}

}
