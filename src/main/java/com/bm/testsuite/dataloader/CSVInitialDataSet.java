package com.bm.testsuite.dataloader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;

import org.apache.commons.lang.BooleanUtils;

import com.bm.cfg.Ejb3UnitCfg;
import com.bm.introspectors.EmbeddedClassIntrospector;
import com.bm.introspectors.EntityBeanIntrospector;
import com.bm.introspectors.PersistentPropertyInfo;
import com.bm.introspectors.Property;
import com.bm.introspectors.relations.EntityReleationInfo;
import com.bm.utils.BasicDataSource;
import com.bm.utils.Ejb3Utils;
import com.bm.utils.SQLUtils;
import com.bm.utils.csv.CSVParser;

/**
 * This class creates initial data from a comma separated file.
 * The default separator is ',' for columns and \n\r for a new line
 * If a ',' is used inside a colun use quotes "xx" to indicate the 
 * start and and of an column.  
 * 
 * @param <T>
 *            the type of the entity bean (mapping the table)
 * 
 * @author Daniel Wiese
 * @author Istvan Devai
 * @author Peter Doornbosch
 * @since 17.04.2006
 */
public class CSVInitialDataSet<T> implements InitialDataSet {

	private static final org.apache.log4j.Logger log = org.apache.log4j.Logger
			.getLogger(CSVInitialDataSet.class);

	private EntityBeanIntrospector<T> introspector;

	private String[] propertyMapping;

	private Property[] propertyInfo;

	private String insertSQLString;

	private final File file;

	private final boolean isCopressed;

	private List<DateFormats> userDefinedDateFormats = new ArrayList<DateFormats>();

	private final boolean useSchemaName;

	/**
	 * Constructor.
	 * 
	 * @param entityBeanClass -
	 *            the corresponding entity bean class
	 * @param propertyMapping -
	 *            a string array with the meaning the first column of the cvs
	 *            file belongs to the property with the name
	 *            <code>propertyMapping[0]</code>
	 * @param isCompressed -
	 *            true if compressed (zip)
	 * @param useSchemaName
	 *            the schema name will be used for sql generation
	 * @param csvFileName -
	 *            the name of the csv file
	 */
	public CSVInitialDataSet(
			Class<T> entityBeanClass,
			String csvFileName,
			boolean isCompressed,
			boolean useSchemaName,
			String... propertyMapping) {
		this.useSchemaName = useSchemaName;
		this.isCopressed = isCompressed;
		initialize(entityBeanClass, propertyMapping);
		final URL tmp = Thread.currentThread().getContextClassLoader().getResource(csvFileName);
		if (tmp == null) {
			throw new IllegalArgumentException("Can't find the CVS file named (" + csvFileName
					+ ")");
		}

		file = new File(Ejb3Utils.getDecodedFilename(tmp));

	}

	/**
	 * Constructor.
	 * 
	 * @param entityBeanClass -
	 *            the corresponding entity bean class
	 * @param propertyMapping -
	 *            a string array with the meaning the first column of the csv
	 *            file belongs to the property with the name
	 *            <code>propertyMapping[0]</code>
	 * @param isCompressed -
	 *            true if compressed (zip)
	 * @param csvFileName -
	 *            the name of the csv file
	 */
	public CSVInitialDataSet(
			Class<T> entityBeanClass,
			String csvFileName,
			boolean isCompressed,
			String... propertyMapping) {
		this(entityBeanClass, csvFileName, isCompressed, false, propertyMapping);
	}

	/**
	 * Constructor.
	 * 
	 * @param entityBeanClass -
	 *            the corresponding entity bean class
	 * @param propertyMapping -
	 *            a string array with the meaning the first column of the csv
	 *            file belongs to the property with the name
	 *            <code>propertyMapping[0]</code>
	 * @param csvFileName -
	 *            the name of the csv file
	 */
	public CSVInitialDataSet(
			Class<T> entityBeanClass,
			String csvFileName,
			String... propertyMapping) {
		this(entityBeanClass, csvFileName, false, false, propertyMapping);
	}

	/**
	 * Allows to specify a user specific date format pattern's. The are
	 * processed in the added order.
	 * 
	 * @param dateFormat
	 *            the date format pattern
	 * @return this instance for inlining.
	 */
	public CSVInitialDataSet<T> addDateFormat(DateFormats dateFormat) {
		this.userDefinedDateFormats.add(dateFormat);
		return this;
	}

