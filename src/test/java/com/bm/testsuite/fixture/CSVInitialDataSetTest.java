package com.bm.testsuite.fixture;

import com.bm.data.bo.StockWKNBo;
import com.bm.testsuite.dataloader.CSVInitialDataSet;

import junit.framework.TestCase;

/**
 * Testclass for the CSVInitialDataSet.
 * 
 * @author Daniel Wiese
 * @since 17.04.2006
 */
public class CSVInitialDataSetTest extends TestCase {

	/**
	 * Testmethod.
	 */
	public void testRead_happyPath() {
		final CSVInitialDataSet toTest = new CSVInitialDataSet<StockWKNBo>(
				StockWKNBo.class, "allstatData.csv", "wkn", "aktienName",
				"isin", "symbol", "kaufModus", "branchenCode", "branche",
				"transaktionenProTag", "zumHandelZugelassen", "volatilitaet",
				"durchschnittskaufkurs");
		toTest.create();
	}
}
