/*
 * Created on Oct 27, 2008.
 */
package com.bm.testsuite.fixture;

import com.bm.cfg.Ejb3UnitCfg;
import com.bm.ejb3data.bo.*;
import com.bm.ejb3guice.inject.Ejb3UnitInternalInject;
import com.bm.testsuite.dataloader.CSVInitialDataSet;
import com.bm.utils.SQLUtils;
import com.bm.utils.injectinternal.InternalInjector;
import javax.persistence.EntityManager;
import junit.framework.TestCase;

/**
 *
 * @author Kamil Toman <kamil.toman@gmail.com>
 */
public class OneToOneCompositeTest extends TestCase {

    private static final CSVInitialDataSet<VersionedOneB> toTestOneToOneB = new CSVInitialDataSet<VersionedOneB>(
            VersionedOneB.class, "versionedOneB.csv", "id", "version", "bFeature");
    private static final CSVInitialDataSet<VersionedOneA> toTestOneToOneA = new CSVInitialDataSet<VersionedOneA>(
            VersionedOneA.class, "versionedOneA.csv", "id", "version", "aFeature", "oneB(ONE_B_ID)", "oneB(ONE_B_VERSION)",
            "uniOneB(UNI_ONE_B_ID)", "uniOneB(UNI_ONE_B_VERSION)");
    private static final CSVInitialDataSet<VersionedOneBProperty> toTestOneToOneBProperty = new CSVInitialDataSet<VersionedOneBProperty>(
            VersionedOneBProperty.class, "versionedOneB.csv", "id", "version", "BFeature");
    private static final CSVInitialDataSet<VersionedOneAProperty> toTestOneToOneAProperty = new CSVInitialDataSet<VersionedOneAProperty>(
            VersionedOneAProperty.class, "versionedOneA.csv", "id", "version", "AFeature", "oneB(ONE_B_ID)", "oneB(ONE_B_VERSION)",
            "uniOneB(UNI_ONE_B_ID)", "uniOneB(UNI_ONE_B_VERSION)");
    @Ejb3UnitInternalInject
    private EntityManager em;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setUp() throws Exception {
        InternalInjector.createInternalInjector(
                VersionedOneB.class,
                VersionedOneA.class,
                VersionedOneBProperty.class,
                VersionedOneAProperty.class
                ).injectMembers(this);
        super.setUp();
    }

    /**
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        em.clear();
        em.getTransaction().begin();
        toTestOneToOneA.cleanup(em);
        toTestOneToOneB.cleanup(em);
        toTestOneToOneAProperty.cleanup(em);
        toTestOneToOneBProperty.cleanup(em);
        super.tearDown();
        em.getTransaction().commit();
    }

    /**
     * Testmethod.
     */
    public void testWrite_OneToOne() {
        toTestOneToOneB.create();
        toTestOneToOneA.create();
    }

    /**
     * Testmethod.
     */
    public void testWrite_OneToOneProperty() {
        toTestOneToOneBProperty.create();
        toTestOneToOneAProperty.create();
    }
}
