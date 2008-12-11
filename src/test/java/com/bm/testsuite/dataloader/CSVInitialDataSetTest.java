package com.bm.testsuite.dataloader;

import com.bm.ejb3data.bo.ItemCodebook;
import javax.persistence.EntityManager;

import junit.framework.TestCase;

import com.bm.ejb3data.bo.EmbeddedExampleBo;
import com.bm.ejb3data.bo.IdClassExampleBo;
import com.bm.ejb3data.bo.ItemEnumBo;
import com.bm.ejb3data.bo.StockWKNBo;
import com.bm.ejb3data.bo.StockWKNBoWithShema;
import com.bm.ejb3guice.inject.Ejb3UnitInternalInject;
import com.bm.utils.injectinternal.InternalInjector;

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

	private static final CSVInitialDataSet<IdClassExampleBo> toTestWithIdClass = new CSVInitialDataSet<IdClassExampleBo>(
			IdClassExampleBo.class, "trades.csv", "wkn", "day", "framenr",
			"price", "volume", "transactions");
        
        private static final CSVInitialDataSet<ItemCodebook> toTestWithEnum2 = new CSVInitialDataSet<ItemCodebook>(
			ItemCodebook.class, "itemcodebook.csv", "id", "code");
        
        private static final CSVInitialDataSet<ItemEnumBo> toTestWithEnum = new CSVInitialDataSet<ItemEnumBo>(
			ItemEnumBo.class, "enum.csv", "wkn", "enumField", "enumFieldString", "itemCodebook");
        

	@Ejb3UnitInternalInject
	private EntityManager em;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void setUp() throws Exception {
		InternalInjector.createInternalInjector(StockWKNBo.class,
				EmbeddedExampleBo.class,
                                IdClassExampleBo.class,
                                ItemCodebook.class,
                                ItemEnumBo.class
                                ).injectMembers(this);
		super.setUp();
	}
	
	/**
	 * @see junit.framework.TestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception {
		toTest.cleanup(em);
                super.tearDown();
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
	public void testSQLisCorrect_withoutShema() {
		assertEquals(
				"INSERT INTO stocks (aid, name, isin, symbol, isKaufModus, brancheCode, brancheName, transaktionenTag, zumHandelZugelassen, volatilitaet, durschnittskaufkurs) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
				toTest.buildInsertSQL()[0]);
	}

	/**
	 * Testmethod.
	 */
	public void testSQLisCorrect_withShema() {
		assertEquals(
				"INSERT INTO foo.stocks (aid, name, isin, symbol, isKaufModus, brancheCode, brancheName, transaktionenTag, zumHandelZugelassen, volatilitaet, durschnittskaufkurs) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
				toTestWithSchema.buildInsertSQL()[0]);
	}

	/**
	 * Testmethod.
	 */
	public void testWrite_embeddedCSV_buildSQL() {
		assertEquals(
				"INSERT INTO testembedded (wkn, day, framenr, price, volume, transactions) VALUES (?, ?, ?, ?, ?, ?)",
				toTestWithEmbedded.buildInsertSQL()[0]);
	}

	/**
	 * Testmethod.
	 */
	public void testWrite_idClassCSV_buildSQL() {
		assertEquals(
				"INSERT INTO testidclass (wkn_id, day_id, framenr_id, price, volume, transactions) VALUES (?, ?, ?, ?, ?, ?)",
				toTestWithIdClass.buildInsertSQL()[0]);
	}
        
	/**
	 * Testmethod.
	 */
	public void testWrite_idClassCSV_buildSQLComplex_HibernateBug() {
            try {
		toTestWithIdClass.create();
            } catch (RuntimeException e) {
                // This shows a bug in hibernate handling 
                // of @IdClass/@Column attributes
            }
	}

	/**
	 * Testmethod.
	 */
	public void testWrite_Enum() {
            toTestWithEnum2.create();
            toTestWithEnum.create();
	}

}
