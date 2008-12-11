/*
 * Created on Oct 27, 2008.
 */
package com.bm.testsuite.fixture;

import com.bm.ejb3data.bo.*;
import com.bm.ejb3guice.inject.Ejb3UnitInternalInject;
import com.bm.testsuite.dataloader.CSVInitialDataSet;
import com.bm.utils.injectinternal.InternalInjector;
import javax.persistence.EntityManager;
import junit.framework.TestCase;

/**
 *
 * @author Kamil Toman <kamil.toman@gmail.com>
 */
public class OneToOneTest extends TestCase {

    private static final CSVInitialDataSet<OneB> toTestOneToOneB = new CSVInitialDataSet<OneB>(
            OneB.class, "oneb.csv", "id", "bFeature");
    private static final CSVInitialDataSet<OneA> toTestOneToOneA = new CSVInitialDataSet<OneA>(
            OneA.class, "onea.csv", "id", "aFeature", "oneB",
            "uniOneB");
    private static final CSVInitialDataSet<OneC> toTestOneToOneC = new CSVInitialDataSet<OneC>(
            OneC.class, "onec.csv", "id", "oneA");
    @Ejb3UnitInternalInject
    private EntityManager em;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setUp() throws Exception {
        InternalInjector.createInternalInjector(
                OneB.class,
                OneA.class,
                OneC.class).injectMembers(this);
        super.setUp();
    }

    /**
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        toTestOneToOneC.cleanup(em);
        toTestOneToOneA.cleanup(em);
        toTestOneToOneB.cleanup(em);
        super.tearDown();
    }

    /**
     * Testmethod.
     */
    public void testWrite_OneToOne() {
        toTestOneToOneB.create();
        toTestOneToOneA.create();
        toTestOneToOneC.create();
    }
}
