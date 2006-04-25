//$Id: EJB3NamingStrategy.java,v 1.1 2006/04/17 12:11:10 daniel_wiese Exp $
package org.hibernate.cfg;

import org.hibernate.util.StringHelper;
import org.hibernate.AssertionFailure;

/**
 * NAming strategy implementing the EJB3 standards
 *
 * @author Emmanuel Bernard
 */
public class EJB3NamingStrategy implements NamingStrategy {
	public static final NamingStrategy INSTANCE = new EJB3NamingStrategy();

	public String classToTableName(String className) {
		return StringHelper.unqualify(className);
	}

	public String propertyToColumnName(String propertyName) {
		return StringHelper.unqualify(propertyName);
	}

	public String tableName(String tableName) {
		return tableName;
	}

	public String columnName(String columnName) {
		return columnName;
	}

	public String collectionTableName(String ownerEntityTable, String associatedEntityTable, String propertyName) {
		return tableName( new StringBuilder(ownerEntityTable).append("_")
				.append(
					associatedEntityTable != null ?
					associatedEntityTable :
					StringHelper.unqualify( propertyName )
				).toString() );
	}

	public String joinKeyColumnName(String joinedColumn, String joinedTable) {
		return columnName( joinedColumn );
	}

	public String foreignKeyColumnName(String propertyName, String propertyTableName, String referencedColumnName) {
		String header = propertyName != null ? StringHelper.unqualify( propertyName ) : propertyTableName;
		if (header == null) throw new AssertionFailure("NammingStrategy not properly filled");
		return columnName( header + "_" + referencedColumnName );
	}

	public String logicalColumnName(String columnName, String propertyName) {
		return StringHelper.isNotEmpty( columnName ) ? columnName : StringHelper.unqualify( propertyName );
	}

	public String logicalCollectionTableName(String tableName,
											 String ownerEntityTable, String associatedEntityTable, String propertyName
	) {
		if ( tableName != null ) {
			return tableName;
		}
		else {
			//use of a stringbuffer to workaround a JDK bug
			return new StringBuffer(ownerEntityTable).append("_")
					.append(
						associatedEntityTable != null ?
						associatedEntityTable :
						StringHelper.unqualify( propertyName )
					).toString();
		}
	}

	public String logicalCollectionColumnName(String columnName, String propertyName, String referencedColumn) {
		return StringHelper.isNotEmpty( columnName ) ?
				columnName :
				StringHelper.unqualify( propertyName ) + "_" + referencedColumn;
	}
}