	/**
	 * @author Daniel Wiese
	 * @since 29.06.2006
	 * @param entityBeanClass
	 * @param propertyMapping
	 */
	private void initialize(Class<T> entityBeanClass, String... propertyMapping) {
		this.introspector = new EntityBeanIntrospector<T>(entityBeanClass);
		this.propertyMapping = propertyMapping;
		this.propertyInfo = new Property[propertyMapping.length];
		this.insertSQLString = this.buildInsertSQL();
	}

	/**
	 * Returns the insert SQL.
	 * 
	 * @return the insert SQL
	 */
	public String buildInsertSQL() {
		StringBuilder insertSQL = new StringBuilder();
		StringBuilder questionMarks = new StringBuilder();
		insertSQL.append("INSERT INTO ").append(getTableName()).append(" (");
		int counter = -1;
		for (String stringProperty : this.propertyMapping) {
			final Property property = this.getProperty(stringProperty);
			// persistent field info
			final PersistentPropertyInfo info = this.getPersistentFieldInfo(stringProperty);

			insertSQL.append((info.getDbName().length() == 0) ? property.getName() : info
					.getDbName());
			questionMarks.append("?");
			counter++;
			// store the property
			this.propertyInfo[counter] = property;
			if (counter + 1 < this.propertyMapping.length) {
				insertSQL.append(", ");
				questionMarks.append(", ");
			}
		}
		// If entity uses single table inheritance, insert a discriminator value
		// (which must be present)
		if (this.introspector.usesSingleTableInheritance()) {
			insertSQL.append(", ").append(this.introspector.getDiscriminatorName()).append(") ")
					.append("VALUES (").append(questionMarks.toString()).append(", ");
			Class<?> discriminatorType = this.introspector.getDiscriminatorType();
			if (discriminatorType.equals(Integer.class)) {
				insertSQL.append(this.introspector.getDiscriminatorValue());
			} else {
				insertSQL.append("'").append(this.introspector.getDiscriminatorValue()).append("'");
			}
			insertSQL.append(")");
		} else {
			// Normal case: just insert the values.
			insertSQL.append(") ").append("VALUES (").append(questionMarks.toString()).append(")");
		}

		return insertSQL.toString();
	}

	private String getTableName() {
		if (useSchemaName && this.introspector.hasSchema()) {
			return this.introspector.getShemaName() + "." + this.introspector.getTableName();
		}
		return this.introspector.getTableName();
	}

	private String getClassName() {
		return this.introspector.getPersistentClassName();
	}

	@SuppressWarnings("unchecked")
	private Property getProperty(String property) {
		Property info = null;
		if (this.introspector.hasEmbeddedPKClass()) {
			final EmbeddedClassIntrospector pkintro = this.introspector.getEmbeddedPKClass();
			final List<Property> pkFields = pkintro.getPersitentProperties();

			for (Property current : pkFields) {
				if (current.getName().equals(property)) {
					info = current;
					break;
				}
			}
		}
		if (info == null) {
			for (Property current : this.introspector.getPersitentProperties()) {
				if (current.getName().equals(property)) {
					info = current;
					break;
				}
			}

			if (info == null) {
				throw new IllegalArgumentException("The property (" + property
						+ ") is not a persistent field");
			}
		}

		return info;
	}

	@SuppressWarnings("unchecked")
	private PersistentPropertyInfo getPersistentFieldInfo(String property) {
		PersistentPropertyInfo info = null;
		if (this.introspector.hasEmbeddedPKClass()) {
			final EmbeddedClassIntrospector pkintro = this.introspector.getEmbeddedPKClass();
			final List<Property> pkFields = pkintro.getPersitentProperties();

			for (Property current : pkFields) {
				if (current.getName().equals(property)) {
					info = pkintro.getPresistentFieldInfo(current);
					break;
				}
			}
		}
		if (info == null) {
			for (Property current : this.introspector.getPersitentProperties()) {
				if (current.getName().equals(property)) {
					info = this.introspector.getPresistentFieldInfo(current);
					break;
				}
			}

			if (info == null) {
				throw new IllegalArgumentException("The property (" + property
						+ ") is not a persistent field");
			}
		}

		return info;
	}

