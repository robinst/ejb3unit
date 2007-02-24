package com.bm;

import com.bm.data.bo.LineItem;
import com.bm.data.bo.Order;
import com.bm.datagen.Generator;
import com.bm.datagen.annotations.GeneratorType;
import com.bm.datagen.relation.SingleBeanGenerator;
import com.bm.testsuite.BaseEntityTest;

/**
 * This test test a entity bean with property access and a N:1 releation.
 * 
 * @author Daniel Wiese
 * 
 */
public class LineItemTest extends BaseEntityTest<LineItem> {

	private static final Generator[] SPECIAL_GENERATORS = { new MyOrderCreator() };

	/**
	 * Default constructor.
	 */
	public LineItemTest() {
		super(LineItem.class, SPECIAL_GENERATORS);
	}

	/**
	 * Innec Test class.
	 * 
	 * @author Daniel Wiese
	 * @since 21.11.2005
	 */
	@GeneratorType(className = Order.class)
	private static final class MyOrderCreator extends
			SingleBeanGenerator<Order> {
		private MyOrderCreator() {
			super(Order.class);
		}
	}
}
