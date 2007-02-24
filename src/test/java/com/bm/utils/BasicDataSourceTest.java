package com.bm.utils;

import java.sql.Connection;
import java.sql.DatabaseMetaData;

import org.apache.log4j.Logger;

import com.bm.cfg.Ejb3UnitCfg;

import junit.framework.TestCase;

/**
 * Test.
 * 
 * @author Daniel Wiese
 * @since 08.11.2005
 */
public class BasicDataSourceTest extends TestCase {

	private static final Logger log = Logger
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
		log.debug(mt.getDatabaseProductVersion());
		log.debug(mt.getDatabaseProductName());
		con.close();

	}

}
