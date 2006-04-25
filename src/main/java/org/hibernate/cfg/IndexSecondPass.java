//$Id: IndexSecondPass.java,v 1.1 2006/04/17 12:11:10 daniel_wiese Exp $
package org.hibernate.cfg;

import java.util.Map;

import org.hibernate.AnnotationException;
import org.hibernate.MappingException;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Table;

/**
 * @author Emmanuel Bernard
 */
public class IndexSecondPass implements SecondPass {
	private Table table;
	private final String indexName;
	private final String[] columns;
	private final ExtendedMappings mappings;
	private final Ejb3Column column;

	public IndexSecondPass(Table table, String indexName, String[] columns, ExtendedMappings mappings) {
		this.table = table;
		this.indexName = indexName;
		this.columns = columns;
		this.mappings = mappings;
		this.column = null;
	}

	public IndexSecondPass(String indexName, Ejb3Column column, ExtendedMappings mappings) {
		this.indexName = indexName;
		this.column = column;
		this.columns = null;
		this.mappings = mappings;
	}

	public void doSecondPass(Map persistentClasses, Map inheritedMetas) throws MappingException {
		if (columns != null) {
			for ( String columnName : columns) {
				addIndexToColumn( columnName );
			}
		}
		if (column != null) {
			this.table = column.getTable();
			addIndexToColumn( column.getName() );
		}
	}

	private void addIndexToColumn(String columnName) {
		Column column = table.getColumn( new Column(
				mappings.getPhysicalColumnName( columnName, table )
		) );
		if ( column == null ) {
			throw new AnnotationException(
					"@Index references a unknown column: " + columnName
			);
		}
		table.getOrCreateIndex( indexName ).addColumn( column );
	}
}
