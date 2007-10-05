package com.bm.testsuite.fixture;

import junit.framework.TestCase;

import com.bm.cfg.Ejb3UnitCfg;
import com.bm.data.bo.StockWKNBo;
import com.bm.data.bo.StockWKNBoWithShema;
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
			StockWKNBo.class, "allstatData.csv", "wkn", "aktienName", "isin", "symbol",
			"kaufModus", "branchenCode", "branche", "transaktionenProTag",
			"zumHandelZugelassen", "volatilitaet", "durchschnittskaufkurs");
	
	private final CSVInitialDataSet toTestWithSchema = new CSVInitialDataSet<StockWKNBoWithShema>(
			StockWKNBoWithShema.class, "allstatData.csv", false, true, "wkn", "aktienName", "isin", "symbol",
			"kaufModus", "branchenCode", "branche", "transaktionenProTag",
			"zumHandelZugelassen", "volatilitaet", "durchschnittskaufkurs");

	/**
	 * @see junit.framework.TestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception {
		final BasicDataSource ds = new BasicDataSource(Ejb3UnitCfg.getConfiguration());
		toTest.cleanup(ds);
	}

	/**
	 * Testmethod.
	 */
	public void testRead_happyPath() {
		toTest.create();
	}

	/**
	 * Testmethod.
	 */
	public void testSQLisCorrent_withoutShema() {
		assertEquals("INSERT INTO stocks (aid, name, isin, symbol, isKaufModus, brancheCode, brancheName, transaktionenTag, zumHandelZugelassen, volatilitaet, durschnittskaufkurs) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", toTest.buildInsertSQL());
	}
	
	/**
	 * Testmethod.
	 */
	public void testSQLisCorrent_withShema() {
		assertEquals("INSERT INTO foo.stocks (aid, name, isin, symbol, isKaufModus, brancheCode, brancheName, transaktionenTag, zumHandelZugelassen, volatilitaet, durschnittskaufkurs) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", toTestWithSchema.buildInsertSQL());
	}
}
