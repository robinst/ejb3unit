package com.bm.testsuite.fixture;

import junit.framework.TestCase;

import com.bm.cfg.Ejb3UnitCfg;
import com.bm.ejb3data.bo.StockWKNBo;
import com.bm.testsuite.dataloader.CSVInitialDataNoRelationalSet;

/**
 * Testclass for the CSVInitialDataNoRelationalSet.
 * 
 * @author Daniel Wiese
 * @since 17.04.2006
 */
public class CompressedCSVInitialDataSetTest extends TestCase {
	
	private final CSVInitialDataNoRelationalSet toTest = new CSVInitialDataNoRelationalSet<StockWKNBo>(
			StockWKNBo.class, "allstatData.csv.zip", true, "wkn",
			"aktienName", "isin", "symbol", "kaufModus", "branchenCode",
			"branche", "transaktionenProTag", "zumHandelZugelassen",
			"volatilitaet", "durchschnittskaufkurs");
	
	/**
	 * @see junit.framework.TestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception {
		Ejb3UnitCfg.addEntytiesToTest(StockWKNBo.class);
		toTest.cleanup(Ejb3UnitCfg.getConfiguration().getEntityManagerFactory()
				.createEntityManager());
	}

	/**
	 * Testmethod.
	 */
	public void testRead_happyPath() {
		toTest.create();
	}
}
