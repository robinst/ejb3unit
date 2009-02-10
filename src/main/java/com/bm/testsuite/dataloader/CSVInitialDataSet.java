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
import javax.persistence.Enumerated;
import javax.persistence.EnumType;

import javax.persistence.Query;
import org.apache.commons.lang.BooleanUtils;

import com.bm.cfg.Ejb3UnitCfg;
import com.bm.introspectors.DbMappingInfo;
import com.bm.introspectors.EmbeddedClassIntrospector;
import com.bm.introspectors.EntityBeanIntrospector;
import com.bm.introspectors.PersistentPropertyInfo;
import com.bm.introspectors.Property;
import com.bm.introspectors.relations.EntityReleationInfo;
import com.bm.utils.BasicDataSource;
import com.bm.utils.Ejb3Utils;
import com.bm.utils.SQLUtils;
import com.bm.utils.csv.CSVParser;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory
			.getLogger(CSVInitialDataSet.class);

        private Class<T> entityBeanClass;

	private EntityBeanIntrospector<T> introspector;

	private String[] propertyMapping;

	private String[] insertSQLStrings;

        private Set<String> usedTables;
        
        private List[] csvMapping;
        
        private final String csvFileName;
        
	private final File file;

	private final boolean isCopressed;

	private final List<DateFormats> userDefinedDateFormats = new ArrayList<DateFormats>();

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
	public CSVInitialDataSet(Class<T> entityBeanClass, String csvFileName,
			boolean isCompressed, boolean useSchemaName,
			String... propertyMapping) {
                this.entityBeanClass = entityBeanClass;
		this.useSchemaName = useSchemaName;
		this.isCopressed = isCompressed;
		this.csvFileName = csvFileName;
                initialize(entityBeanClass, propertyMapping);
                file = resolveFile(csvFileName);
	}

        public Class<T> getEntityBeanClass() {
            return entityBeanClass;
        }

        public String[] getPropertyMapping() {
            return propertyMapping;
        }
        
        public String getCSVFileName() {
            return csvFileName;
        }

    private File resolveFile(String fileName) {
        final URL tmp = Thread.currentThread().getContextClassLoader()
				.getResource(fileName);
		if (tmp == null) {
			throw new IllegalArgumentException(
					"Can't find the file named '" + fileName
                                        + "' (must be on classpath)");
		}

		return new File(Ejb3Utils.getDecodedFilename(tmp));
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
	public CSVInitialDataSet(Class<T> entityBeanClass, String csvFileName,
			boolean isCompressed, String... propertyMapping) {
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
	public CSVInitialDataSet(Class<T> entityBeanClass, String csvFileName,
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
		this.introspector = EntityBeanIntrospector.getEntityBeanIntrospector(entityBeanClass);
		this.propertyMapping = propertyMapping;
		this.buildInsertSQL();
	}

        
        private String getDeclaringTable(Class clazz) {
            EntityBeanIntrospector ebi = null;
            try {
                ebi = EntityBeanIntrospector.getEntityBeanIntrospector(clazz);
            } catch (RuntimeException e) {
                // if the class is not an Entity (i.e. MappedSuperClass)
                ebi = EntityBeanIntrospector.getEntityBeanIntrospector(this.introspector.getBaseClass());
            }

            return ebi.getFullTableName(useSchemaName);
        }
        
        private String getDbColumnName(final Property property) {
            final PersistentPropertyInfo info = this.
                    getPersistentFieldInfo(property.getName());
            if (log.isTraceEnabled()) {
                log.trace("processing field: " + property.getName() +
                        ", property: " + property.getDeclaringClass().
                        getSimpleName() + "." + property.getName() + "[" +
                        property.getType() + "]" + ", dbTable: " + getDeclaringTable(
                        property.getDeclaringClass()));
            }

            String dbName = (info.getDbName().length() == 0)
                    ? property.getName()
                    : info.getDbName();
            return dbName;
        }
        
        /** 
         * FOR TESTING ONLY.
         * 
         * @param position
         * @return SQL insert statement
         */
        List<PropertyPosition> getCsvMapping(int position) {
                return this.csvMapping[position];
        }

        private SortedMap<TableInfo, Builder> initInsertSqlMap() {
                @SuppressWarnings("unchecked")
                SortedMap<TableInfo, Builder> insertSqlMap = new TreeMap<TableInfo, Builder>();
                Class<?> toInspect = this.introspector.getPersistentClass();
                usedTables = new HashSet<String>();

                while (toInspect != null) {
                    try {
                        final String tableName = getDeclaringTable(toInspect);
                        final TableInfo ti = new TableInfo (tableName, toInspect);
                        Builder b = insertSqlMap.get(ti);
                        if (b == null) {
                            b = new Builder(tableName);
                            insertSqlMap.put(ti, b);
                            usedTables.add(tableName);
                        }
                    } catch (RuntimeException e) {
                        // Stop at first non-entity parent
                        break;
                    }
                    toInspect = toInspect.getSuperclass();
                }

                return insertSqlMap;
        }

	/**
	 * Builds the SQL insert statement definitions
	 * 
	 * @return the list of sql statements in order of execution
	 */
	public String[] buildInsertSQL() {

        SortedMap<TableInfo, Builder> insertSqlMap = initInsertSqlMap();
		for (String stringProperty : this.propertyMapping) {
                        CompoundPropertyName cn = new CompoundPropertyName(
                                stringProperty);
			final Property property = this.getProperty(cn.getName());
                        final String tableName = getDeclaringTable(property.getDeclaringClass());
                        final TableInfo ti = new TableInfo (tableName, property.getDeclaringClass());

                        // append to the table insert
                        Builder b = insertSqlMap.get(ti);
                        assert b != null;
                        // store the property
                        if (cn.getColumn() == null) {
                            cn.setColumn(getDbColumnName(property));
                        }
                        b.append(stringProperty, property, cn.getColumn());
		}
                
		// If entity uses single table inheritance, insert a discriminator value
		// (which must be present)
		if (this.introspector.usesSingleTableInheritance()) {
                        final Class<?> persistentClass = this.introspector.getPersistentClass();
                        final TableInfo ti = new TableInfo (getDeclaringTable(persistentClass), persistentClass);
                        Builder b = insertSqlMap.get(ti);
                        assert b != null;
                        Class<?> discriminatorType = this.introspector
					.getDiscriminatorType();
			if (discriminatorType.equals(Integer.class)) {
				b.appendDirectValue(this.introspector.getDiscriminatorName(),
                                        this.introspector.getDiscriminatorValue());
			} else {
                b.appendStringValue(this.introspector.getDiscriminatorName(),
                        this.introspector.getDiscriminatorValue());
			}
        }
                
        // find primary keys' positions
        Set<Property> pkFields = EntityBeanIntrospector.
                getEntityBeanIntrospector(this.introspector.
                getPersistentClass()).getPkFields();
        Map<String, Integer> pkPositions = new HashMap<String, Integer>();
        for (Property pk : pkFields) {
            int j = 0;
            for (String stringProperty : this.propertyMapping) {
                if (pk.getName().equals(stringProperty)) {
                    pkPositions.put(stringProperty, j);
                    break;
                }
                j++;
            }
            if (this.propertyMapping.length <= j &&
                    this.introspector.usesJoinedInheritance()) {
                throw new RuntimeException("Unsupported data format! "
                        + "Primary key ("
                        + pk.getName() + ") is not present in the data row definition.");
            }
        }

        // add primary keys of the base table to joined tables
        int i = 0;
        for (Builder b : insertSqlMap.values()) {
            if (i++ > 0) {
                for (Property pk : pkFields) {
                    b.append(pk.getName(), pk, getDbColumnName(pk));
                }
            }
        }

        // finalize insert and delete strings
        int m = 0;
        int n = insertSqlMap.values().size();
        this.insertSQLStrings = new String[n];
        for (Builder b : insertSqlMap.values()) {
            this.insertSQLStrings[m++] = b.toInsertString();
        }

        // shovel to a simpler structure
        i = 0;
        this.csvMapping = new List[this.propertyMapping.length];
        for (String stringProperty : this.propertyMapping) {
            int j = 0;
            csvMapping[i] = new ArrayList<PropertyPosition>();
            for (Builder b : insertSqlMap.values()) {
                CompoundPropertyName cn = new CompoundPropertyName(stringProperty);
                PropertyPosition p = b.getPosition(cn);
                if (p != null) {
                    p.setBuilderPosition(j);
                    csvMapping[i].add(p);
                    if (log.isTraceEnabled()) {
                        log.trace("csvMapping[" + i + "]="
                                + "name: " + p.getProperty().getName()
                                + ", column: " + p.getColumn()
                                + ", builder: " + p.builderPosition
                                + ", localPosition=" + p.getLocalPosition());
                    }
                }
                j++;
            }
            i++;
        }

        return insertSQLStrings;
	}

	private String getClassName() {
		return this.introspector.getPersistentClassName();
	}

	@SuppressWarnings("unchecked")
	private Property getProperty(String property) {
		Property info = null;
		if (this.introspector.hasEmbeddedPKClass()) {
			final EmbeddedClassIntrospector pkintro = this.introspector
					.getEmbeddedPKClass();
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
			final EmbeddedClassIntrospector pkintro = this.introspector
					.getEmbeddedPKClass();
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

    public Set<String> getUsedTables() {
        return usedTables;
    }

    public void create() {
        BasicDataSource ds = new BasicDataSource(Ejb3UnitCfg.getConfiguration());
        Connection con = null;
		try {
			con = ds.getConnection();
            con.setAutoCommit(false);
            create(con);
            con.commit();
		} catch (SQLException e) {
            try {
                con.rollback();
            } catch (Exception ex) { }
			throw new RuntimeException("Can't get database connection: ", e);
        } finally {
            SQLUtils.cleanup(con);
        }
    }


	/**
	 * Creates the data.
	 * 
	 * @author Daniel Wiese
	 * @since 17.04.2006
	 * @see com.bm.testsuite.dataloader.InitialDataSet#create()
	 */
	public void create(Connection con) {
		PreparedStatement[] prep = new PreparedStatement[this.insertSQLStrings.length];
		String value = "<Not Initialised>";
		String line = "";
		int lineNr = 0;

        log.debug("CSVInitialDataSet " + this.introspector.getPersistentClass().getName() + " create");
		try {
            for (int i = 0; i < this.insertSQLStrings.length; i++) {
                prep[i] = con.prepareStatement(this.insertSQLStrings[i]);
                log.debug("INSERT STATEMENT[" + i + "]=" + this.insertSQLStrings[i]);
            }

            final CSVParser parser = new CSVParser(getCSVInputStream());
			parser.setCommentStart("#;!");
			parser.setEscapes("nrtf", "\n\r\t\f");

            log.trace("Insert statement = " + this.insertSQLStrings[0]);

			int count = 0;
			int lastLineNumber = parser.lastLineNumber();
			while ((value = parser.nextValue()) != null) {
                boolean lineChanged = (parser.lastLineNumber() != lastLineNumber);
                if (lineChanged) {
                    if (count < this.csvMapping.length && lastLineNumber != -1)
                        throw new RuntimeException("The number of records < number of defined columns!");

                    // read next line
					lastLineNumber = parser.lastLineNumber();
					count = 0;
					line = "";
					lineNr = parser.getLastLineNumber(); // get absolute line
				}
				line += value + ";";

                if (count >= this.csvMapping.length) {
                    // there is a residual token
                    throw new RuntimeException("The number of records > number of defined columns!");
                }

                List<PropertyPosition> pList = csvMapping[count++];
                for (PropertyPosition p : pList) {
                    log.trace("Builder " + p.getBuilderPosition() + ": INSERT[" + (p.getLocalPosition() + 1) + "="+ p.getColumn() + "] "
                        + p.getProperty()
                        + " = " + value);

                    this.setPreparedStatement(p, prep[p.getBuilderPosition()], value);
                }

                if (count == this.csvMapping.length) {
               		// execute sql
                                    for (int i = 0; i < this.insertSQLStrings.length; i++) {
                                        log.trace("execute(); lineNr=" + lineNr + ", INSERT STATEMENT[" + i + "]");
                                        prep[i].execute();
                                    }
				}
			}

			parser.close();
		} catch (FileNotFoundException e) {
			log.error("FileNotFoundException: Data-Loader failing (" + file.getName() + ")", e);
            try {
                con.rollback();
            } catch (Exception ex) { }
			throw new RuntimeException(e);
		} catch (IOException e) {
			final String errorText = "IOException: Data-Loader failing (" + file.getName() + ") on line " + lineNr
					+ ": " + line;
			log.error(errorText, e);
            try {
                con.rollback();
            } catch (Exception ex) { }
			throw new RuntimeException(errorText, e);
		} catch (SQLException e) {
			final String errorText = "SQLException: Data-Loader failing (" + file.getName() + ") on line " + lineNr
					+ ": " + line;
			log.error(errorText, e);
            try {
                con.rollback();
            } catch (Exception ex) { }
			throw new RuntimeException(errorText, e);
		} catch (RuntimeException e) {
			final String errorText = "RuntimeException: Data-Loader failing (" + file.getName() + ") on line " + lineNr
					+ ": " + line;
			log.error(errorText, e);
            try {
                con.rollback();
            } catch (Exception ex) { }
			throw new RuntimeException(errorText, e);
		} 
        finally {
            for (int i = 0; i < prep.length; i++) {
                if (prep[i] != null) {
                    try {
                        prep[i].close();
                    } catch (SQLException ex) {
                        log.error("Can't close prepared statement: ", ex);
                    }
                }
            }
		}
	}

	private InputStream getCSVInputStream() {
		InputStream toReturn = null;
		if (this.isCopressed) {
			try {
				toReturn = Ejb3Utils.unjar(new FileInputStream(this.file));
				if (toReturn == null) {
					throw new IllegalArgumentException("The copressed file "
							+ this.file.getName() + " was empty");
				}
			} catch (IOException e) {
				log.error("The file " + this.file.getName()
						+ " could not be accessed", e);
				throw new IllegalArgumentException("The file "
						+ this.file.getAbsolutePath()
						+ " could not be accessed", e);
			}

		} else {
			try {
				toReturn = new FileInputStream(this.file);
			} catch (FileNotFoundException e) {
				throw new IllegalArgumentException("The copressed file "
						+ this.file.getName() + "not found");
			}
		}

		return toReturn;
	}

    private static byte[] getBytesFromFile(File file) throws IOException {
        InputStream is = new FileInputStream(file);

        // Get the size of the file
        long length = file.length();

        // Create the byte array to hold the data
        byte[] bytes = new byte[(int)length];

        // Read in the bytes
        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length
               && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
            offset += numRead;
        }

        // Ensure all the bytes have been read in
        if (offset < bytes.length) {
            throw new IOException("Could not completely read file "+file.getName());
        }

        // Close the input stream and return bytes
        is.close();
        return bytes;
    }

/**
	 * Deletes the data. Compatibility function.
	 *
	 * @param em -
	 *            the entyty manager.
	 * @author Daniel Wiese
	 * @since 17.04.2006
	 * @see com.bm.testsuite.dataloader.InitialDataSet#cleanup(EntityManager)
	 */
    public void cleanup(EntityManager em) {
                log.debug("CSVInitialDataSet " + this.introspector.getPersistentClass().getName() + " cleanup");

                boolean closeTx = false; // don't close Tx from outside
                EntityTransaction tx = em.getTransaction();
                try {
                    if (!tx.isActive()) {
                        // No active transaction? Create one.
                        closeTx = true;
                        tx.begin();
                    }

                    /**
                     * Unable to use bulk delete since
                     * h2database (1.0.104) doesn't support more than
                     * one column in subqueries. The following
                     * query fails for entities with compound keys.
                     */
                    if (EntityBeanIntrospector.
                            getEntityBeanIntrospector(this.introspector.
                            getPersistentClass()).getPkFields().size() > 1) {
                        // compound key workaround
                        String selectionString = "SELECT e FROM " + this.entityBeanClass.getName() + " e";
                        Query query = em.createQuery(selectionString);
                        List l = query.getResultList();
                        for (Object object : l) {
                            em.remove(object);
                        }
                    } else {
                        // standard way
                        Query query = em.createQuery("DELETE FROM " +
                                this.entityBeanClass.getName());
                        query.executeUpdate();
                    }

                    if (closeTx) {
                        // commit only transactions which we implicitly created
                        tx.commit();
                    }
                } catch (RuntimeException e) {
                    log.error("Cleanup error: ", e);
                    tx.rollback();
                    throw e;
                }
	}

	/**
	 * Sets the value (using the right type) in the prepared statement.
	 * 
	 * @param p - property position information
	 *            inside the prepared statement
	 * @param statement -
	 *            the prepared statement itself
	 * @param prop -
	 *            the property representing the type
	 * @param value -
	 *            the value to be set
	 * @throws SQLException -
	 *             in error case
	 */
	private void setPreparedStatement(PropertyPosition p, PreparedStatement statement,
			String value) throws SQLException, IOException {

                int index = p.getLocalPosition() + 1;
                Property prop = p.getProperty();
                String column = p.getColumn();

		// First, check whether this property denotes a relation
		PersistentPropertyInfo persistentFieldInfo = getPersistentFieldInfo(prop
				.getPropertyName());
		if (persistentFieldInfo.isReleation()) {
			EntityReleationInfo relationInfo = persistentFieldInfo
					.getEntityReleationInfo();
                        log.trace("relationInfo=" + relationInfo);
			// Must determine the type of the primary key of the class that is
			// referenced by the relation.
			Set<Property> targetKeyProps = relationInfo.getTargetKeyProperty();
                        log.trace("targetKeyProps=" + targetKeyProps);
			if (targetKeyProps == null || targetKeyProps.size() == 0) {
				// This can happen with relation types that we do not yet
				// support
                                final String errorMessage = "Can't determine key type of relation target - relation type not yet supported?";
                                log.error(errorMessage);
				throw new IllegalArgumentException(errorMessage);
			}
                        for (Property keyProp : targetKeyProps) {
                            if (targetKeyProps.size() == 1) {
                                    setPreparedStatementSimple(index,
                                            statement, keyProp, value);
                                    break;
                            } else {
                                    DbMappingInfo mappingInfo = persistentFieldInfo.getDbMappingInfo(column);
                                    if (getPersistentFieldInfo(keyProp.getPropertyName()).getDbName()
                                               .equalsIgnoreCase(mappingInfo.getReferencedName())) {
                                        setPreparedStatementSimple(index,
                                                statement, keyProp, value);
                                        break;
                                    }
                            }
                        }
		} else {
			// convert to non-primitive if primitive
			setPreparedStatementSimple(index, statement, prop, value);
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
	private void setPreparedStatementSimple(int index, PreparedStatement statement,
			Property fieldProp, String value) throws SQLException, IOException {
                Class<?> type = Ejb3Utils.getNonPrimitiveType(fieldProp.getType());

        if (type.isArray()) {
			if (value == null || value.equals("") || value.equals("null")) {
				statement.setNull(index, Types.JAVA_OBJECT);
            } else {
                File blobFile = resolveFile(value);
                byte[] contents = getBytesFromFile(blobFile);
                statement.setObject(index, contents);
            }
        } else if (type.equals(String.class)) {
                statement.setString(index, value);
		} else if (type.equals(Integer.class)) {
			if (value == null || value.equals("") || value.equals("null")) {
				statement.setNull(index, Types.INTEGER);
			} else {
				statement.setInt(index, ((value.equals("")) ? 0 : Integer
						.valueOf(value)));
			}
		} else if (type.equals(Long.class)) {
			if (value == null || value.equals("") || value.equals("null")) {
				statement.setNull(index, Types.BIGINT);
			} else {
				statement.setLong(index, ((value.equals("")) ? 0 : Long
						.valueOf(value)));
			}
		} else if (type.equals(Boolean.class)) {
			final boolean result = BooleanUtils.toBoolean(value);
			statement.setBoolean(index, result);
		} else if (type.equals(Short.class)) {
			statement.setShort(index, ((value.equals("")) ? 0 : Short
					.valueOf(value)));
		} else if (type.equals(Byte.class)) {
			statement.setByte(index, Byte.valueOf(value));
		} else if (type.equals(Character.class)) {
			statement.setString(index, String.valueOf(value));
		} else if (type.equals(Date.class)) {
			parseAndSetDate(index, value, statement);
		} else if (type.equals(Double.class)) {
			statement.setDouble(index, ((value.equals("")) ? 0 : Double
					.valueOf(value)));
		} else if (type.equals(Float.class)) {
			statement.setFloat(index, ((value.equals("")) ? 0 : Float
					.valueOf(value)));
		} else if (type.isEnum()) {
                        // try to parse as int
                        try {
			statement.setInt(index, Integer.valueOf(value));
                        } catch (NumberFormatException ex) {
                            // not an int, continue trying a literal name
                        }
                    
                        // try to parse literal name
                        try {
                            Class enumClass = type.getClassLoader().
                                    loadClass(type.getCanonicalName());
                            Enum someEnum = Enum.valueOf(enumClass,value);

                            // insert as ordinal or string
                            if (isEnumeratedOrdinal(fieldProp)) {
                                // Enumerated.ORDINAL
                                statement.setObject(index, someEnum.ordinal());
                            } else {
                                // Enumerated.STRING
                                statement.setObject(index, someEnum.name());
                            }
                            
                        } catch (Exception ex) {
                            log.error("Can't load enum value for constant: " +  value + ": ", ex);
                            throw new RuntimeException(ex);
                        }
		}
	}
        
        @SuppressWarnings("unchecked")
        private boolean isEnumeratedOrdinal(Property enumField) {
            Enumerated enumerated = (Enumerated) enumField.getAnnotation(Enumerated.class);
            if (enumerated != null) {
                if (EnumType.STRING == enumerated.value())
                    return false;
                // else => EnumType.ORDINAL
            }
            
            // default is EnumType.ORDINAL
            return true;
        }


	private void parseAndSetDate(int index, String value,
			PreparedStatement statement) throws SQLException {
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
					log.debug("Date parser (" + current
							+ ") was not working, trying next");
				}
			}

			if (!success) {
				// java 1.5 will convert this to string builder internally
				String msg = "Illegal date format (" + value + ")";
				msg += " expecting one of: ";
				for (DateFormats current : DateFormats.values()) {
					msg += "(" + current.toPattern() + "), ";
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
        
        static class PropertyPosition {
                private Property property;
                private String column;
                private int localPosition;
                private int builderPosition;


                public PropertyPosition(Property property, String column, int localPosition) {
                    this.property = property;
                    this.column = column;
                    this.localPosition = localPosition;
                    this.builderPosition = 0;
                }

                public PropertyPosition(Property property, String column, int localPosition,
                        int builderPosition) {
                    this.property = property;
                    this.column = column;
                    this.localPosition = localPosition;
                    this.builderPosition = builderPosition;
                }

                public int getLocalPosition() {
                    return localPosition;
                }

                public void setLocalPosition(int localPosition) {
                    this.localPosition = localPosition;
                }

                public Property getProperty() {
                    return property;
                }

                public void setProperty(Property property) {
                    this.property = property;
                }

                public String getColumn() {
                    return column;
                }

                public void setColumn(String column) {
                    this.column = column;
                }

                public int getBuilderPosition() {
                    return builderPosition;
                }

                public void setBuilderPosition(int builderPosition) {
                    this.builderPosition = builderPosition;
                }
        }
        
        static class Builder {
                private String tableName;
                private StringBuilder insertSQL = new StringBuilder();
		private StringBuilder questionMarks = new StringBuilder();
                private Map<String, PropertyPosition> propertyPositions = new HashMap<String, PropertyPosition>();
                private int counter = 0;

                public Builder(final String tableName) {
                    this.tableName = tableName;
                    insertSQL.append("INSERT INTO ").append(tableName).append(" (");
                }
                
                public String toInsertString() {
                    String columns = strip(insertSQL);
                    String values = strip(questionMarks);

                    return columns + ") VALUES (" + values + ")";
                }

                public String getTableName() {
                    return tableName;
                }

                public PropertyPosition getPosition (final CompoundPropertyName name) {
                    return propertyPositions.get(name.toString());
                }
                
                public void append(String name, Property property, String column) {
                    appendValue(name, "?", property, column);
                }
                
                public void appendValue(String name, String value, Property property, String column) {
                    propertyPositions.put(name, new PropertyPosition(property, column, counter));
                    appendDirectValue(column, value);
                }
                
                public void appendDirectValue(String col, String value) {
                    insertSQL.append(col);
                    insertSQL.append(", ");
                    questionMarks.append(value);
                    questionMarks.append(", ");
                    counter++;
                }
                
                public void appendStringValue(String col, String value) {
                    insertSQL.append(col);
                    insertSQL.append(", ");
                    questionMarks.append("'");
                    questionMarks.append(value);
                    questionMarks.append("'");
		    questionMarks.append(", ");
                    counter++;
                }
                
                private static String strip(StringBuilder sb) {
                    String str = sb.toString();
                    int last = str.lastIndexOf(",");
                    if (last == -1)
                        last = str.length();
                    return str.substring(0, last);
                }
        }

        public static class TableInfo implements Comparable<TableInfo> {
            final String tableName;
            final Class clazz;

            public TableInfo(String tableName, Class clazz) {
                this.tableName = tableName;
                this.clazz = clazz;
            }

            public Class getClazz() {
                return clazz;
            }

            public String getTableName() {
                return tableName;
            }

            @Override
            public boolean equals(Object obj) {
                if (obj == null) {
                    return false;
                }
                final TableInfo other = (TableInfo) obj;
                if ((this.tableName == null) ? (other.tableName != null)
                        : !this.tableName.equals(other.tableName)) {
                    return false;
                }
                return true;
            }

            @Override
            public int hashCode() {
                int hash = 3 * tableName.length();
                return hash;
            }

            public int compareTo(TableInfo b) {
                    if (this.equals(b)) {
                        return 0;
                    }

                    if (this.getClazz().isAssignableFrom(b.getClazz())) {
                        return -1;
                    } else {
                        return 1;
                    }
            }

            @Override
            public String toString() {
                return "[table=" + tableName
                        + ", class=" + clazz.getSimpleName()
                        + "]";
            }
        }

        static class CompoundPropertyName {
            private String name;
            private String column;
            private static final Pattern p = Pattern.compile("([^\\s(]+)[\\s]*(\\([^)]+\\))?");

            public CompoundPropertyName(String definition) {
                Matcher m = p.matcher(definition);
                if (!m.matches())
                    throw new RuntimeException("Can't parse definition '" + definition + "'");

                this.name = m.group(1);
                if (m.group(2) != null || "()".equals(m.group(2))) {
                    this.column = m.group(2).substring(1,
                            m.group(2).length() - 1);
                }
            }

            public CompoundPropertyName(String name, String column) {
                this.name = name;
                this.column = column;
            }

            public String getColumn() {
                return column;
            }

            public String getName() {
                return name;
            }

            public void setColumn(String column) {
                this.column = column;
            }

            public void setName(String name) {
                this.name = name;
            }

            @Override
            public String toString() {
                if (column != null)
                    return name + "(" + column + ")";
                else
                    return name;
            }


        }
}
