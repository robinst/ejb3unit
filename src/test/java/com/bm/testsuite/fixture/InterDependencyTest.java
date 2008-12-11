/*
 * Created on Oct 27, 2008.
 */
package com.bm.testsuite.fixture;

import com.bm.cfg.Ejb3UnitCfg;
import com.bm.testsuite.dataloader.*;
import com.bm.ejb3data.bo.*;
import com.bm.ejb3guice.inject.Ejb3UnitInternalInject;
import com.bm.utils.BasicDataSource;
import com.bm.utils.SQLUtils;
import com.bm.utils.injectinternal.InternalInjector;
import java.sql.Connection;
import java.sql.SQLException;
import javax.persistence.EntityManager;
import junit.framework.TestCase;

/**
 *
 * @author Kamil Toman <kamil.toman@gmail.com>
 */
public class InterDependencyTest extends TestCase {

    private static final CSVInitialDataSet<InterDepItem> interDepItem = new CSVInitialDataSet<InterDepItem>(
            InterDepItem.class, "interDepItem.csv", "id", "order");
    private static final CSVInitialDataSet<InterDepOrder> interDepOrder = new CSVInitialDataSet<InterDepOrder>(
            InterDepOrder.class, "interDepOrder.csv", "id", "mainItem");
    @Ejb3UnitInternalInject
    private EntityManager em;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setUp() throws Exception {
        InternalInjector.createInternalInjector(
                InterDepItem.class,
                InterDepOrder.class).injectMembers(this);
        super.setUp();
    }

    /**
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {

        SQLUtils.disableReferentialIntegrity(Ejb3UnitCfg.getConfiguration());
        em.getTransaction().begin();
        interDepOrder.cleanup(em);
        interDepItem.cleanup(em);
        em.getTransaction().commit();
        SQLUtils.enableReferentialIntegrity(Ejb3UnitCfg.getConfiguration());
        super.tearDown();
    }

    private void loadCsv() {
        BasicDataSource ds = new BasicDataSource(Ejb3UnitCfg.
                getConfiguration());
        Connection con = null;
        try {
            con = ds.getConnection();
            con.setAutoCommit(false);

            if (Ejb3UnitCfg.getConfiguration().isInMemory()) {
                // disable referential integrity for csv loads in H2
                // one have to do this manually in external RDBMS
                SQLUtils.disableReferentialIntegrity(con);
            }

            interDepItem.create(con);
            interDepOrder.create(con);

            if (Ejb3UnitCfg.getConfiguration().isInMemory()) {
                // enable referential integrity for csv loads in H2
                // one have to do this manually in external RDBMS
                SQLUtils.enableReferentialIntegrity(con);
            }

            con.commit();
        } catch (SQLException e) {
            try {
                con.rollback();
            } catch (Exception ex) {
            }
            throw new RuntimeException(
                    "Can't get database connection: ", e);
        } finally {
            SQLUtils.cleanup(con);
        }
    }

    /**
     * Testmethod.
     */
    public void testWrite_interDependency() {

        loadCsv();
        InterDepOrder order1 = em.find(InterDepOrder.class, 1L);
        assertEquals(1L, (long) order1.getId());
        assertEquals(3L, (long) order1.getMainItem().getId());
        InterDepOrder order2 = em.find(InterDepOrder.class, 2L);
        assertEquals(2L, (long) order2.getId());
        assertEquals(10L, (long) order2.getMainItem().getId());
    }
}
