package com.bm.utils;

import java.sql.Connection;
import java.sql.DatabaseMetaData;



import com.bm.cfg.Ejb3UnitCfg;

import junit.framework.TestCase;

/**
 * Test.
 * 
 * @author Daniel Wiese
 * @since 08.11.2005
 */
public class BasicDataSourceTest extends TestCase {

	private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory
			.getLogger(BasicDataSourceTest.class);

	/**
	 * Test method for 'com.bm.utils.BasicDataSource.getConnection()'.
	 * 
	 * @throws Exception -
	 *             error
	 */
	public void testGetConnection() throws Exception {
		final BasicDataSource ds = new BasicDataSource(Ejb3UnitCfg
				.getConfiguration());
		Connection con = ds.getConnection();
		assertNotNull(con);
		DatabaseMetaData mt = con.getMetaData();
		assertNotNull(mt);
		logger.debug(mt.getDatabaseProductVersion());
		logger.debug(mt.getDatabaseProductName());
		con.close();

	}

}
