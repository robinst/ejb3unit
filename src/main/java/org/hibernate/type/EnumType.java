//$Id: EnumType.java,v 1.1 2006/04/17 12:11:06 daniel_wiese Exp $
package org.hibernate.type;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.sql.DatabaseMetaData;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.util.StringHelper;
import org.hibernate.util.ReflectHelper;
import org.hibernate.usertype.ParameterizedType;
import org.hibernate.usertype.UserType;

/**
 * Enum type mapper
 * Try and find the appropriate SQL type depending on column metadata
 * @author Emmanuel Bernard
 */
public class EnumType implements UserType, ParameterizedType {
	private static Log log = LogFactory.getLog(EnumType.class);
	private static final boolean IS_TRACE_ENABLED;
	static {
		//cache this, because it was a significant performance cost
		IS_TRACE_ENABLED = LogFactory.getLog( StringHelper.qualifier( Type.class.getName() ) ).isTraceEnabled();
	}

	public static final String ENUM = "enumClass";
	public static final String SCHEMA = "schema";
	public static final String CATALOG = "catalog";
	public static final String TABLE = "table";
	public static final String COLUMN = "column";
	public static final String TYPE = "type";

	private static Map<Class,Object[]> enumValues = new HashMap<Class, Object[]>();

	private Class<? extends Enum> enumClass;
	private String column;
	private String table;
	private String catalog;
	private String schema;
	private boolean guessed = false;
	private int sqlType = Types.INTEGER; //before any guessing

	public int[] sqlTypes() {
		return new int[] { sqlType };
	}

	public Class returnedClass() {
		return enumClass;
	}

	public boolean equals(Object x, Object y) throws HibernateException {
		return x == y;
	}

	public int hashCode(Object x) throws HibernateException {
		return x == null ? 0 : x.hashCode();
	}

	public Object nullSafeGet(ResultSet rs, String[] names, Object owner) throws HibernateException, SQLException {
		Object object = rs.getObject( names[0] );
		if ( rs.wasNull() ) {
			if ( IS_TRACE_ENABLED ) {
				log.debug("Returning null as column " + names[0]);
			}
			return null;
		}
		if (object instanceof Number) {
			Object[] values = enumValues.get(enumClass);
			if (values == null) {
				try {
					Method method = null;
					method = enumClass.getDeclaredMethod( "values", new Class[0] );
					values = (Object[]) method.invoke(null, new Object[0] );
					enumValues.put( enumClass, values );
				}
				catch (Exception e) {
					throw new HibernateException("Error while accessing enum.values(): " + enumClass, e);
				}
			}
			int ordinal = ( (Number) object ).intValue();
			if (ordinal < 0 || ordinal >= values.length) {
				throw new IllegalArgumentException("Unknown ordinal value for enum " + enumClass + ": " + ordinal);
			}
			if ( IS_TRACE_ENABLED ) {
				log.debug("Returning '" + ordinal + "' as column " + names[0]);
			}
			return values[ordinal];
		}
		else {
			String name = (String) object;
			if ( IS_TRACE_ENABLED ) {
				log.debug("Returning '" + name + "' as column " + names[0]);
			}
			try {
				return Enum.valueOf(enumClass, name);
			}
			catch (IllegalArgumentException iae) {
				throw new IllegalArgumentException("Unknown name value for enum " + enumClass + ": " + name, iae);
			}
		}
	}

	public void nullSafeSet(PreparedStatement st, Object value, int index) throws HibernateException, SQLException {
		//if (!guessed) guessType( st, index );
		if (value == null) {
			if (IS_TRACE_ENABLED) log.debug("Binding null to parameter: " + index);
			st.setNull( index, sqlType );
		}
		else {
			boolean isOrdinal = isOrdinal(sqlType);
			if (isOrdinal) {
				int ordinal = ( (Enum) value ).ordinal();
				if ( IS_TRACE_ENABLED ) {
					log.debug("Binding '" + ordinal + "' to parameter: " + index);
				}
				st.setObject( index, new Integer(ordinal), sqlType );
			}
			else {
				String enumString = ( (Enum) value ).name();
				if ( IS_TRACE_ENABLED ) {
					log.debug("Binding '" + enumString + "' to parameter: " + index);
				}
				st.setObject( index, enumString, sqlType );
			}
		}
	}

	//TODO remove
	private void guessType(PreparedStatement statement, int index) {
		//TODO use SQLException wrapper?
		if (! guessed) {
			try {
				sqlType = guessTypeFromMetaData(statement.getConnection().getMetaData());
			}
			catch (SQLException e) {
				sqlType = guessTypeByParameter(statement, index, e);
			}
			catch (HibernateException e) {
				sqlType = guessTypeByParameter(statement, index, e);
			}
			guessed = true;
		}
	}

