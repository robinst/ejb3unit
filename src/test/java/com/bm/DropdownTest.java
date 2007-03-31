package com.bm;

import java.util.Collection;

import com.bm.data.bo.Choice;
import com.bm.data.bo.Dropdown;
import com.bm.datagen.Generator;
import com.bm.datagen.annotations.GeneratorType;
import com.bm.datagen.relation.BeanCollectionGenerator;
import com.bm.testsuite.BaseEntityFixture;

/**
 * Test classes with unidirectional relationship.
 */
public class DropdownTest extends BaseEntityFixture<Dropdown> {

    /**
     * Generators.
     */
    private static final Generator[] GENERATORS = {new ChoiceCreator() };

    /**
     * Constructor.
     */
    public DropdownTest() {
        super(Dropdown.class, GENERATORS);
    }

    /**
     * Generator for releated collection
     * 
     * @author Daniel Wiese
     */
    @GeneratorType(className = Collection.class, field = "choices")
    private static final class ChoiceCreator extends BeanCollectionGenerator<Choice> {
        private ChoiceCreator() {
            super(Choice.class, 10);
        }
    }
}
