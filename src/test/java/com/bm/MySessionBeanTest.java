package com.bm;

import java.util.List;

import javax.persistence.EntityTransaction;

import com.bm.creators.EntityBeanCreator;
import com.bm.ejb3data.bo.ExpertiseAreas;
import com.bm.ejb3data.bo.MySessionBean;
import com.bm.ejb3data.bo.StockWKNBo;
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

	private static final Class<?>[] usedBeans = { StockWKNBo.class, ExpertiseAreas.class };

	private static final CSVInitialDataSet<StockWKNBo> CSV_SET = new CSVInitialDataSet<StockWKNBo>(
			StockWKNBo.class, "allstatData.csv", "wkn", "aktienName", "isin", "symbol",
			"kaufModus", "branchenCode", "branche", "transaktionenProTag",
			"zumHandelZugelassen", "volatilitaet", "durchschnittskaufkurs");

	/**
	 * Constructor.
	 */
	public MySessionBeanTest() {
		super(MySessionBean.class, usedBeans, new StockWKNEntityInitialDataSet(), CSV_SET);
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
	 * Test persistence.
	 */
	public void testPersistABean() throws Exception {
		EntityTransaction tx = this.getEntityManager().getTransaction();
		final MySessionBean toTest = this.getBeanToTest();
		try {
			tx.begin();
			final EntityBeanCreator<ExpertiseAreas> expAreasCreator = new EntityBeanCreator<ExpertiseAreas>(
					this.getEntityManager(), ExpertiseAreas.class);
			toTest.saveEntityBean(expAreasCreator.createBeanInstance());
			tx.commit();
		} catch (Exception e) {
			tx.rollback();
			throw e;
		}
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
		assertEquals(new StockWKNBo(1, "Das ist ein Name"), back.get(0));
		assertEquals(new StockWKNBo(2, "Das ist ein andere Name"), back.get(1));
	}
	
	/**
	 * Test a named query (typed).
	 */
	public void testTypedQuery() {
		final MySessionBean toTest = this.getBeanToTest();
		List<StockWKNBo> stocks = toTest.getAllStocksWithTypedQuery();
		assertNotNull(stocks);
		assertEquals(188, stocks.size());
		assertEquals(new StockWKNBo(1, "Das ist ein Name"), stocks.get(0));
		assertEquals(new StockWKNBo(2, "Das ist ein andere Name"), stocks.get(1));
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
	 * Test the dpendency injection.
	 * 
	 * @author Daniel Wiese
	 * @since 08.11.2005
	 */
	public void testJavaCompJndiLookup() {
		final MySessionBean toTest = this.getBeanToTest();
		MySessionBean lookedUp = (MySessionBean) toTest.getCtx().lookup(
				"java:/ejb/MySessionBean");
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
