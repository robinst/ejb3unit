package com.bm.junit4;

import java.util.Collection;

import com.bm.datagen.Generator;
import com.bm.datagen.annotations.GeneratorType;
import com.bm.datagen.relation.BeanCollectionGenerator;
import com.bm.ejb3data.bo.Choice;
import com.bm.ejb3data.bo.Dropdown;
import com.bm.testsuite.junit4.BaseEntityJunit4Fixture;

public class DropdownJUnit4Test extends BaseEntityJunit4Fixture<Dropdown> {

	/**
	 * Generators.
	 */
	private static final Generator<?>[] GENERATORS = { new ChoiceCreator() };

	/**
	 * Constructor.
	 */
	public DropdownJUnit4Test() {
		super(Dropdown.class, GENERATORS);
	}

	/**
	 * Generator for releated collection
	 * 
	 * @author Daniel Wiese
	 */
	@GeneratorType(className = Collection.class, field = "choices")
	private static final class ChoiceCreator extends
			BeanCollectionGenerator<Choice> {
		private ChoiceCreator() {
			super(Choice.class, 10);
		}
	}
}
