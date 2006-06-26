package com.bm.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Untility class for SQL operations.
 * 
 * @author Daniel Wiese
 * @author <a href="mailto:daniel.wiese@siemens.com">Daniel Wiese</a>
 * 
 */
public final class SQLUtils {

    /**
     * 
     * Constructor.
     */
    private SQLUtils() {
        // intentionally left blank
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
    public static void cleanup(Connection con, PreparedStatement prst, ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (Throwable e) {
            }
        }
        if (prst != null) {
            try {
                prst.close();
            } catch (Throwable e) {
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
    public static void cleanup(Connection con, PreparedStatement prst) {
        cleanup(con, prst, null);
    }

}
