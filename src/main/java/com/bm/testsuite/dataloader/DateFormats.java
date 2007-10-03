package com.bm.testsuite.dataloader;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Represents date formats for CVS initial data set.
 * @author Daniel Wiese
 * @author Peter Doornbosch
 *
 */
public enum DateFormats {

	DEFAULT_DATE_TIME(false, DateFormat.getDateTimeInstance(), SQLTypes.SQL_TIMESTAMP),

	DEFAULT_DATE(false, DateFormat.getDateInstance(), SQLTypes.SQL_DATE),

	DEFAULT_TIME(false, DateFormat.getDateTimeInstance(), SQLTypes.SQL_TIME),

	USER_DATE_TIME(true, null, SQLTypes.SQL_TIMESTAMP),

	USER_DATE(true, null, SQLTypes.SQL_DATE),

	USER_TIME(true, null, SQLTypes.SQL_TIME);

	private DateFormat dateFormatter;
	private final SQLTypes sqlType;
	private boolean userDefined;

	private DateFormats(boolean userDefined, DateFormat dateFormatter, SQLTypes sqlType) {
		this.userDefined = userDefined;
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
		if (this.userDefined) {
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
			break;
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

	public static DateFormats[] systemValues() {
		return new DateFormats[] { DEFAULT_DATE, DEFAULT_DATE_TIME, DEFAULT_TIME };
	}
}
