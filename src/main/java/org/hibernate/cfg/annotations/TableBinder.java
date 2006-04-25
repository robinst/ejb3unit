//$Id: TableBinder.java,v 1.1 2006/04/17 12:11:11 daniel_wiese Exp $
package org.hibernate.cfg.annotations;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.persistence.UniqueConstraint;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.AnnotationException;
import org.hibernate.AssertionFailure;
import org.hibernate.annotations.Index;
import org.hibernate.cfg.AnnotationBinder;
import org.hibernate.cfg.Ejb3JoinColumn;
import org.hibernate.cfg.ExtendedMappings;
import org.hibernate.cfg.IndexSecondPass;
import org.hibernate.mapping.Collection;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.DependantValue;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.SimpleValue;
import org.hibernate.mapping.Table;
import org.hibernate.mapping.ToOne;
import org.hibernate.mapping.Value;
import org.hibernate.mapping.JoinedSubclass;
import org.hibernate.util.StringHelper;

/**
 * Table related operations
 *
 * @author Emmanuel Bernard
 */
public class TableBinder {
	//TODO move it to a getter/setter strategy
	private static Log log = LogFactory.getLog( TableBinder.class );
	private String schema;
	private String catalog;
	private String name;
	private boolean isAbstract;
	private List<String[]> uniqueConstraints;
	String constraints;
	Table denormalizedSuperTable;
	ExtendedMappings mappings;
	private String ownerEntityTable;
	private String associatedEntityTable;
	private String propertyName;

	public void setSchema(String schema) {
		this.schema = schema;
	}

