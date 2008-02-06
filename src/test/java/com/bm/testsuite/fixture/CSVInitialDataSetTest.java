package com.bm.testsuite.fixture;

import junit.framework.TestCase;

import com.bm.cfg.Ejb3UnitCfg;
import com.bm.ejb3data.bo.EmbeddedExampleBo;
import com.bm.ejb3data.bo.StockWKNBo;
import com.bm.ejb3data.bo.StockWKNBoWithShema;
import com.bm.testsuite.dataloader.CSVInitialDataSet;

/**
 * Testclass for the CSVInitialDataNoRelationalSet.
 * 
 * @author Daniel Wiese
 * @since 17.04.2006
 */
public class CSVInitialDataSetTest extends TestCase {

	private final CSVInitialDataSet<StockWKNBo> toTest = new CSVInitialDataSet<StockWKNBo>(
			StockWKNBo.class, "allstatData.csv", "wkn", "aktienName", "isin", "symbol",
			"kaufModus", "branchenCode", "branche", "transaktionenProTag", "zumHandelZugelassen",
			"volatilitaet", "durchschnittskaufkurs");

	private final CSVInitialDataSet<StockWKNBoWithShema> toTestWithSchema = new CSVInitialDataSet<StockWKNBoWithShema>(
			StockWKNBoWithShema.class, "allstatData.csv", false, true, "wkn", "aktienName", "isin",
			"symbol", "kaufModus", "branchenCode", "branche", "transaktionenProTag",
			"zumHandelZugelassen", "volatilitaet", "durchschnittskaufkurs");

	private static final CSVInitialDataSet<EmbeddedExampleBo> toTestWithEmbedded = new CSVInitialDataSet<EmbeddedExampleBo>(
			EmbeddedExampleBo.class, "trades.csv", "wkn", "day", "framenr", "price", "volume",
			"transactions");

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
	public void testWrite_simpleCSV_happyPath() {
		toTest.create();
	}

	/**
	 * Testmethod.
	 */
	public void testWrite_embeddedCSV_happyPath() {
		toTestWithEmbedded.create();
	}

	/**
	 * Testmethod.
	 */
	public void testSQLisCorrent_withoutShema() {
		assertEquals(
				"INSERT INTO stocks (aid, name, isin, symbol, isKaufModus, brancheCode, brancheName, transaktionenTag, zumHandelZugelassen, volatilitaet, durschnittskaufkurs) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
				toTest.buildInsertSQL());
	}

	/**
	 * Testmethod.
	 */
	public void testSQLisCorrent_withShema() {
		assertEquals(
				"INSERT INTO foo.stocks (aid, name, isin, symbol, isKaufModus, brancheCode, brancheName, transaktionenTag, zumHandelZugelassen, volatilitaet, durschnittskaufkurs) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
				toTestWithSchema.buildInsertSQL());
	}
}
