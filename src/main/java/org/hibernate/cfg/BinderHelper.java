//$Id: BinderHelper.java,v 1.1 2006/04/17 12:11:10 daniel_wiese Exp $
package org.hibernate.cfg;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.hibernate.AnnotationException;
import org.hibernate.AssertionFailure;
import org.hibernate.MappingException;
import org.hibernate.cfg.annotations.TableBinder;
import org.hibernate.mapping.Collection;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.ToOne;
import org.hibernate.mapping.Value;
import org.hibernate.mapping.Table;
import org.hibernate.mapping.Join;
import org.hibernate.util.StringHelper;

/**
 * @author Emmanuel Bernard
 */
public class BinderHelper {
	private BinderHelper() {
	}

	static {
		Set<String> primitiveNames = new HashSet<String>();
		primitiveNames.add( byte.class.getName() );
		primitiveNames.add( short.class.getName() );
		primitiveNames.add( int.class.getName() );
		primitiveNames.add( long.class.getName() );
		primitiveNames.add( float.class.getName() );
		primitiveNames.add( double.class.getName() );
		primitiveNames.add( char.class.getName() );
		primitiveNames.add( boolean.class.getName() );
		PRIMITIVE_NAMES = Collections.unmodifiableSet( primitiveNames );
	}

	public static final Set<String> PRIMITIVE_NAMES;

	/**
	 * create a property copy reusing the same value
	 */
	public static Property shallowCopy(Property property) {
		Property clone = new Property();
		clone.setCascade( property.getCascade() );
		clone.setInsertable( property.isInsertable() );
		clone.setLazy( property.isLazy() );
		clone.setName( property.getName() );
		clone.setNaturalIdentifier( property.isNaturalIdentifier() );
		clone.setOptimisticLocked( property.isOptimisticLocked() );
		clone.setOptional( property.isOptional() );
		clone.setPersistentClass( property.getPersistentClass() );
		clone.setPropertyAccessorName( property.getPropertyAccessorName() );
		clone.setSelectable( property.isSelectable() );
		clone.setUpdateable( property.isUpdateable() );
		clone.setValue( property.getValue() );
		return clone;
	}

