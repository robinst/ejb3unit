package com.bm.cfg;

import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.SQLServerDialect;

/**
 * Containts different db dialects.
 * @author Daniel Wiese
 *
 */
public enum DBDialect {

	MS_SQLSERVER(new SQLServerDialect());

	private final Dialect dialect;

	private DBDialect(Dialect dialect) {
		this.dialect = dialect;
	}

	/**
	 * Getter to return the dialect.
	 * 
	 * @return the dialect
	 */
	public Dialect getDialect() {
		return dialect;
	}

}
