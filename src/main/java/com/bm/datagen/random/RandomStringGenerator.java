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
 * Generates random string values - distinguish between PK and non PK fields.
 * 
 * @author Daniel Wiese
 * 
 */
@GeneratorType(className = String.class, fieldType = FieldType.ALL_TYPES)
public class RandomStringGenerator extends BaseUniqueValueGenerator<String>
		implements Generator<String> {

	private int lastLength = 255;

	@ForProperty
	private Property forProperty;

	@UsedIntrospector
	private Introspector<Object> introspector;

	/**
	 * Returns the next String.
	 * 
	 * @return - the generated value
	 * @see com.bm.datagen.Generator#getValue()
	 */
	public String getValue() {
		lastLength = introspector.getPresistentFieldInfo(forProperty)
				.getLength();

		if (introspector.getPkFields().contains(forProperty)) {
			// generate non simple strings for non pk fields
			return this.getUniqueValueForEachPkField(forProperty, introspector);
		} else {
			return BaseRandomDataGenerator.getValueString(lastLength, false);
		}

	}

	@Override
	protected String generateCadidate() {
		return BaseRandomDataGenerator.getValueString(lastLength, true);
	}

}