	public static void createSyntheticPropertyReference(
			Ejb3JoinColumn[] columns,
			PersistentClass ownerEntity,
			PersistentClass associatedEntity,
			Value value,
			boolean inverse, ExtendedMappings mappings
	) {
		//associated entity only used for more precise exception, yuk!
		if ( columns[0].isImplicit() || StringHelper.isNotEmpty( columns[0].getMappedBy() ) ) return;
		int fkEnum = Ejb3JoinColumn.checkReferencedColumnsType( columns, ownerEntity, mappings );
		PersistentClass associatedClass = columns[0].getPropertyHolder() == null ? null : columns[0].getPropertyHolder()
				.getPersistentClass();
		if ( Ejb3JoinColumn.NON_PK_REFERENCE == fkEnum ) {
			/**
			 * Create a synthetic property to refer to including an
			 * embedded component value containing all the properties
			 * mapped to the referenced columns
			 * We need to shallow copy those properties to mark them
			 * as non insertable / non updatable
			 */
			StringBuilder propertyNameBuffer = new StringBuilder( "_" );
			propertyNameBuffer.append( associatedClass.getEntityName().replace( '.', '_' ) );
			propertyNameBuffer.append( "_" ).append( columns[0].getPropertyName() );
			String syntheticPropertyName = propertyNameBuffer.toString();
			//find properties associated to a certain column
			Object columnOwner = findColumnOwner( ownerEntity, columns[0].getReferencedColumn(), mappings );
			List<Property> properties = findPropertiesByColumns( columnOwner, columns, mappings );
			//create an embeddable component
			Property synthProp = null;
			if ( properties != null ) {
				//todo how about properties.size() == 1, this should be much simpler
				Component embeddedComp = columnOwner instanceof PersistentClass ?
						new Component( (PersistentClass) columnOwner ) :
						new Component( (Join) columnOwner );
				embeddedComp.setEmbedded( true );
				embeddedComp.setComponentClassName( embeddedComp.getOwner().getClassName() );
				for ( Property property : properties ) {
					Property clone = BinderHelper.shallowCopy( property );
					clone.setInsertable( false );
					clone.setUpdateable( false );
					clone.setNaturalIdentifier( false );
					embeddedComp.addProperty( clone );
				}
				synthProp = new Property();
				synthProp.setName( syntheticPropertyName );
				//synthProp.setNodeName(syntheticPropertyName);
				synthProp.setPersistentClass( ownerEntity );
				synthProp.setUpdateable( false );
				synthProp.setInsertable( false );
				synthProp.setValue( embeddedComp );
				synthProp.setPropertyAccessorName( "embedded" );
				ownerEntity.addProperty( synthProp );
				//make it unique
				TableBinder.createUniqueConstraint( embeddedComp );
			}
			else {
				//TODO use a ToOne type doing a second select
				StringBuilder columnsList = new StringBuilder();
				columnsList.append( "referencedColumnNames(" );
				for ( Ejb3JoinColumn column : columns ) {
					columnsList.append( column.getReferencedColumn() ).append( ", " );
				}
				columnsList.setLength( columnsList.length() - 2 );
				columnsList.append( ") " );

				if ( associatedEntity != null ) {
					//overidden destination
					columnsList.append( "of " )
							.append( associatedEntity.getEntityName() )
							.append( "." )
							.append( columns[0].getPropertyName() )
							.append( " " );
				}
				else {
					if ( columns[0].getPropertyHolder() != null ) {
						columnsList.append( "of " )
								.append( columns[0].getPropertyHolder().getEntityName() )
								.append( "." )
								.append( columns[0].getPropertyName() )
								.append( " " );
					}
				}
				columnsList.append( "referencing " )
						.append( ownerEntity.getEntityName() )
						.append( " not mapped to a single property" );
				throw new AnnotationException( columnsList.toString() );
			}

			/**
			 * creating the property ref to the new synthetic property
			 */
			if ( value instanceof ToOne ) {
				( (ToOne) value ).setReferencedPropertyName( syntheticPropertyName );
				mappings.addUniquePropertyReference( ownerEntity.getEntityName(), syntheticPropertyName );
			}
			else if ( value instanceof Collection ) {
				( (Collection) value ).setReferencedPropertyName( syntheticPropertyName );
				//not unique because we could create a mtm wo association table
				mappings.addPropertyReference( ownerEntity.getEntityName(), syntheticPropertyName );
			}
			else {
				throw new AssertionFailure(
						"Do a property ref on an unexpected Value type: "
								+ value.getClass().getName()
				);
			}
			mappings.addPropertyReferencedAssociation(
					( inverse ? "inverse__" : "" ) + associatedClass.getEntityName(),
					columns[0].getPropertyName(),
					syntheticPropertyName
			);
		}
	}


	private static List<Property> findPropertiesByColumns(
			Object columnOwner, Ejb3JoinColumn[] columns,
			ExtendedMappings mappings
	) {
		Map<Column, Set<Property>> columnsToProperty = new HashMap<Column, Set<Property>>();
		List<Column> orderedColumns = new ArrayList<Column>();
		Table referencedTable = null;
		if ( columnOwner instanceof PersistentClass) {
			referencedTable = ( (PersistentClass) columnOwner ).getTable();
		}
		else if (columnOwner instanceof Join) {
			referencedTable = ( (Join) columnOwner ).getTable();
		}
		else {
			throw new AssertionFailure( columnOwner == null ?
					"columnOwner is null" :
					"columnOwner neither PersistentClass not Join: " + columnOwner.getClass() );
		}
		for ( int index = 0; index < columns.length ; index++ ) {
			Column column = new Column(
					mappings.getPhysicalColumnName( columns[index].getReferencedColumn(), referencedTable )
			);
			orderedColumns.add( column );
			columnsToProperty.put( column, new HashSet<Property>() );
		}
		Iterator it = columnOwner instanceof PersistentClass ?
				( (PersistentClass) columnOwner ).getPropertyIterator() :
				( (Join) columnOwner ).getPropertyIterator();
		while ( it.hasNext() ) {
			Property property = (Property) it.next();
			Iterator columnIt = property.getColumnIterator();
			while ( columnIt.hasNext() ) {
				Column column = (Column) columnIt.next();
				if ( columnsToProperty.containsKey( column ) ) {
					columnsToProperty.get( column ).add( property );
				}
			}
		}
		//first naive implementation
		//only check 1 columns properties
		//TODO make it smarter by checking correctly ordered multi column properties
		List<Property> orderedProperties = new ArrayList<Property>();
		for ( Column column : orderedColumns ) {
			boolean found = false;
			for ( Property property : columnsToProperty.get( column ) ) {
				if ( property.getColumnSpan() == 1 ) {
					orderedProperties.add( property );
					found = true;
					break;
				}
			}
			if ( !found ) return null; //have to find it the hard way
		}
		return orderedProperties;
	}

