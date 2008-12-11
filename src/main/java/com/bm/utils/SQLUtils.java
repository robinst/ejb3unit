package com.bm.utils;

import com.bm.cfg.Ejb3UnitCfg;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Untility class for SQL operations.
 * 
 * @author Daniel Wiese
 * @author <a href="mailto:daniel.wiese@siemens.com">Daniel Wiese</a>
 * 
 */
public final class SQLUtils {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.
            getLogger(SQLUtils.class);

    /**
     *
     * Constructor.
     */
    private SQLUtils() {
        // intentionally left blank
    }

    public static void disableReferentialIntegrity(Ejb3UnitCfg cfg) {

        BasicDataSource ds = new BasicDataSource(cfg);
        Connection con = null;
        try {
            con = ds.getConnection();
            if (cfg.isInMemory()) {
                // disable referential integrity for csv loads in H2
                // one have to do this manually in external RDBMS
                SQLUtils.disableReferentialIntegrity(con);
            }
        } catch (SQLException e) {
            throw new RuntimeException(
                    "Can't get disable referential integrity: ", e);
        } finally {
            SQLUtils.cleanup(con);
        }
    }

    public static void enableReferentialIntegrity(Ejb3UnitCfg cfg) {

        BasicDataSource ds = new BasicDataSource(cfg);
        Connection con = null;
        try {
            con = ds.getConnection();
            if (cfg.isInMemory()) {
                // disable referential integrity for csv loads in H2
                // one have to do this manually in external RDBMS
                SQLUtils.enableReferentialIntegrity(con);
            }
        } catch (SQLException e) {
            throw new RuntimeException(
                    "Can't get disable referential integrity: ", e);
        } finally {
            SQLUtils.cleanup(con);
        }
    }

    /**
     * Disable referential integrity for inserts in H2 database.
     * @param con SQL Connection
     * @throws SQLException
     */
    public static void disableReferentialIntegrity(Connection con)
            throws SQLException {
        PreparedStatement prs = null;
        try {
            log.debug(
                    "Trying to disable referential integrity for inserts.");
            final String disableRICommand = "SET REFERENTIAL_INTEGRITY FALSE";
            prs = con.prepareStatement(disableRICommand);
            prs.execute();
            log.debug("OK. RI disabled.");
        } finally {
            SQLUtils.cleanup(prs);
        }
    }

    /**
     * Enable referential integrity for inserts in H2 database.
     * @param con SQL Connection
     * @throws SQLException 
     */
    public static void enableReferentialIntegrity(Connection con)
            throws SQLException {
        PreparedStatement prs = null;
        try {
            log.debug(
                    "Trying to enable referential integrity for inserts.");
            final String enableRICommand = "SET REFERENTIAL_INTEGRITY TRUE";
            prs = con.prepareStatement(enableRICommand);
            prs.execute();
            log.debug("OK. RI enabled.");
        } finally {
            SQLUtils.cleanup(prs);
        }
    }

    /**
     * Close a prepared statement.
     *
     * @param prst -
     *            prepared statement
     */
    public static void cleanup(PreparedStatement prst) {
        cleanup(new PreparedStatement[]{prst});
    }

    /**
     * Close prepared statements.
     *
     * @param prst -
     *            prepared statements
     */
    public static void cleanup(PreparedStatement[] prst) {
        if (prst != null) {
            for (int i = 0; i < prst.length; i++) {
                try {
                    prst[i].close();
                } catch (Throwable e) {
                }
            }
        }
    }

    /**
     * Close the resources.
     *
     * @param con
     *            -connection
     * @param prst -
     *            prepared statement
     * @param rs -
     *            result set
     */
    public static void cleanup(Connection con,
            PreparedStatement[] prst,
            ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (Throwable e) {
            }
        }
        if (prst != null) {
            for (int i = 0; i < prst.length; i++) {
                try {
                    prst[i].close();
                } catch (Throwable e) {
                }
            }
        }
        if (con != null) {
            try {
                con.close();
            } catch (Throwable e) {
            }
        }
    }

    /**
     * Close the resources.
     *
     * @param con
     *            -connection
     */
    public static void cleanup(Connection con) {
        cleanup(con, null, null);
    }

    /**
     * Close the resources.
     *
     * @param con
     *            -connection
     * @param prst -
     *            prepared statement
     */
    public static void cleanup(Connection con,
            PreparedStatement[] prst) {
        cleanup(con, prst, null);
    }

    /**
     * Close the resources.
     *
     * @param con
     *            -connection
     * @param prst -
     *            prepared statement
     */
    public static void cleanup(Connection con, PreparedStatement prst) {
        cleanup(con, new PreparedStatement[]{prst}, null);
    }
}
