//$Id: MapBinder.java,v 1.1 2006/04/17 12:11:11 daniel_wiese Exp $
package org.hibernate.cfg.annotations;

import java.util.Iterator;
import java.util.Map;

import org.hibernate.AnnotationException;
import org.hibernate.AssertionFailure;
import org.hibernate.FetchMode;
import org.hibernate.MappingException;
import org.hibernate.cfg.BinderHelper;
import org.hibernate.cfg.CollectionSecondPass;
import org.hibernate.cfg.Ejb3Column;
import org.hibernate.cfg.Ejb3JoinColumn;
import org.hibernate.cfg.ExtendedMappings;
import org.hibernate.cfg.SecondPass;
import org.hibernate.mapping.Collection;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.Formula;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.SimpleValue;
import org.hibernate.mapping.Value;
import org.hibernate.reflection.XProperty;

/**
 * Implementation to bind a Map
 *
 * @author Emmanuel Bernard
 */
public class MapBinder extends CollectionBinder {

	protected Collection createCollection(PersistentClass persistentClass) {
		return new org.hibernate.mapping.Map( persistentClass );
	}

	@Override
	public SecondPass getSecondPass(
			final Ejb3JoinColumn[] fkJoinColumns, final Ejb3JoinColumn[] keyColumns,
			final Ejb3JoinColumn[] inverseColumns,
			final Ejb3Column[] elementColumns,
			final boolean isEmbedded,
			final XProperty property, final String collType,
			final FetchMode fetchMode, final boolean ignoreNotFound, final boolean unique,
			final TableBinder assocTableBinder, final ExtendedMappings mappings
	) {
		return new CollectionSecondPass( mappings, MapBinder.this.collection ) {
			public void secondPass(Map persistentClasses, Map inheritedMetas)
					throws MappingException {
				bindStarToManySecondPass(
						persistentClasses, collType, fkJoinColumns, keyColumns, inverseColumns, elementColumns,
						isEmbedded, property, fetchMode, unique, assocTableBinder, ignoreNotFound, mappings
				);
				bindKeyFromAssociationTable( collType, persistentClasses, mapKeyPropertyName, mappings );
			}
		};
	}

	private void bindKeyFromAssociationTable(
			String collType, Map persistentClasses, String mapKeyPropertyName, ExtendedMappings mappings
	) {
		if ( mapKeyPropertyName == null ) throw new AnnotationException( "A Map must declare a @MapKey element" );
		PersistentClass associatedClass = (PersistentClass) persistentClasses.get( collType );
		if ( associatedClass == null ) throw new AnnotationException( "Associated class not found: " + collType );
		Property property = BinderHelper.findPropertyByName( associatedClass, mapKeyPropertyName );
		if ( property == null ) {
			throw new AnnotationException(
					"Map key property not found: " + collType + "." + mapKeyPropertyName
			);
		}
		org.hibernate.mapping.Map map = (org.hibernate.mapping.Map) this.collection;
		Value indexValue = createFormulatedValue( property.getValue(), map );
		map.setIndex( indexValue );
	}

	protected Value createFormulatedValue(Value value, Collection collection) {
		if ( value instanceof Component ) {
			Component component = (Component) value;
			Iterator properties = component.getPropertyIterator();
			Component indexComponent = new Component( collection );
			indexComponent.setComponentClassName( component.getComponentClassName() );
			while ( properties.hasNext() ) {
				Property current = (Property) properties.next();
				Property newProperty = new Property();
				newProperty.setCascade( current.getCascade() );
				newProperty.setGeneration( current.getGeneration() );
				newProperty.setInsertable( false );
				newProperty.setUpdateable( false );
				newProperty.setMetaAttributes( current.getMetaAttributes() );
				newProperty.setName( current.getName() );
				newProperty.setNaturalIdentifier( false );
				//newProperty.setOptimisticLocked( false );
				newProperty.setOptional( false );
				newProperty.setPersistentClass( current.getPersistentClass() );
				newProperty.setPropertyAccessorName( current.getPropertyAccessorName() );
				newProperty.setSelectable( current.isSelectable() );
				newProperty.setValue( createFormulatedValue( current.getValue(), collection ) );
				indexComponent.addProperty( newProperty );
			}
			return indexComponent;
		}
		else {
			SimpleValue sourceValue = (SimpleValue) value;
			SimpleValue targetValue = new SimpleValue( collection.getCollectionTable() );
			targetValue.setTypeName( sourceValue.getTypeName() );
			targetValue.setTypeParameters( sourceValue.getTypeParameters() );
			Iterator columns = sourceValue.getColumnIterator();
			while ( columns.hasNext() ) {
				Object current = columns.next();
				Formula formula = new Formula();
				if ( current instanceof Column ) {
					formula.setFormula( ( (Column) current ).getName() );
					//FIXME support quoted name
				}
				else if ( current instanceof Formula ) {
					formula.setFormula( ( (Formula) current ).getFormula() );
				}
				else {
					throw new AssertionFailure( "Unknown element in column iterator: " + current.getClass() );
				}
				targetValue.addFormula( formula );

			}
			return targetValue;
		}
	}
}
