package com.bm;

import java.util.Collection;

import com.bm.datagen.Generator;
import com.bm.datagen.annotations.GeneratorType;
import com.bm.datagen.relation.BeanCollectionGenerator;
import com.bm.ejb3data.bo.LineItem;
import com.bm.ejb3data.bo.Order;
import com.bm.testsuite.BaseEntityFixture;

/**
 * This test use getter/setter (properties) configuration for entity beans.
 * 
 * @author Daniel Wiese
 * 
 */
public class OrderTest extends BaseEntityFixture<Order> {

	private static final Generator<?>[] SPECIAL_GENERATORS = { new MyLineItemCreator() };

	/**
	 * Default constructor.
	 */
	public OrderTest() {
		super(Order.class, SPECIAL_GENERATORS);
	}

	/**
	 * Generator for releated collection
	 * 
	 * @author Daniel Wiese
	 */
	@GeneratorType(className = Collection.class, field = "lineItems")
	private static final class MyLineItemCreator extends
			BeanCollectionGenerator<LineItem> {
		private MyLineItemCreator() {
			super(LineItem.class, 10);
		}
	}

}
