package com.bm.testsuite.fixture;

import javax.persistence.EntityManager;

import junit.framework.TestCase;

import com.bm.ejb3data.bo.StockWKNBo;
import com.bm.ejb3guice.inject.Inject;
import com.bm.testsuite.dataloader.CSVInitialDataSet;
import com.bm.utils.injectinternal.InternalInjector;

/**
 * Testclass for the CSVInitialDataNoRelationalSet.
 * 
 * @author Daniel Wiese
 * @since 17.04.2006
 */
public class CompressedCSVInitialDataSetTest extends TestCase {
	
	private final CSVInitialDataSet<StockWKNBo> toTest = new CSVInitialDataSet<StockWKNBo>(
			StockWKNBo.class, "allstatData.csv.zip", true, "wkn",
			"aktienName", "isin", "symbol", "kaufModus", "branchenCode",
			"branche", "transaktionenProTag", "zumHandelZugelassen",
			"volatilitaet", "durchschnittskaufkurs");
	
	@Inject
	private EntityManager em;
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void setUp() throws Exception {
		InternalInjector.createInternalInjector(StockWKNBo.class).injectMembers(this);
		super.setUp();
	}

	/**
	 * @see junit.framework.TestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception {
		toTest.cleanup(em);
	}

	/**
	 * Testmethod.
	 */
	public void testRead_happyPath() {
		toTest.create();
	}
}
