//$Id: SimpleValueBinder.java,v 1.1 2006/04/17 12:11:11 daniel_wiese Exp $
package org.hibernate.cfg.annotations;

import java.io.Serializable;
import java.sql.Types;
import java.util.Properties;
import java.util.Date;
import java.util.Calendar;

import javax.persistence.Enumerated;
import javax.persistence.Lob;
import javax.persistence.Temporal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.AssertionFailure;
import org.hibernate.AnnotationException;
import org.hibernate.util.StringHelper;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.hibernate.cfg.AnnotationBinder;
import org.hibernate.cfg.Ejb3Column;
import org.hibernate.cfg.Mappings;
import org.hibernate.cfg.NotYetImplementedException;
import org.hibernate.mapping.SimpleValue;
import org.hibernate.mapping.Table;
import org.hibernate.reflection.ReflectionManager;
import org.hibernate.reflection.XClass;
import org.hibernate.reflection.XProperty;
import org.hibernate.type.ByteArrayBlobType;
import org.hibernate.type.CharacterArrayClobType;
import org.hibernate.type.EnumType;
import org.hibernate.type.PrimitiveByteArrayBlobType;
import org.hibernate.type.PrimitiveCharacterArrayClobType;
import org.hibernate.type.SerializableToBlobType;
import org.hibernate.type.StringClobType;

/**
 * @author Emmanuel Bernard
 */
public class SimpleValueBinder {
	private static Log log = LogFactory.getLog( SimpleValueBinder.class );
	private String propertyName;
	private String returnedClassName;
	private Ejb3Column[] columns;
	private String persistentClassName;
	private String explicitType = "";
	private Properties typeParameters = new Properties();
	private Mappings mappings;

	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

	public void setReturnedClassName(String returnedClassName) {
		this.returnedClassName = returnedClassName;
	}

	public void setColumns(Ejb3Column[] columns) {
		this.columns = columns;
	}


	public void setPersistentClassName(String persistentClassName) {
		this.persistentClassName = persistentClassName;
	}

