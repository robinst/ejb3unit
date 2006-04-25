package com.bm.datagen.random;

import java.util.Date;

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
 * Generates random Date values - distinguish between PK and non PK fields.
 * 
 * @author Daniel Wiese
 * 
 */
@GeneratorType(className = Date.class, fieldType = FieldType.ALL_TYPES)
public class RandomDateGenerator extends BaseUniqueValueGenerator<Date>
		implements Generator<Date> {
	
	@ForProperty private Property forProperty;
	@UsedIntrospector private Introspector<Object> introspector;

	/**
	 * Returns the date.
	 * @return - the generated value
	 * @see com.bm.datagen.Generator#getValue()
	 */
	public Date getValue() {
		return this.getUniqueValueForEachPkField(forProperty, introspector);
	}

	@Override
	protected Date generateCadidate() {
		return BaseRandomDataGenerator.getValueDate();
	}

}
