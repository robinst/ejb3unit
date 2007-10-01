package com.bm.testsuite.dataloader;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public enum DateFormats {

	DEFAULT_DATE_TIME(DateFormat.getDateTimeInstance(), SQLTypes.SQL_TIMESTAMP),

	DEFAULT_DATE(DateFormat.getDateInstance(), SQLTypes.SQL_DATE),

	DEFAULT_TIME(DateFormat.getDateTimeInstance(), SQLTypes.SQL_TIME),

	USER_DATE_TIME(null, SQLTypes.SQL_TIMESTAMP),

	USER_DATE(null, SQLTypes.SQL_DATE),

	USER_TIME(null, SQLTypes.SQL_TIME);

	private DateFormat dateFormatter;
	private final SQLTypes sqlType;

	private DateFormats(DateFormat dateFormatter, SQLTypes sqlType) {
		this.dateFormatter = dateFormatter;
		this.sqlType = sqlType;

	}

	/**
	 * Allows to set a user defined formatter.
	 * 
	 * @param dateFormatter -
	 *            the date formatter
	 * @return this intance for inlining
	 */
	public DateFormats setUserDefinedFomatter(String pattern) {
		if (this.dateFormatter == null) {
			this.dateFormatter = new SimpleDateFormat(pattern);
		} else {
			throw new IllegalArgumentException("Only allowed for user defined values");
		}

		return this;
	}

	/**
	 * Parse the specified instance
	 * 
	 * @param toParse
	 * @return
	 * @throws ParseException
	 */
	public Date parse(String toParse) throws ParseException {
		if (dateFormatter == null) {
			throw new IllegalArgumentException("Please define your format pattern");
		}
		return this.dateFormatter.parse(toParse);
	}

	public void parseToPreparedStatemnt(String toParse, PreparedStatement ps, int pos)
			throws ParseException, SQLException {
		Date dateValue = parse(toParse);
		switch (sqlType) {
		case SQL_TIMESTAMP:
			ps.setTimestamp(pos, new java.sql.Timestamp(dateValue.getTime()));
			break;
		case SQL_DATE:
			ps.setDate(pos, new java.sql.Date(dateValue.getTime()));
			break;
		case SQL_TIME:
			ps.setTime(pos, new java.sql.Time(dateValue.getTime()));
		default:
			throw new IllegalArgumentException(
					"No handling specified foer this SQL type (" + sqlType + ")");

		}
	}

	/**
	 * Returns the pattern for formatting.
	 * 
	 * @return th epattern
	 */
	public String toPattern() {
		if (this.dateFormatter instanceof SimpleDateFormat) {
			return ((SimpleDateFormat) this.dateFormatter).toPattern();
		} else {
			return "Unknown pattern";
		}
	}

}
