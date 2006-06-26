package com.bm.utils;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.sql.DataSource;

import com.bm.cfg.Ejb3UnitCfg;

/**
 * <p>
 * Basic implementation of <code>javax.sql.DataSource</code> that is
 * configured via Ejb3Prop properties.
 * 
 * @author Daniel Wiese
 */
public class BasicDataSource implements DataSource {

	private final Ejb3UnitCfg config;

	private PrintWriter logger = null;

	private int loginTimeout = 0;

	/**
	 * Constructor.
	 * 
	 * @param config -
	 *            die konfiguration.
	 */
	public BasicDataSource(Ejb3UnitCfg config) {
		this.config = config;
	}

	/**
	 * The connection.
	 * @author Daniel Wiese
	 * @since 08.11.2005
     * @return connection
	 * @see javax.sql.DataSource#getConnection()
	 */
	public Connection getConnection() throws SQLException {
		if (Ejb3UnitCfg.getConfiguration().isInMemory()) {
			return this.getConnection("sa", "");
		} else {
			final String user = config
					.getValue(Ejb3UnitCfg.KEY_CONNECTION_USERNAME);
			final String pw = config
					.getValue(Ejb3UnitCfg.KEY_CONNECTION_PASSWORD);
			return this.getConnection(user, pw);
		}
	}

	/**
	 * The connection.
	 * 
	 * @author Daniel Wiese
	 * @since 08.11.2005
	 * @see javax.sql.DataSource#getConnection(java.lang.String,
	 *      java.lang.String)
	 */
	public Connection getConnection(String username, String password)
			throws SQLException {
		try {
			if (Ejb3UnitCfg.getConfiguration().isInMemory()) {
				Class.forName("org.hsqldb.jdbcDriver");
				final Connection cn = DriverManager.getConnection("jdbc:hsqldb:mem:ejb3unit", username,
						password);
				return cn;
			} else {
				Class.forName(config
						.getValue(Ejb3UnitCfg.KEY_CONNECTION_DRIVER_CLASS));
				final Connection cn = DriverManager.getConnection(config
						.getValue(Ejb3UnitCfg.KEY_CONNECTION_URL), username,
						password);
				return cn;
			}
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * The log writer.
	 * 
	 * @author Daniel Wiese
	 * @since 08.11.2005
	 * @see javax.sql.DataSource#getLogWriter()
	 */
	public PrintWriter getLogWriter() throws SQLException {
		return this.logger;
	}

	/**
	 * The log writer.
	 * 
	 * @author Daniel Wiese
	 * @since 08.11.2005
	 * @see javax.sql.DataSource#setLogWriter(java.io.PrintWriter)
	 */
	public void setLogWriter(PrintWriter logger) throws SQLException {
		this.logger = logger;

	}

	/**
	 * The timeout.
	 * 
	 * @author Daniel Wiese
	 * @since 08.11.2005
	 * @see javax.sql.DataSource#setLoginTimeout(int)
	 */
	public void setLoginTimeout(int arg0) throws SQLException {
		this.loginTimeout = arg0;

	}

	/**
	 * The timeout.
	 * 
	 * @author Daniel Wiese
	 * @since 08.11.2005
	 * @see javax.sql.DataSource#getLoginTimeout()
	 */
	public int getLoginTimeout() throws SQLException {
		return this.loginTimeout;
	}

	/**
	 * If the datasource is in memory this will shutdown the database.
	 */
	public void shutdownInMemoryDatabase() throws Exception {
		Connection con = null;
		PreparedStatement ps = null;
		try {
			con = this.getConnection();
			ps = con.prepareStatement("SHUTDOWN");
			ps.execute();
		} finally {
			SQLUtils.cleanup(con, ps);
		}
	}

}
