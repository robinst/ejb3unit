//$Id: PrimitiveArrayBinder.java,v 1.1 2006/04/17 12:11:11 daniel_wiese Exp $
package org.hibernate.cfg.annotations;

import org.hibernate.mapping.Collection;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.PrimitiveArray;

/**
 * @author Emmanuel Bernard
 */
public class PrimitiveArrayBinder extends ArrayBinder {
	@Override
	protected Collection createCollection(PersistentClass persistentClass) {
		return new PrimitiveArray( persistentClass );
	}
}
