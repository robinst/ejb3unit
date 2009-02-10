package com.bm.testsuite;

import com.bm.cfg.Ejb3UnitCfg;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import com.bm.ejb3guice.inject.Ejb3UnitInternalInject;
import com.bm.ejb3guice.inject.Injector;
import com.bm.ejb3guice.inject.Provider;
import com.bm.jndi.Ejb3UnitJndiBinder;
import com.bm.testsuite.dataloader.CSVInitialDataSet;
import com.bm.testsuite.dataloader.EntityInitialDataSet;
import com.bm.testsuite.dataloader.InitialDataSet;
import com.bm.utils.BasicDataSource;
import com.bm.utils.SQLUtils;
import com.bm.utils.injectinternal.InternalInjector;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

/**
 * Baseclass for all Fixtures with Entity-Manager support.
 * 
 * @author Fabian Bauschulte
 * 
 */
public class BaseFixture extends BaseTest {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.
            getLogger(BaseFixture.class);

    /**
     * Default costructor.
     */
    public BaseFixture(InitialDataSet[] initalDataSet) {
        this.initalDataSet = initalDataSet;
    }

    /**
     * Default costructor.
     */
    public BaseFixture() {
        this(null);

    }
    @Ejb3UnitInternalInject
    private Ejb3UnitJndiBinder jndiBinder;
    @Ejb3UnitInternalInject
    private Provider<EntityManager> entityManagerProv;
    private Injector injector;
    private final InitialDataSet[] initalDataSet;
    /**
     * If an exception during construction occurs, it is stored here to fail the
     * tests.
     */
    private EntityInitializationException initializationError;

    /**
     * @author Fabian Bauschulte
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    public void setUp() throws Exception {
        log.debug("BaseFixture setUp");
        super.setUp();
        fireExceptionIfNotInitialized();
        injector.injectMembers(this);
        this.jndiBinder.bind();
        entityManagerProv.get().clear();

        // In case there are Initialdatasets they are persited
        if (this.initalDataSet != null) {
            loadCsvFirst();
            loadEntitySet();
        }
    }

    private void loadCsvFirst() {
        int csvCount = 0;
        for (InitialDataSet current : this.initalDataSet) {
            if (current instanceof CSVInitialDataSet) {
                csvCount++;
            }
        }
        if (csvCount > 0) {
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

                for (InitialDataSet current : this.initalDataSet) {
                    // insert entity manager
                    if (current instanceof CSVInitialDataSet) {
                        ((CSVInitialDataSet) current).create(con);
                    }
                }

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
    }

    private void loadEntitySet() throws Exception {
        EntityManager em = getEntityManagerProv().get();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            for (InitialDataSet current : this.initalDataSet) {
                // insert entity manager
                if (current instanceof EntityInitialDataSet) {
                    EntityInitialDataSet<?> curentEntDs = (EntityInitialDataSet<?>) current;
                    curentEntDs.setEntityManager(em);
                    current.create();
                }
            }
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            throw e;
        }
    }

    private void removeEntitySet(EntityManager em) {
        for (InitialDataSet current : this.initalDataSet) {
            // insert entity manager
            if (current instanceof EntityInitialDataSet) {
                current.cleanup(em);
            }
        }
    }

    private void truncateCsvTables(EntityManager em) {
        Set<String> usedTables = new HashSet<String>();
        for (InitialDataSet current : this.initalDataSet) {
            // insert entity manager
            if (current instanceof CSVInitialDataSet) {
                usedTables.addAll(((CSVInitialDataSet)current).getUsedTables());
            }
        }
        if (!usedTables.isEmpty()) {
            BasicDataSource ds = new BasicDataSource(Ejb3UnitCfg.
                    getConfiguration());
            Connection con = null;
            Statement stmt = null;
            try {
                con = ds.getConnection();
                con.setAutoCommit(true);
                stmt = con.createStatement();

                if (Ejb3UnitCfg.getConfiguration().isInMemory()) {
                    // disable referential integrity for csv loads in H2
                    // one have to do this manually in external RDBMS
                    SQLUtils.disableReferentialIntegrity(con);
                }

                for (String table : usedTables) {
                    String sql = "DELETE FROM " + table;
                    log.debug(sql);
                    stmt.execute(sql);
                }

                if (Ejb3UnitCfg.getConfiguration().isInMemory()) {
                    // enable referential integrity for csv loads in H2
                    // one have to do this manually in external RDBMS
                    SQLUtils.enableReferentialIntegrity(con);
                }
            } catch (SQLException e) {
                String errMsg = "Can't delete CSVInitialDataSet tables!";
                log.error(errMsg, e);
                throw new RuntimeException(errMsg, e);
            } finally {
                SQLUtils.cleanup(con, stmt);
            }
        }
    }

    /**
     * @author Daniel Wiese
     * @since 16.10.2005
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    public void tearDown() throws Exception {
        log.debug("BaseFixture tearDown");

        // If there are Initaldatasets there have to be cleared up
        if (this.initalDataSet != null && getEntityManagerProv() !=
                null) {

            EntityManager em = getEntityManagerProv().get();
            // Is this necessary?
            em.clear();
            // be violated ??

            // In case Initialdatasets are persited
            if (this.initalDataSet != null) {
                removeEntitySet(em);
                truncateCsvTables(em);
            }
        }

        super.tearDown();
    }

    public boolean initFailed() {
        return injector == null;
    }

    /**
     * Fires an Exception if not Initialised.
     */
    private void fireExceptionIfNotInitialized() {
        if (initFailed()) {
            if (initializationError == null) {
                fail("Initialization failed.");
            } else {
                throw initializationError;
            }
        }
    }

    void initInjector(final List<Class<?>> entitiesToTest) {
        try {
            injector = InternalInjector.createInternalInjector(
                    entitiesToTest);
        } catch (EntityInitializationException e) {
            initializationError = e;
        }
    }

    EntityInitializationException getInitializationError() {
        return initializationError;
    }

    public Ejb3UnitJndiBinder getJndiBinder() {
        return jndiBinder;
    }

    Provider<EntityManager> getEntityManagerProv() {
        return entityManagerProv;
    }

    Injector getInjector() {
        return injector;
    }
}