	public static Property findPropertyByName(PersistentClass associatedClass, String propertyName) {
		Property property = null;
		Property idProperty = associatedClass.getIdentifierProperty();
		String idName = idProperty != null ? idProperty.getName() : null;
		try {
			if ( propertyName == null
					|| propertyName.equals( "" )
					|| propertyName.equals( idName ) ) {
				//default to id
				property = idProperty;
			}
			else {
				if ( propertyName.indexOf( idName + "." ) == 0 ) {
					property = idProperty;
					propertyName = propertyName.substring( idName.length() + 1 );
				}
				StringTokenizer st = new StringTokenizer( propertyName, ".", false );
				while ( st.hasMoreElements() ) {
					String element = (String) st.nextElement();
					if ( property == null ) {
						property = associatedClass.getProperty( element );
					}
					else {
						if ( ! property.isComposite() ) return null;
						property = ( (Component) property.getValue() ).getProperty( element );
					}
				}
			}
		}
		catch (MappingException e) {
			try {
				//if we do not find it try to check the identifier mapper
				if ( associatedClass.getIdentifierMapper() == null ) return null;
				StringTokenizer st = new StringTokenizer( propertyName, ".", false );
				while ( st.hasMoreElements() ) {
					String element = (String) st.nextElement();
					if ( property == null ) {
						property = associatedClass.getIdentifierMapper().getProperty( element );
					}
					else {
						if ( ! property.isComposite() ) return null;
						property = ( (Component) property.getValue() ).getProperty( element );
					}
				}
			}
			catch (MappingException ee) {
				return null;
			}
		}
		return property;
	}

	public static String getRelativePath(PropertyHolder propertyHolder, String propertyName) {
		if (propertyHolder == null) return propertyName;
		String path = propertyHolder.getPath();
		String entityName = propertyHolder.getPersistentClass().getEntityName();
		if ( path.length() == entityName.length() ) {
			return propertyName;
		}
		else {
			return StringHelper.qualify( path.substring( entityName.length() + 1 ), propertyName );
		}
	}

	/**
	 * Find the column owner (ie PersistentClass or Join) of columnName.
	 * If columnName is null or empty, persistentClass is returned
	 */
	public static Object findColumnOwner(PersistentClass persistentClass, String columnName, ExtendedMappings mappings) {
		if ( StringHelper.isEmpty( columnName ) ) return persistentClass; //shortcut for implicit referenced column names
		PersistentClass current = persistentClass;
		Object result = null;
		boolean found = false;
		do {
			result = current;
			Table currentTable = current.getTable();
			try {
				mappings.getPhysicalColumnName( columnName, currentTable );
				found = true;
			}
			catch (MappingException me) {
				//swallow it
			}
			Iterator joins = current.getJoinIterator();
			while ( ! found && joins.hasNext() ) {
				result = joins.next();
				currentTable = ( (Join) result ).getTable();
				try {
					mappings.getPhysicalColumnName( columnName, currentTable );
					found = true;
				}
				catch (MappingException me) {
					//swallow it
				}
			}
			current = current.getSuperclass();
		}
		while (!found && current != null);
		return found ? result : null;
	}
}
