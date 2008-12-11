/*
 * Created on Oct 27, 2008.
 */
package com.bm.testsuite.dataloader;

import com.bm.ejb3data.bo.AbstractDocument;
import com.bm.ejb3data.bo.Document1;
import com.bm.ejb3data.bo.Document2;
import com.bm.ejb3data.bo.Document3;
import com.bm.ejb3data.bo.Document4;
import com.bm.ejb3data.bo.DocumentBlank;
import com.bm.ejb3data.bo.DocumentBlankProperty;
import com.bm.ejb3data.bo.ExpertiseAreasPK;
import com.bm.ejb3data.bo.UserDocument;
import com.bm.ejb3guice.inject.Ejb3UnitInternalInject;
import com.bm.testsuite.dataloader.CSVInitialDataSet.PropertyPosition;
import com.bm.utils.injectinternal.InternalInjector;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import junit.framework.TestCase;

/**
 *
 * @author Kamil Toman <kamil.toman@gmail.com>
 */
public class DocumentInheritanceTest extends TestCase {

    @Ejb3UnitInternalInject
    private EntityManager em;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setUp() throws Exception {
        InternalInjector.createInternalInjector(
                Document1.class,
                Document2.class,
                Document3.class,
                Document4.class,
                DocumentBlank.class,
                DocumentBlankProperty.class,
                UserDocument.class
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
        final CSVInitialDataSet<Document1> document1Data =
            new CSVInitialDataSet<Document1>(
            Document1.class, "document1.csv", "id", "starRating", "name");

        assertNotNull(document1Data);
        // no exception ok

        String[] sqlStatements = document1Data.buildInsertSQL();

        assertEquals(2, sqlStatements.length);
        assertEquals("INSERT INTO DOCUMENTS (id, name) VALUES (?, ?)", sqlStatements[0]);
        assertEquals("INSERT INTO DOCUMENT1 (starRating, id) VALUES (?, ?)", sqlStatements[1]);

        final List<PropertyPosition> idPositions = document1Data.getCsvMapping(0);
        assertEquals(2, idPositions.size());
        assertEquals("id", idPositions.get(0).getProperty().getName());
        assertEquals(0, idPositions.get(0).getBuilderPosition());
        assertEquals(0, idPositions.get(0).getLocalPosition());
        assertEquals("id", idPositions.get(1).getProperty().getName());
        assertEquals(1, idPositions.get(1).getBuilderPosition());
        assertEquals(1, idPositions.get(1).getLocalPosition());

        final List<PropertyPosition> starRatingPositions = document1Data.getCsvMapping(1);
        assertEquals(1, starRatingPositions.size());
        assertEquals("starRating", starRatingPositions.get(0).getProperty().getName());
        assertEquals(1, starRatingPositions.get(0).getBuilderPosition());
        assertEquals(0, starRatingPositions.get(0).getLocalPosition());

        final List<PropertyPosition> namePositions = document1Data.getCsvMapping(2);
        assertEquals(1, namePositions.size());
        assertEquals("name", namePositions.get(0).getProperty().getName());
        assertEquals(0, namePositions.get(0).getBuilderPosition());
        assertEquals(1, namePositions.get(0).getLocalPosition());
    }


    /**
     * Testmethod.
     */
    public void testInheritedFields2() {
        final CSVInitialDataSet<Document3> document3Data =
            new CSVInitialDataSet<Document3>(
            Document3.class, "document3.csv", "id", "starRating", "name");

        assertNotNull(document3Data);
        // no exception ok

        String[] sqlStatements = document3Data.buildInsertSQL();

        assertEquals(2, sqlStatements.length);
        assertEquals("INSERT INTO DOCUMENTS2 (id, name) VALUES (?, ?)", sqlStatements[0]);
        assertEquals("INSERT INTO DOCUMENT3 (starRating, id) VALUES (?, ?)", sqlStatements[1]);

        final List<PropertyPosition> idPositions = document3Data.getCsvMapping(0);
        assertEquals(2, idPositions.size());
        assertEquals("id", idPositions.get(0).getProperty().getName());
        assertEquals(0, idPositions.get(0).getBuilderPosition());
        assertEquals(0, idPositions.get(0).getLocalPosition());
        assertEquals("id", idPositions.get(1).getProperty().getName());
        assertEquals(1, idPositions.get(1).getBuilderPosition());
        assertEquals(1, idPositions.get(1).getLocalPosition());

        final List<PropertyPosition> starRatingPositions = document3Data.getCsvMapping(1);
        assertEquals(1, starRatingPositions.size());
        assertEquals("starRating", starRatingPositions.get(0).getProperty().getName());
        assertEquals(1, starRatingPositions.get(0).getBuilderPosition());
        assertEquals(0, starRatingPositions.get(0).getLocalPosition());

        final List<PropertyPosition> namePositions = document3Data.getCsvMapping(2);
        assertEquals(1, namePositions.size());
        assertEquals("name", namePositions.get(0).getProperty().getName());
        assertEquals(0, namePositions.get(0).getBuilderPosition());
        assertEquals(1, namePositions.get(0).getLocalPosition());
    }


    /**
     * Testmethod.
     */
    public void testWrite_document1() {
        final CSVInitialDataSet<Document1> document1Data =
                new CSVInitialDataSet<Document1>(
                Document1.class, "document1.csv", "id", "starRating",
                "name");

        assertNotNull(document1Data);
        // no exception ok

        try {
            document1Data.create();

            Document1 doc = em.find(Document1.class, 11L);
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
    public void testWrite_document3() {
        final CSVInitialDataSet<Document3> document3Data =
                new CSVInitialDataSet<Document3>(
                Document3.class, "document1.csv", "id", "starRating",
                "name");

        assertNotNull(document3Data);
        // no exception ok

        try {
            document3Data.create();

            Document3 doc = em.find(Document3.class, 11L);
            assertNotNull(doc);

            assertEquals(11L, (long) doc.getId());
            assertEquals("document11", doc.getName());
            assertEquals(5, doc.getStarRating());
        } finally {
            if (document3Data != null)
                document3Data.cleanup(em);
        }
    }


    /**
     * Testmethod.
     */
    public void testWrite_document2() {
        final CSVInitialDataSet<Document2> document2Data =
                new CSVInitialDataSet<Document2>(
                Document2.class, "document2.csv", "id", "starRating",
                "name", "fullReview");

        assertNotNull(document2Data);
        // no exception ok

        try {
            document2Data.create();

            Document2 doc = em.find(Document2.class, 2L);
            assertNotNull(doc);

            assertEquals(2L, (long) doc.getId());
            assertEquals("document2", doc.getName());
            assertEquals(2, doc.getStarRating());
            assertEquals("hilarious", doc.getFullReview());
        } finally {
            if (document2Data != null)
                document2Data.cleanup(em);
        }
    }

    /**
     * Testmethod.
     */
    public void testWrite_document4() {
        final CSVInitialDataSet<Document4> document4Data =
                new CSVInitialDataSet<Document4>(
                Document4.class, "document2.csv", "id", "starRating",
                "name", "fullReview");

        assertNotNull(document4Data);
        // no exception ok

        try {
            document4Data.create();

            Document4 doc = em.find(Document4.class, 2L);
            assertNotNull(doc);

            assertEquals(2L, (long) doc.getId());
            assertEquals("document2", doc.getName());
            assertEquals(2, doc.getStarRating());
            assertEquals("hilarious", doc.getFullReview());
        } finally {
            if (document4Data != null)
                document4Data.cleanup(em);
        }
    }

    /**
     * Testmethod.
     */
    public void testWrite_documentBlank() {
        final CSVInitialDataSet<DocumentBlank> documentBlankData =
                new CSVInitialDataSet<DocumentBlank>(
                DocumentBlank.class, "documentblank.csv", "id", "name", "superfluous");

        assertNotNull(documentBlankData);
        // no exception ok

        try {
            documentBlankData.create();

            DocumentBlank doc = em.find(DocumentBlank.class, 13L);
            assertNotNull(doc);

            assertEquals(13L, (long) doc.getId());
            assertEquals("document blank 13", doc.getName());
        } finally {
            if (documentBlankData != null)
                documentBlankData.cleanup(em);
        }
    }

    /**
     * Testmethod.
     */
    public void testWrite_documentBlankProperty() {
        final CSVInitialDataSet<DocumentBlankProperty> documentBlankDataProperty =
                new CSVInitialDataSet<DocumentBlankProperty>(
                DocumentBlankProperty.class, "documentblank.csv", "id", "name", "superfluous");

        assertNotNull(documentBlankDataProperty);
        // no exception ok

        try {
            documentBlankDataProperty.create();

            DocumentBlankProperty doc = em.find(DocumentBlankProperty.class, 13L);
            assertNotNull(doc);

            assertEquals(13L, (long) doc.getId());
            assertEquals("document blank 13", doc.getName());
        } finally {
            if (documentBlankDataProperty != null)
                documentBlankDataProperty.cleanup(em);
        }
    }


    /**
     * Testmethod.
     */
    public void testDiscriminator() {
        final CSVInitialDataSet<Document1> document1Data =
                new CSVInitialDataSet<Document1>(
                Document1.class, "document1.csv", "id", "starRating",
                "name");
        final CSVInitialDataSet<Document2> document2Data =
                new CSVInitialDataSet<Document2>(
                Document2.class, "document2.csv", "id", "starRating",
                "name", "fullReview");
        try {
            document1Data.create();
            document2Data.create();

            Document2 doc2 = em.find(Document2.class, 11L);
            assertNull(doc2);

            Document1 doc1 = em.find(Document1.class, 2L);
            assertNotNull(doc1);

            AbstractDocument doc = em.find(AbstractDocument.class, 1L);
            assertNotNull(doc);
            assertTrue(doc instanceof Document1);

            doc = em.find(AbstractDocument.class, 2L);
            assertNotNull(doc);
            assertTrue(doc instanceof Document2);

        } finally {
            if (document1Data != null)
                document1Data.cleanup(em);
            if (document2Data != null)
                document2Data.cleanup(em);
        }
    }


    /**
     * Testmethod.
     */
    public void testWrite_userDocument() {
        final CSVInitialDataSet<UserDocument> userDocData =
                new CSVInitialDataSet<UserDocument>(
                UserDocument.class, "document3.csv",
                "prodId", "userId", "name", "note");

        assertNotNull(userDocData);
        // no exception ok

        try {
            userDocData.create();

            UserDocument doc = em.find(UserDocument.class,
                    new ExpertiseAreasPK(10L, 20L));
            assertNotNull(doc);

            assertEquals(10L, (long) doc.getProdId());
            assertEquals(20L, (long) doc.getUserId());
            assertEquals("user document", doc.getName());
            assertEquals("crap", doc.getNote());
        } finally {
            if (userDocData != null)
                userDocData.cleanup(em);
        }
    }
    
}