	//TODO execute it lazily to be order safe
	public void setType(XProperty property, XClass returnedClass) {
		if ( returnedClass == null ) return; //we cannot guess anything
		XClass returnedClassOrElement = returnedClass;
		boolean isArray = false;
		if ( property.isArray() ) {
			returnedClassOrElement = property.getElementClass();
			isArray = true;
		}
		Properties typeParameters = this.typeParameters;
		typeParameters.clear();
		String type = AnnotationBinder.ANNOTATION_STRING_DEFAULT;
		if ( property.isAnnotationPresent( Temporal.class ) ) {
			Temporal ann = property.getAnnotation( Temporal.class );
			boolean isDate;
			if ( ReflectionManager.INSTANCE.equals( property.getType(), Date.class ) ) {
				isDate = true;
			}
			else if ( ReflectionManager.INSTANCE.equals( property.getType(), Calendar.class ) ) {
				isDate = false;
			}
			else {
				throw new AnnotationException(
						"@Temporal should be set on a java.util.Date or java.util.Calendar property: "
								+ StringHelper.qualify( persistentClassName, propertyName )
				);
			}

			switch ( ann.value() ) {
				case DATE:
					type = isDate ? "date" : "calendar_date";
					break;
				case TIME:
					type = "time";
					if ( ! isDate ) {
						throw new NotYetImplementedException( "Calendar cannot persist TIME only"
							+ StringHelper.qualify( persistentClassName, propertyName ) );
					}
					break;
				case TIMESTAMP:
					type = isDate ? "timestamp" : "calendar";
					break;
				default:
					throw new AssertionFailure( "Unknown temporal type: " + ann.value() );
			}
		}
		else if ( property.isAnnotationPresent( Lob.class ) ) {

			if ( ReflectionManager.INSTANCE.equals( returnedClassOrElement, java.sql.Clob.class ) ) {
				type = "clob";
			}
			else if ( ReflectionManager.INSTANCE.equals( returnedClassOrElement, java.sql.Blob.class ) ) {
				type = "blob";
			}
			else if ( ReflectionManager.INSTANCE.equals( returnedClassOrElement, String.class ) ) {
				type = StringClobType.class.getName();
			}
			else if ( ReflectionManager.INSTANCE.equals( returnedClassOrElement, Character.class ) && isArray ) {
				type = CharacterArrayClobType.class.getName();
			}
			else if ( ReflectionManager.INSTANCE.equals( returnedClassOrElement, char.class ) && isArray ) {
				type = PrimitiveCharacterArrayClobType.class.getName();
			} else if ( ReflectionManager.INSTANCE.equals( returnedClassOrElement, Byte.class ) && isArray ) {
				type = ByteArrayBlobType.class.getName();
			}
			else if ( ReflectionManager.INSTANCE.equals( returnedClassOrElement, byte.class ) && isArray ) {
				type = PrimitiveByteArrayBlobType.class.getName();
			}
			else if ( ReflectionManager.INSTANCE.toXClass( Serializable.class ).isAssignableFrom( returnedClassOrElement ) ) {
				type = SerializableToBlobType.class.getName();
				//typeParameters = new Properties();
				typeParameters.setProperty(
						SerializableToBlobType.CLASS_NAME,
						returnedClassOrElement.getName()
				);
			}
			else {
				type = "blob";
			}
		}
		//implicit type will check basic types and Serializable classes
		if ( columns == null ) {
			throw new AssertionFailure( "SimpleValueBinder.setColumns should be set before SimpleValueBinder.setType" );
		}
		if ( AnnotationBinder.ANNOTATION_STRING_DEFAULT.equals( type ) ) {
			if ( returnedClassOrElement.isEnum() ) {
				type = EnumType.class.getName();
				typeParameters = new Properties();
				typeParameters.setProperty( EnumType.ENUM, returnedClassOrElement.getName() );
				String schema = columns[0].getTable().getSchema();
				schema = schema == null ? "" : schema;
				String catalog = columns[0].getTable().getCatalog();
				catalog = catalog == null ? "" : catalog;
				typeParameters.setProperty( EnumType.SCHEMA, schema );
				typeParameters.setProperty( EnumType.CATALOG, catalog );
				typeParameters.setProperty( EnumType.TABLE, columns[0].getTable().getName() );
				typeParameters.setProperty( EnumType.COLUMN, columns[0].getName() );
				Enumerated enumAnn = property.getAnnotation( Enumerated.class );
				if (enumAnn != null) {
					javax.persistence.EnumType enumType = enumAnn.value();
					if ( javax.persistence.EnumType.ORDINAL.equals( enumType ) ) {
						typeParameters.setProperty( EnumType.TYPE, String.valueOf( Types.INTEGER ) );
					}
					else if ( javax.persistence.EnumType.STRING.equals( enumType ) ) {
						typeParameters.setProperty( EnumType.TYPE, String.valueOf( Types.VARCHAR ) );
					}
					else {
						throw new AssertionFailure( "Unknown EnumType: " + enumType );
					}
				}
			}
		}
		explicitType = type;
		this.typeParameters = typeParameters;
		Type annType = (Type) property.getAnnotation( Type.class );
		setExplicitType( annType );
	}

	public void setExplicitType(String explicitType) {
		this.explicitType = explicitType;
	}

	//FIXME raise an assertion failure  if setExplicitType(String) and setExplicitType(Type) are use at the same time
	public void setExplicitType(Type typeAnn) {
		if ( typeAnn != null ) {
			explicitType = typeAnn.type();
			typeParameters.clear();
			for ( Parameter param : typeAnn.parameters() ) {
				typeParameters.setProperty( param.name(), param.value() );
			}
		}
	}

	public void setMappings(Mappings mappings) {
		this.mappings = mappings;
	}

	private void validate() {
		//TODO check necessary params
		Ejb3Column.checkPropertyConsistency( columns, propertyName );
	}

	public SimpleValue make() {
		validate();
		log.debug( "building SimpleValue for " + propertyName );
		Table table = columns[0].getTable();
		SimpleValue simpleValue = new SimpleValue( table );
		return fillSimpleValue( simpleValue );
	}

	private SimpleValue fillSimpleValue(SimpleValue simpleValue) {
		String type = AnnotationBinder.isDefault( explicitType ) ? returnedClassName : explicitType;
		org.hibernate.mapping.TypeDef typeDef = mappings.getTypeDef( type );
		if ( typeDef != null ) {
			type = typeDef.getTypeClass();
			simpleValue.setTypeParameters( typeDef.getParameters() );
		}
		if ( typeParameters != null && typeParameters.size() != 0 ) {
			//explicit type params takes precedence over type def params
			simpleValue.setTypeParameters( typeParameters );
		}
		simpleValue.setTypeName( type );
		if ( persistentClassName != null ) {
			simpleValue.setTypeUsingReflection( persistentClassName, propertyName );
		}
		for ( Ejb3Column column : columns ) {
			column.linkWithValue( simpleValue );
		}
		return simpleValue;
	}
}
