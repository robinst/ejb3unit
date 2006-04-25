// $Id: FkSecondPass.java,v 1.1 2006/04/17 12:11:10 daniel_wiese Exp $
package org.hibernate.cfg;

import org.hibernate.AnnotationException;
import org.hibernate.AssertionFailure;
import org.hibernate.MappingException;
import org.hibernate.cfg.annotations.TableBinder;
import org.hibernate.mapping.ManyToOne;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Value;

/**
 * Enable a proper set of the FK columns in respect with the id column order
 * Allow the correct implementation of the default EJB3 values which needs both
 * sides of the association to be resolved
 *
 * @author Emmanuel Bernard
 */
public class FkSecondPass implements SecondPass {
	private Value value;
	private Ejb3JoinColumn[] columns;
	private boolean unique;
	private ExtendedMappings mappings;
	private String path;

	FkSecondPass(Value value, Ejb3JoinColumn[] columns, boolean unique, String path, ExtendedMappings mappings) {
		this.mappings = mappings;
		this.value = value;
		this.columns = columns;
		this.unique = unique;
		this.path = path;
	}

	public void doSecondPass(java.util.Map persistentClasses, java.util.Map inheritedMetas) throws MappingException {
		if ( value instanceof ManyToOne ) {
			ManyToOne manyToOne = (ManyToOne) value;
			PersistentClass ref = (PersistentClass) persistentClasses.get( manyToOne.getReferencedEntityName() );
			if ( ref == null ) {
				throw new AnnotationException(
						"@OneToOne or @ManyToOne on " + path +  " references an unknown entity: " + manyToOne.getReferencedEntityName()
				);
			}
			BinderHelper.createSyntheticPropertyReference( columns, ref, null, manyToOne, false, mappings );
			TableBinder.bindFk( ref, null, columns, manyToOne, unique, mappings );
			/*
			 * HbmBinder does this only when property-ref != null, but IMO, it makes sense event if it is null
			 */
			if ( ! manyToOne.isIgnoreNotFound() ) manyToOne.createPropertyRefConstraints( persistentClasses );
		}
		else {
			throw new AssertionFailure( "FkSecondPass for a wrong value type: " + value.getClass().getName() );
		}
	}
}