	/**
	 * Creates the data.
	 * 
	 * @author Daniel Wiese
	 * @since 17.04.2006
	 * @see com.bm.testsuite.dataloader.InitialDataSet#create()
	 */
	public void create() {
		BasicDataSource ds = new BasicDataSource(Ejb3UnitCfg.getConfiguration());
		Connection con = null;
		PreparedStatement prep = null;
		try {
			con = ds.getConnection();
			prep = con.prepareStatement(this.insertSQLString);
			final CSVParser parser = new CSVParser(getCSVInputStream());
			parser.setCommentStart("#;!");
			parser.setEscapes("nrtf", "\n\r\t\f");
			String value;
			int count = 0;
			int lastLineNumber = parser.lastLineNumber();
			while ((value = parser.nextValue()) != null) {
				if (parser.lastLineNumber() != lastLineNumber) {
					// we have a new line
					lastLineNumber = parser.lastLineNumber();
					count = 0;
				}

				// insert only if neccessary (ignore not requiered fields)
				if (count < this.propertyInfo.length) {
					this.setPreparedStatement(count + 1, prep, this.propertyInfo[count], value);
					count++;
				}

				// execute sql
				if (count == this.propertyInfo.length) {
					System.out.println(prep);
					prep.execute();
				}

			}

			parser.close();
		} catch (FileNotFoundException e) {
			log.error("Data-Loader failing ", e);
			new RuntimeException(e);
		} catch (IOException e) {
			log.error("Data-Loader failing ", e);
			new RuntimeException(e);
		} catch (SQLException e) {
			log.error("Data-Loader failing ", e);
			new RuntimeException(e);
		} finally {
			SQLUtils.cleanup(con, prep);
		}
	}

	private InputStream getCSVInputStream() {
		InputStream toReturn = null;
		if (this.isCopressed) {
			try {
				toReturn = Ejb3Utils.unjar(new FileInputStream(this.file));
				if (toReturn == null) {
					throw new IllegalArgumentException("The copressed file " + this.file.getName()
							+ " was empty");
				}
			} catch (IOException e) {
				log.error("The file " + this.file.getName() + " could not be accessed", e);
				throw new IllegalArgumentException("The file " + this.file.getAbsolutePath()
						+ " could not be accessed", e);
			}

		} else {
			try {
				toReturn = new FileInputStream(this.file);
			} catch (FileNotFoundException e) {
				throw new IllegalArgumentException("The copressed file " + this.file.getName()
						+ "not found");
			}
		}

		return toReturn;
	}

	/**
	 * Deletes the data.
	 * 
	 * @param em -
	 *            the entyty manager.
	 * @author Daniel Wiese
	 * @since 17.04.2006
	 * @see com.bm.testsuite.dataloader.InitialDataSet#cleanup(EntityManager)
	 */
	public void cleanup(EntityManager em) {
		StringBuilder deleteSQL = new StringBuilder();
		deleteSQL.append("DELETE FROM ").append(getClassName());
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		Query query = em.createQuery(deleteSQL.toString());
		query.executeUpdate();
		tx.commit();

	}

	/**
	 * Sets the value (using the right type) in the prepared statement.
	 * 
	 * @param index -
	 *            the index inside the prepared statement
	 * @param statement -
	 *            the prepared statement itself
	 * @param prop -
	 *            the property representing the type
	 * @param value -
	 *            the value to be set
	 * @throws SQLException -
	 *             in error case
	 */
	private void setPreparedStatement(int index,
			PreparedStatement statement,
			Property prop,
			String value) throws SQLException {

		// First, check whether this property denotes a relation
		PersistentPropertyInfo persistentFieldInfo = getPersistentFieldInfo(prop.getPropertyName());
		if (persistentFieldInfo.isReleation()) {
			EntityReleationInfo relationInfo = persistentFieldInfo.getEntityReleationInfo();
			// Must determine the type of the primary key of the class that is
			// referenced by the relation.
			Set<Property> targetKeyProps = relationInfo.getTargetKeyProperty();
			if (targetKeyProps == null) {
				// This can happen with relation types that we do not yet
				// support
				throw new IllegalArgumentException(
						"Can't determine key type of relation target - relation type not yet supported?");
			}
			if (targetKeyProps.size() > 1) {
				throw new IllegalArgumentException("Composite foreign keys are not yet supported.");
			}
			for (Property keyProp : targetKeyProps) { // Because of the check
														// above, this will loop
														// at most once
				Class<?> foreignKeyType = Ejb3Utils.getNonPrimitiveType(keyProp.getType());
				setPreparedStatement(index, statement, foreignKeyType, value);
			}
		} else {
			// convert to non-primitive if primitive
			Class<?> type = Ejb3Utils.getNonPrimitiveType(prop.getType());
			setPreparedStatement(index, statement, type, value);
		}
	}

