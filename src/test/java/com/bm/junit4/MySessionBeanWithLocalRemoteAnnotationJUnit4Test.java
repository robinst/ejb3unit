package com.bm.junit4;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.bm.ejb3data.bo.ExpertiseAreas;
import com.bm.ejb3data.bo.MySessionBeanWithLocalRemoteAnnotation;
import com.bm.ejb3data.bo.StockWKNBo;
import com.bm.testsuite.dataloader.CSVInitialDataSet;
import com.bm.testsuite.junit4.BaseSessionBeanJUnit4Fixture;

/**
 * Tests session beans with Local or Remote annotations on the bean (not just on the interfaces).
 * See bug 1964677. Thanks to Rob di Marco for pointing this out and providing patches.
 */
public class MySessionBeanWithLocalRemoteAnnotationJUnit4Test extends
		BaseSessionBeanJUnit4Fixture<MySessionBeanWithLocalRemoteAnnotation> {

	private static final Class<?>[] usedBeans = { StockWKNBo.class,
			ExpertiseAreas.class };

	private static final CSVInitialDataSet<StockWKNBo> CSV_SET = new CSVInitialDataSet<StockWKNBo>(
			StockWKNBo.class, "allstatData.csv", "wkn", "aktienName", "isin",
			"symbol", "kaufModus", "branchenCode", "branche",
			"transaktionenProTag", "zumHandelZugelassen", "volatilitaet",
			"durchschnittskaufkurs");

	/**
	 * Constructor.
	 */
	public MySessionBeanWithLocalRemoteAnnotationJUnit4Test() {
		super(MySessionBeanWithLocalRemoteAnnotation.class, usedBeans,
				new MySessionBeanJUnit4Test.StockWKNEntityInitialDataSet(), CSV_SET);
	}

	/**
	 * Test the dependency injection.
	 */
	@Test
	public void dependencyInjection() {
		final MySessionBeanWithLocalRemoteAnnotation toTest = this.getBeanToTest();
		assertNotNull(toTest);
		assertNotNull(toTest.getDs());
		assertNotNull(toTest.getEm());
	}

}
