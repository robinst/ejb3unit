/*
 * Created on Oct 27, 2008.
 */
package com.bm.testsuite.dataloader;

import com.bm.ejb3data.bo.MappedClass;
import com.bm.ejb3data.bo.MappedClassDocument;
import com.bm.ejb3data.bo.MappedClassDocumentProperty;
import com.bm.ejb3data.bo.MappedClassProperty;
import com.bm.ejb3guice.inject.Ejb3UnitInternalInject;
import com.bm.utils.injectinternal.InternalInjector;
import javax.persistence.EntityManager;
import junit.framework.TestCase;

/**
 *
 * @author Kamil Toman <kamil.toman@gmail.com>
 */
public class MappedSuperClassTest extends TestCase {

    @Ejb3UnitInternalInject
    private EntityManager em;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setUp() throws Exception {
        InternalInjector.createInternalInjector(
                MappedClass.class,
                MappedClassDocument.class,
                MappedClassProperty.class,
                MappedClassDocumentProperty.class
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
    public void testInheritedFields() {
        final CSVInitialDataSet<MappedClassDocument> document1Data =
            new CSVInitialDataSet<MappedClassDocument>(
            MappedClassDocument.class, "document1.csv", "id", "starRating", "name");

        assertNotNull(document1Data);
        // no exception ok

        String[] sqlStatements = document1Data.buildInsertSQL();

        assertEquals(1, sqlStatements.length);
        assertEquals("INSERT INTO MAPPED_CLASS_DOCUMENT (id, starRating, name) VALUES (?, ?, ?)", sqlStatements[0]);
    }


    /**
     * Testmethod.
     */
    public void testInheritedFieldsProperty() {
        final CSVInitialDataSet<MappedClassDocumentProperty> documentPropertyData =
            new CSVInitialDataSet<MappedClassDocumentProperty>(
            MappedClassDocumentProperty.class, "document1.csv", "id", "starRating", "name");

        assertNotNull(documentPropertyData);
        // no exception ok

        String[] sqlStatements = documentPropertyData.buildInsertSQL();

        assertEquals(1, sqlStatements.length);
        assertEquals("INSERT INTO MAPPED_CLASS_DOCUMENT_PROPERTY (id, starRating, name) VALUES (?, ?, ?)", sqlStatements[0]);
    }

    /**
     * Testmethod.
     */
    public void testWrite_document1() {
        final CSVInitialDataSet<MappedClassDocument> document1Data =
                new CSVInitialDataSet<MappedClassDocument>(
                MappedClassDocument.class, "document1.csv", "id", "starRating",
                "name");

        assertNotNull(document1Data);
        // no exception ok

        try {
            document1Data.create();

            MappedClassDocument doc = em.find(MappedClassDocument.class, 11L);
            assertNotNull(doc);

            assertEquals(11L, (long) doc.getId());
            assertEquals("document11", doc.getName());
            assertEquals(5, doc.getStarRating());
        } finally {
            if (document1Data != null)
                document1Data.cleanup(em);
        }
    }


    /**
     * Testmethod.
     */
    public void testWrite_documentProperty() {
        final CSVInitialDataSet<MappedClassDocumentProperty> documentPropertyData =
                new CSVInitialDataSet<MappedClassDocumentProperty>(
                MappedClassDocumentProperty.class, "document1.csv", "id", "starRating",
                "name");

        assertNotNull(documentPropertyData);
        // no exception ok

        try {
            documentPropertyData.create();

            MappedClassDocumentProperty doc = em.find(MappedClassDocumentProperty.class, 11L);
            assertNotNull(doc);

            assertEquals(11L, (long) doc.getId());
            assertEquals("document11", doc.getName());
            assertEquals(5, doc.getStarRating());
        } finally {
            if (documentPropertyData != null)
                documentPropertyData.cleanup(em);
        }
    }

}
