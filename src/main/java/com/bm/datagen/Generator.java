package com.bm.datagen;

/**
 * This intrface represents a base generator contract.
 * 
 * @author Daniel Wiese
 * @param <T> - the type of the generator (e.g. java.util.Date)
 * @since 07.10.2005
 */
public interface Generator<T extends Object> {

	/**
	 * Generates a value for a distinct type T for a specified field
	 * - the field is used as an additional information an can be the same
	 * for all calls.
	 * @return - the generated value
	 */
	T getValue();
}
