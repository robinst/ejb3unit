package com.bm.junit4;

import static com.bm.testsuite.BaseTest.assertCollectionsEqual;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.bm.ejb3data.bo.LineItem;
import com.bm.ejb3data.bo.Order;
import com.bm.testsuite.junit4.PoJoJUnit4Fixture;

public class PojoFixtureUsageJUnit4ExampleTest extends PoJoJUnit4Fixture {
	private static final Class<?>[] USED_ENTITIES = { Order.class,
			LineItem.class };

	public PojoFixtureUsageJUnit4ExampleTest() {
		super(USED_ENTITIES);
	}
	
	
	/**
	 * Delets all data. {@inheritDoc}
	 */
	@Before
	public void setUpAdditional() throws Exception {
		deleteAll(LineItem.class);
		deleteAll(Order.class);
	}

	/**
	 * Delets all data. {@inheritDoc}
	 */
	@After
	public void tearDownAdditional() throws Exception {
		deleteAll(LineItem.class);
		deleteAll(Order.class);
	}
	
	@Test
	public void toWriteComplexObjectGraph() {
		List<Order> complexObjectGraph = generateTestOrders();

		// persist the graph and load it again
		List<Order> persisted = persist(complexObjectGraph);
		List<Order> allFromDB = findAll(Order.class);

		// assert the persisted graph and the loaded are equal
		assertCollectionsEqual(persisted, allFromDB);
	}
	
	@Test
	public void checkGetEntityManager() {
		assertNotNull(this.getEntityManager());
	}

	private List<Order> generateTestOrders() {
		final List<Order> orders = new ArrayList<Order>();
		Order order = new Order();
		order.setExpiration(new Date());
		order.addPurchase("Testprod1", 30, 30.34);
		order.addPurchase("Testprod2", 31, 31.34);
		order.addPurchase("Testprod3", 32, 32.34);
		order.addPurchase("Testprod4", 33, 33.34);
		orders.add(order);
		return orders;
	}
}