	/**
	 * Sets the value (using the right type) in the prepared statement. Supports
	 * only simple types
	 * 
	 * @param index
	 *            the index of the value in the prepared statement
	 * @param statement
	 *            the prepared statement in which values are set
	 * @param type
	 *            the type of the property
	 * @param value
	 *            the value to set
	 * @throws SQLException
	 */
	private void setPreparedStatement(int index,
			PreparedStatement statement,
			Class<?> type,
			String value) throws SQLException {
		if (type.equals(String.class)) {
			statement.setString(index, value);
		} else if (type.equals(Integer.class)) {
			if (value == null || value.equals("") || value.equals("null")) {
				statement.setNull(index, Types.INTEGER);
			} else {
				statement.setInt(index, ((value.equals("")) ? 0 : Integer.valueOf(value)));
			}
		} else if (type.equals(Long.class)) {
			if (value == null || value.equals("") || value.equals("null")) {
				statement.setNull(index, Types.BIGINT);
			} else {
				statement.setLong(index, ((value.equals("")) ? 0 : Long.valueOf(value)));
			}
		} else if (type.equals(Boolean.class)) {
			final boolean result = BooleanUtils.toBoolean(value);
			statement.setBoolean(index, result);
		} else if (type.equals(Short.class)) {
			statement.setShort(index, ((value.equals("")) ? 0 : Short.valueOf(value)));
		} else if (type.equals(Byte.class)) {
			statement.setByte(index, Byte.valueOf(value));
		} else if (type.equals(Character.class)) {
			statement.setString(index, String.valueOf(value));
		} else if (type.equals(Date.class)) {
			parseAndSetDate(index, value, statement);
		} else if (type.equals(Double.class)) {
			statement.setDouble(index, ((value.equals("")) ? 0 : Double.valueOf(value)));
		} else if (type.equals(Float.class)) {
			statement.setFloat(index, ((value.equals("")) ? 0 : Float.valueOf(value)));
		} else if (type.isEnum()) {
			// TODO: possible to have the ordinal value of an
			// enum in a .csv file maybe it would be reasonable to extend this
			// so that it is also possible to have enums by literal name
			statement.setInt(index, Integer.valueOf(value));
		}
	}

	private void parseAndSetDate(int index, String value, PreparedStatement statement)
			throws SQLException {
		if (value.equals("") || value.equalsIgnoreCase("null")) {
			statement.setNull(index, java.sql.Types.DATE);
		} else {
			// try to guess the default format
			boolean success = false;
			List<DateFormats> allValidFormats = getValidFormats();
			for (DateFormats current : allValidFormats) {
				try {
					current.parseToPreparedStatemnt(value, statement, index);
					success = true;
					break;
				} catch (ParseException ex) {
					log.debug("Date parser (" + current + ") was not working, trying next");
				}
			}

			if (!success) {
				// java 1.5 will convert this to string builder internally
				String msg = "Illegal date format (" + value + ")";
				msg += " expecting one of: ";
				for (DateFormats current : DateFormats.values()) {
					msg += current.toPattern() + ", ";
				}

				throw new IllegalArgumentException(msg);
			}
		}
	}

	private List<DateFormats> getValidFormats() {
		final List<DateFormats> dtFormats = new ArrayList<DateFormats>();
		if (!this.userDefinedDateFormats.isEmpty()) {
			dtFormats.addAll(this.userDefinedDateFormats);
		}

		dtFormats.addAll(Arrays.asList(DateFormats.systemValues()));
		return dtFormats;
	}

}
