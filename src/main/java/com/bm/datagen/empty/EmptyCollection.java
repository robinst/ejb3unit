package com.bm.datagen.empty;

import java.util.ArrayList;
import java.util.Collection;

import com.bm.datagen.Generator;
import com.bm.datagen.annotations.FieldType;
import com.bm.datagen.annotations.GeneratorType;

/**
 * Generates boolean values (only for non PK fields).
 * 
 * @author Daniel Wiese
 * 
 */
@GeneratorType(className = Collection.class, fieldType = FieldType.ALL_TYPES)
public class EmptyCollection implements Generator<Collection> {

	/**
	 * Returns the next boolean value.
	 * 
	 * @return - the generated value
	 * @see com.bm.datagen.Generator#getValue()
	 */
	public Collection getValue() {
		return new ArrayList();
	}

}
