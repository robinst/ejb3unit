package com.bm;

import java.util.Set;

import com.bm.datagen.Generator;
import com.bm.datagen.annotations.GeneratorType;
import com.bm.datagen.relation.BeanCollectionGenerator;
import com.bm.ejb3data.bo.LineItemForSetOrder;
import com.bm.ejb3data.bo.OrderWithSetFields;
import com.bm.testsuite.BaseEntityFixture;

/**
 * This test use getter/setter (properties) configuration for entity beans.
 * 
 * @author Daniel Wiese
 * 
 */
public class OrderTestWithSetCollections extends BaseEntityFixture<OrderWithSetFields> {

	private static final Generator<?>[] SPECIAL_GENERATORS = { new MyLineItemCreator() };

	/**
	 * Default constructor.
	 */
	public OrderTestWithSetCollections() {
		super(OrderWithSetFields.class, SPECIAL_GENERATORS);
	}

	/**
	 * Generator for releated collection
	 * 
	 * @author Daniel Wiese
	 */
	@GeneratorType(className = Set.class, field = "lineItems")
	private static final class MyLineItemCreator extends
			BeanCollectionGenerator<LineItemForSetOrder> {
		private MyLineItemCreator() {
			super(LineItemForSetOrder.class, 10);
		}
	}

}
