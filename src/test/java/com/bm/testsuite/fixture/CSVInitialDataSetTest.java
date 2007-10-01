package com.bm.testsuite.fixture;

import junit.framework.TestCase;

import com.bm.cfg.Ejb3UnitCfg;
import com.bm.data.bo.StockWKNBo;
import com.bm.testsuite.dataloader.CSVInitialDataSet;
import com.bm.utils.BasicDataSource;

/**
 * Testclass for the CSVInitialDataSet.
 * 
 * @author Daniel Wiese
 * @since 17.04.2006
 */
public class CSVInitialDataSetTest extends TestCase {
	
	private final CSVInitialDataSet toTest = new CSVInitialDataSet<StockWKNBo>(
			StockWKNBo.class, "allstatData.csv", "wkn", "aktienName",
			"isin", "symbol", "kaufModus", "branchenCode", "branche",
			"transaktionenProTag", "zumHandelZugelassen", "volatilitaet",
	"durchschnittskaufkurs");

	/**
	 * @see junit.framework.TestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception {
		final BasicDataSource ds = new BasicDataSource(Ejb3UnitCfg
				.getConfiguration());
		toTest.cleanup(ds);
	}

	/**
	 * Testmethod.
	 */
	public void testRead_happyPath() {
		toTest.create();
	}
}