	//TODO remove
	private int guessTypeFromMetaData(DatabaseMetaData metadata) throws SQLException {
		ResultSet rs = null;
		try {
			String username = metadata.getUserName();
			int guessedType = Types.NULL;
			int numResults = 0;
			boolean nonNullType = false;

			// Try to find the column information up to three times. First with values as is in case
			// the database ever stores mixed case identifiers, or the supplied identifiers match
			// perfectly. Then if the metadata is not found, try using lower and upper case
			// identifiers - but only if the datbaase reports it would use them.
			rs = metadata.getColumns(catalog, schema, table, column);

			boolean isValid;

			isValid = rs.next();

			if ( ! isValid && metadata.storesLowerCaseIdentifiers() ) {
				rs.close();
				rs = metadata.getColumns( catalog == null ? catalog : catalog.toLowerCase(),
									  schema == null  ? schema  : schema.toLowerCase(),
									  table.toLowerCase(), column.toLowerCase() );
				isValid = rs.next();
			}

			if ( ! isValid && metadata.storesUpperCaseIdentifiers() ) {
				rs.close();
				rs = metadata.getColumns( catalog == null ? catalog : catalog.toUpperCase(),
									  schema == null  ? schema  : schema.toUpperCase(),
									  table.toUpperCase(), column.toUpperCase() );
				isValid = rs.next();
			}

			// try to find
			while ( isValid ) {
				guessedType = rs.getInt( "DATA_TYPE" );
				if ( rs.wasNull() ) {
					continue; // TODO throw exception?
				}
				else {
					nonNullType = true;
				}

				numResults++;
				if ( username != null) {
					String schema = rs.getString( "TABLE_SCHEM" );
					// if the username matches the schema, there's no better guess available...
					if ( username.equalsIgnoreCase(schema) ) return guessedType;
				} else if ( numResults > 1 ) {
					// if there's no username and multiple results, there's no point in continuing
					throw new HibernateException("Several columns matching in metadata: " + column);
				}
				isValid = rs.next();
			}
			if (numResults == 0) throw new HibernateException("Enum type column not found in metadata: " + column);
			if (numResults  > 1) throw new HibernateException("Several columns matching in metadata: " + column);
			if (!nonNullType) throw new HibernateException("Column without type in metadata!: " + column);
			log.trace( "Enum type guessed from metadata: " + guessedType);
			return guessedType;
		} finally {
			try {
				if (rs != null) rs.close();
			} catch (SQLException e) {
				//swallow in purpose
			}
		}
	}

	//TODO remove
	private int guessTypeByParameter(PreparedStatement statement, int index, Exception e) {
		log.debug("Unable to guess the column type for enum through conn.getMetadata(): "
						+ e.getMessage() );
		try {
			return statement.getParameterMetaData().getParameterType( index );
		}
		catch (SQLException ee) {
			log.warn("Unable to guess enum type, default to INTEGER", ee);
			return Types.INTEGER;
		}
	}

	private boolean isOrdinal(int paramType) {
		switch(paramType) {
			case Types.INTEGER:
			case Types.NUMERIC:
			case Types.SMALLINT:
			case Types.TINYINT:
			case Types.BIGINT:
			case Types.DECIMAL: //for Oracle Driver
			case Types.DOUBLE:  //for Oracle Driver
			case Types.FLOAT:   //for Oracle Driver
				return true;
			case Types.CHAR:
			case Types.LONGVARCHAR:
			case Types.VARCHAR:
				return false;
			default:
				throw new HibernateException("Unable to persist an Enum in a column of SQL Type: " +paramType);
		}
	}

	public Object deepCopy(Object value) throws HibernateException {
		return value;
	}

	public boolean isMutable() {
		return false;
	}

	public Serializable disassemble(Object value) throws HibernateException {
		return (Serializable) value;
	}

	public Object assemble(Serializable cached, Object owner) throws HibernateException {
		return cached;
	}

	public Object replace(Object original, Object target, Object owner) throws HibernateException {
		return original;
	}

	public void setParameterValues(Properties parameters) {
		String enumClassName = parameters.getProperty(ENUM);
        try {
            enumClass = ReflectHelper.classForName( enumClassName, this.getClass() ).asSubclass(Enum.class);
        }
        catch (ClassNotFoundException exception) {
            throw new HibernateException("Enum class not found", exception);
        }
		//nullify unnullified properties yuck!
		schema = parameters.getProperty(SCHEMA);
		if ( "".equals( schema ) ) schema = null;
		catalog = parameters.getProperty(CATALOG);
		if ( "".equals( catalog ) ) catalog = null;
		table = parameters.getProperty(TABLE);
		column = parameters.getProperty(COLUMN);
		String type = parameters.getProperty( TYPE );
		if (type != null) {
			sqlType = Integer.decode( type ).intValue();
			guessed = true;
		}
	}
}
