/*
 * Created on Nov 24, 2008.
 * (C) 2008 ICZ, a.s.
 */
package com.bm.testsuite.dataloader;

import com.bm.ejb3data.bo.BlobItem;
import com.bm.ejb3guice.inject.Ejb3UnitInternalInject;
import com.bm.utils.injectinternal.InternalInjector;
import javax.persistence.EntityManager;
import junit.framework.TestCase;

/**
 *
 * @author Kamil Toman <kamil.toman@gmail.com>
 */
public class BlobLoadingTest extends TestCase {

    @Ejb3UnitInternalInject
    private EntityManager em;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setUp() throws Exception {
        InternalInjector.createInternalInjector(
                BlobItem.class
                ).injectMembers(this);
        super.setUp();
    }

    /**
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Testmethod.
     */
    public void testWrite_happyPath() {
    final CSVInitialDataSet<BlobItem> toTest = new CSVInitialDataSet<BlobItem>(
            BlobItem.class, "blobItem.csv", "id", "desc",
            "blobData", "blobData2");

        try {
            toTest.create();
        } finally {
            if (toTest != null)
                toTest.cleanup(em);
        }
    }

    /**
     * Testmethod.
     */
    public void testWrite_BlobItem() {
    final CSVInitialDataSet<BlobItem> toTest = new CSVInitialDataSet<BlobItem>(
            BlobItem.class, "blobItem.csv", "id", "desc",
            "blobData", "blobData2");

        assertNotNull(toTest);
        // no exception ok

        try {
            toTest.create();

            BlobItem item = em.find(BlobItem.class, 1L);
            assertNotNull(item);

            assertEquals(1L, (long) item.getId());
            assertEquals("first", item.getDesc());
            assertEquals("Hello world!", new String(item.getBlobData()));
            assertEquals(null, item.getBlobData2());

            BlobItem item2 = em.find(BlobItem.class, 2L);
            assertNotNull(item);

            assertEquals(2L, (long) item2.getId());
            assertEquals("second", item2.getDesc());
            assertEquals("Hello world!", new String(item2.getBlobData()));
            assertEquals(null, item2.getBlobData2());

            BlobItem item3 = em.find(BlobItem.class, 3L);
            assertNotNull(item);

            assertEquals(3L, (long) item3.getId());
            assertEquals("third", item3.getDesc());
            assertEquals("Hello world!", new String(item3.getBlobData()));
            assertEquals("Hello world!", new String(item3.getBlobData2()));
            
            
        } finally {
            if (toTest != null)
                toTest.cleanup(em);
        }
    }
}
