//$Id: ToOneMappedBySecondPass.java,v 1.1 2006/04/17 12:11:10 daniel_wiese Exp $
package org.hibernate.cfg;

import java.util.Map;

import org.hibernate.AnnotationException;
import org.hibernate.MappingException;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.ToOne;
import org.hibernate.mapping.ManyToOne;
import org.hibernate.mapping.OneToOne;
import org.hibernate.util.StringHelper;

public class ToOneMappedBySecondPass implements SecondPass {
	private String mappedBy;
	private ToOne value;
	private Mappings mappings;
	private String ownerEntity;
	private String ownerProperty;

	public ToOneMappedBySecondPass(String mappedBy, ToOne value, String ownerEntity, String ownerProperty, Mappings mappings) {
		this.ownerEntity = ownerEntity;
		this.ownerProperty = ownerProperty;
		this.mappedBy = mappedBy;
		this.value = value;
		this.mappings = mappings;
	}

	public void doSecondPass(Map persistentClasses, Map inheritedMetas) throws MappingException {
		PersistentClass otherSide = (PersistentClass) persistentClasses.get( value.getReferencedEntityName() );
		Property property;
		try {
			if (otherSide == null) throw new MappingException("Unable to find entity: " + value.getReferencedEntityName() );
			property = otherSide.getProperty( mappedBy );
		}
		catch (MappingException e) {
			throw new AnnotationException("Unknown mappedBy in: " + StringHelper.qualify(ownerEntity, ownerProperty)
					+ ", referenced property unknown: "
					+ StringHelper.qualify( value.getReferencedEntityName(), mappedBy )
			);
		}
		if ( property.getValue() instanceof OneToOne ) {
			//do nothing
		}
		else if ( property.getValue() instanceof ManyToOne ) {
			value.setReferencedPropertyName( mappedBy );

			String propertyRef = value.getReferencedPropertyName();
			if ( propertyRef != null ) {
				mappings.addUniquePropertyReference(
						value.getReferencedEntityName(),
						propertyRef
				);
			}
		}
		else {
			throw new AnnotationException(
					"Referenced property not a (One|Many)ToOne: "
					+ StringHelper.qualify( value.getReferencedEntityName(), value.getReferencedPropertyName() )
					+ " in mappedBy of "
					+ StringHelper.qualify(ownerEntity, ownerProperty)
			);
		}
	}
}
