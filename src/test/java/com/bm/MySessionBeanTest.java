package com.bm;

import java.util.List;

import com.bm.data.bo.MySessionBean;
import com.bm.data.bo.StockWKNBo;
import com.bm.testsuite.BaseSessionBeanFixture;
import com.bm.testsuite.dataloader.CSVInitialDataSet;
import com.bm.testsuite.dataloader.EntityInitialDataSet;

/**
 * The Session bean.
 * 
 * @author Daniel Wiese
 * @since 08.11.2005
 */
public class MySessionBeanTest extends BaseSessionBeanFixture<MySessionBean> {

	private static final Class[] usedBeans = { StockWKNBo.class };

	private static final CSVInitialDataSet CSV_SET = new CSVInitialDataSet<StockWKNBo>(
			StockWKNBo.class, "allstatData.csv", "wkn", "aktienName", "isin",
			"symbol", "kaufModus", "branchenCode", "branche",
			"transaktionenProTag", "zumHandelZugelassen", "volatilitaet",
			"durchschnittskaufkurs");

	/**
	 * Constructor.
	 */
	public MySessionBeanTest() {
		super(MySessionBean.class, usedBeans,
				new StockWKNEntityInitialDataSet(), CSV_SET);
	}

	/**
	 * Test the dpendency injection.
	 * 
	 * @author Daniel Wiese
	 * @since 08.11.2005
	 */
	public void testDependencyInjection() {
		final MySessionBean toTest = this.getBeanToTest();
		assertNotNull(toTest);
		assertNotNull(toTest.getDs());
		assertNotNull(toTest.getEm());
	}

	/**
	 * Test the dpendency injection.
	 * 
	 * @author Daniel Wiese
	 * @since 08.11.2005
	 */
	public void testLoadData() {
		final MySessionBean toTest = this.getBeanToTest();
		assertNotNull(toTest);
		List<StockWKNBo> back = toTest.getAllStocks();
		assertNotNull(back);
		assertEquals(188, back.size());
		assertEquals(back.get(0), new StockWKNBo(1, "Das ist ein Name"));
		assertEquals(back.get(1), new StockWKNBo(2, "Das ist ein andere Name"));
	}

	/**
	 * Test the dpendency injection.
	 * 
	 * @author Daniel Wiese
	 * @since 08.11.2005
	 */
	public void testJndiLookup() {
		final MySessionBean toTest = this.getBeanToTest();
		MySessionBean lookedUp = (MySessionBean) toTest.getCtx().lookup(
				"ejb/MySessionBean");
		assertNotNull(lookedUp);
	}

	/**
	 * Creates a initial data set.
	 * 
	 * @author Daniel Wiese
	 * @since 17.04.2006
	 */
	public static class StockWKNEntityInitialDataSet extends
			EntityInitialDataSet<StockWKNBo> {

		/**
		 * Constructor.
		 * 
		 * @param entityType
		 */
		public StockWKNEntityInitialDataSet() {
			super(StockWKNBo.class);
		}

		/**
		 * Creates the data.
		 * 
		 * @author Daniel Wiese
		 * @since 17.04.2006
		 * @see com.bm.testsuite.dataloader.InitialDataSet#create()
		 */
		public void create() {
			this.add(new StockWKNBo(1, "Das ist ein Name"));
			this.add(new StockWKNBo(2, "Das ist ein andere Name"));

		}

	}

}