	public void setCatalog(String catalog) {
		this.catalog = catalog;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setAbstract(boolean anAbstract) {
		isAbstract = anAbstract;
	}

	public void setUniqueConstraints(UniqueConstraint[] uniqueConstraints) {
		this.uniqueConstraints = TableBinder.buildUniqueConstraints( uniqueConstraints );
	}

	public void setConstraints(String constraints) {
		this.constraints = constraints;
	}

	public void setDenormalizedSuperTable(Table denormalizedSuperTable) {
		this.denormalizedSuperTable = denormalizedSuperTable;
	}

	public void setMappings(ExtendedMappings mappings) {
		this.mappings = mappings;
	}

	public Table bind() {
		//logicalName only accurate for assoc table...
		String logicalName = mappings.getNamingStrategy().logicalCollectionTableName( name, ownerEntityTable, associatedEntityTable, propertyName );
		String extendedName = name != null ?
				mappings.getNamingStrategy().tableName( name ) :
				mappings.getNamingStrategy().collectionTableName( ownerEntityTable, associatedEntityTable, propertyName);
		return fillTable(
				schema, catalog,
				extendedName, logicalName, isAbstract, uniqueConstraints, constraints,
				denormalizedSuperTable, mappings
		);
	}

	public static Table fillTable(
			String schema, String catalog, String realTableName, String logicalName, boolean isAbstract,
			List uniqueConstraints, String constraints, Table denormalizedSuperTable, ExtendedMappings mappings
	) {
		schema = AnnotationBinder.isDefault( schema ) ? mappings.getSchemaName() : schema;
		catalog = AnnotationBinder.isDefault( catalog ) ? mappings.getCatalogName() : catalog;
		Table table;
		if ( denormalizedSuperTable != null ) {
			table = mappings.addDenormalizedTable(
					schema,
					catalog,
					realTableName,
					isAbstract,
					null, //subselect
					denormalizedSuperTable
			);
		}
		else {
			table = mappings.addTable(
					schema,
					catalog,
					realTableName,
					null, //subselect
					isAbstract
			);
		}
		if ( uniqueConstraints != null && uniqueConstraints.size() > 0 ) {
			mappings.addUniqueConstraints( table, uniqueConstraints );
		}
		if ( constraints != null ) table.addCheckConstraint( constraints );
		mappings.addTableBinding( schema, catalog, logicalName, realTableName, denormalizedSuperTable);
		return table;
	}

	public static void bindFk(
			PersistentClass referencedEntity, PersistentClass destinationEntity, Ejb3JoinColumn[] columns,
			SimpleValue value,
			boolean unique, ExtendedMappings mappings
	) {
		PersistentClass associatedClass;
		if ( destinationEntity != null ) {
			//overidden destination
			associatedClass = destinationEntity;
		}
		else {
			associatedClass = columns[0].getPropertyHolder() == null ? null : columns[0].getPropertyHolder()
					.getPersistentClass();
		}
		final String mappedByProperty = columns[0].getMappedBy();
		if ( StringHelper.isNotEmpty( mappedByProperty ) ) {
			/**
			 * Get the columns of the mapped-by property
			 * copy them and link the copy to the actual value
			 */
			if ( log.isDebugEnabled() ) {
				log.debug(
						"Retrieving property " + associatedClass.getEntityName() + "." + mappedByProperty
				);
			}

			final Property property = associatedClass.getProperty( columns[0].getMappedBy() );
			Iterator mappedByColumns;
			if ( property.getValue() instanceof Collection ) {
				Collection collection = ( (Collection) property.getValue() );
				Value element = collection.getElement();
				if (element == null) {
					throw new AnnotationException("Illegal use of mappedBy on both sides of the relationship: "
							+ associatedClass.getEntityName() + "." + mappedByProperty );
				}
				mappedByColumns = element.getColumnIterator();
			}
			else {
				mappedByColumns = property.getValue().getColumnIterator();
			}
			while ( mappedByColumns.hasNext() ) {
				Column column = (Column) mappedByColumns.next();
				columns[0].overrideSqlTypeIfNecessary( column );
				columns[0].linkValueUsingAColumnCopy( column, value );
			}
		}
		else if ( columns[0].isImplicit() ) {
			/**
			 * if columns are implicit, then create the columns based on the
			 * referenced entity id columns
			 */
			Iterator idColumns;
			if (referencedEntity instanceof JoinedSubclass) {
				idColumns = ( (JoinedSubclass) referencedEntity ).getKey().getColumnIterator();
			}
			else {
				idColumns = referencedEntity.getIdentifier().getColumnIterator();
			}
			while ( idColumns.hasNext() ) {
				Column column = (Column) idColumns.next();
				columns[0].overrideSqlTypeIfNecessary( column );
				columns[0].linkValueUsingDefaultColumnNaming( column, referencedEntity.getTable(), value );
			}
		}
		else {
			int fkEnum = Ejb3JoinColumn.checkReferencedColumnsType( columns, referencedEntity, mappings );

			if ( Ejb3JoinColumn.NON_PK_REFERENCE == fkEnum ) {
				String referencedPropertyName;
				if ( value instanceof ToOne ) {
					referencedPropertyName = ( (ToOne) value ).getReferencedPropertyName();
				}
				else if ( value instanceof DependantValue ) {
					String propertyName = columns[0].getPropertyName();
					if ( propertyName != null ) {
						Collection collection = (Collection) referencedEntity.getProperty( propertyName )
								.getValue();
						referencedPropertyName = collection.getReferencedPropertyName();
					}
					else {
						throw new AnnotationException( "SecondaryTable JoinColumn cannot reference a non primary key" );
					}

				}
				else {
					throw new AssertionFailure(
							"Do a property ref on an unexpected Value type: "
									+ value.getClass().getName()
					);
				}
				if ( referencedPropertyName == null ) {
					throw new AssertionFailure(
							"No property ref found while expected"
					);
				}
				Property synthProp = referencedEntity.getProperty( referencedPropertyName );
				if ( synthProp == null ) {
					throw new AssertionFailure(
							"Cannot find synthProp: " + referencedEntity.getEntityName() + "." + referencedPropertyName
					);
				}
				linkJoinColumnWithValueOverridingNameIfImplicit( referencedEntity, synthProp.getColumnIterator(), columns, value );

			}
			else {
				if ( Ejb3JoinColumn.NO_REFERENCE == fkEnum ) {
					//implicit case, we hope PK and FK columns are in the same order
					if ( columns.length != referencedEntity.getIdentifier().getColumnSpan() ) {
						throw new AnnotationException(
								"A Foreign key refering " + referencedEntity.getEntityName()
										+ " has the wrong number of column. should be " + referencedEntity.getIdentifier()
										.getColumnSpan()
						);
					}
					linkJoinColumnWithValueOverridingNameIfImplicit(
							referencedEntity,
							referencedEntity.getIdentifier().getColumnIterator(),
							columns,
							value
					);
				}
				else {
					//explicit referencedColumnName
					Iterator idColItr = referencedEntity.getKey().getColumnIterator();
					org.hibernate.mapping.Column col;
					Table table = referencedEntity.getTable(); //works cause the pk has to be on the primary table
					if ( ! idColItr.hasNext() ) log.debug( "No column in the identifier!" );
					while ( idColItr.hasNext() ) {
						boolean match = false;
						//for each PK column, find the associated FK column.
						col = (org.hibernate.mapping.Column) idColItr.next();
						for ( Ejb3JoinColumn joinCol : columns ) {
							String referencedColumn = joinCol.getReferencedColumn();
							referencedColumn = mappings.getPhysicalColumnName( referencedColumn, table );
							if ( referencedColumn.equals( col.getName() ) ) {
								//proper join column
								if ( joinCol.isNameDeferred() ) {
									joinCol.linkValueUsingDefaultColumnNaming( col, referencedEntity.getTable(), value );
								}
								else {
									joinCol.linkWithValue( value );
								}
								joinCol.overrideSqlTypeIfNecessary( col );
								match = true;
								break;
							}
						}
						if ( match == false ) {
							throw new AnnotationException(
									"Column name " + col.getName() + " of "
											+ referencedEntity.getEntityName() + " not found in JoinColumns.referencedColumnName"
							);
						}
					}
				}
			}
		}
		value.createForeignKey();
		if ( unique == true ) {
			createUniqueConstraint( value );
		}
	}

	private static void linkJoinColumnWithValueOverridingNameIfImplicit(
			PersistentClass referencedEntity, Iterator columnIterator, Ejb3JoinColumn[] columns, SimpleValue value
	) {
		for ( Ejb3JoinColumn joinCol : columns ) {
			Column synthCol = (Column) columnIterator.next();
			if ( joinCol.isNameDeferred() ) {
				//this has to be the default value
				joinCol.linkValueUsingDefaultColumnNaming( synthCol, referencedEntity.getTable(), value );
			}
			else {
				joinCol.linkWithValue( value );
			}
			joinCol.overrideSqlTypeIfNecessary( synthCol );
		}
	}

	public static void createUniqueConstraint(Value value) {
		Iterator iter = value.getColumnIterator();
		ArrayList cols = new ArrayList();
		while ( iter.hasNext() ) {
			cols.add( iter.next() );
		}
		value.getTable().createUniqueKey( cols );
	}

	public static void addIndexes(Table hibTable, Index[] indexes, ExtendedMappings mappings) {
		for ( Index index : indexes ) {
			mappings.addSecondPass(
					new IndexSecondPass( hibTable, index.name(), index.columnNames(), mappings )
			);
		}
	}

	public static List<String[]> buildUniqueConstraints(UniqueConstraint[] constraintsArray) {
		List<String[]> result = new ArrayList<String[]>();
		if ( constraintsArray.length != 0 ) {
			for ( UniqueConstraint uc : constraintsArray ) {
				result.add( uc.columnNames() );
			}
		}
		return result;
	}

	public void setDefaultName(String ownerEntityTable, String associatedEntityTable, String propertyName) {
		this.ownerEntityTable = ownerEntityTable;
		this.associatedEntityTable = associatedEntityTable;
		this.propertyName = propertyName;
		this.name = null;
	}
}
