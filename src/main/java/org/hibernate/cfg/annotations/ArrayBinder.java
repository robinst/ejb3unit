package org.hibernate.cfg.annotations;

import javax.persistence.FetchType;

import org.hibernate.FetchMode;
import org.hibernate.mapping.Array;
import org.hibernate.mapping.Collection;
import org.hibernate.mapping.PersistentClass;

/**
 * Bind an Array
 *
 * @author Anthony Patricio
 */
public class ArrayBinder extends ListBinder {

	public ArrayBinder() {
	}

	public void setFetchType(FetchType fetch) {
		//workaround to hibernate3 bug, remove it once fixed
		fetchMode = FetchMode.SELECT;
	}

	protected Collection createCollection(PersistentClass persistentClass) {
		return new Array( persistentClass );
	}
}
