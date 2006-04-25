//$Id: CollectionPropertyHolder.java,v 1.1 2006/04/17 12:11:10 daniel_wiese Exp $
package org.hibernate.cfg;

import org.hibernate.AssertionFailure;
import org.hibernate.mapping.Collection;
import org.hibernate.mapping.KeyValue;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.Table;
import org.hibernate.reflection.XClass;
import org.hibernate.reflection.XProperty;

/**
 * @author Emmanuel Bernard
 */
public class CollectionPropertyHolder extends AbstractPropertyHolder {
	Collection collection;

	public CollectionPropertyHolder(Collection collection, String path, XClass clazzToProcess, XProperty property) {
		super( path, null, clazzToProcess );
		this.collection = collection;
		setCurrentProperty( property );
	}

	public String getClassName() {
		throw new AssertionFailure( "Collection property holder does not have a class name" );
	}

	public Table getTable() {
		return collection.getCollectionTable();
	}

	public void addProperty(Property prop) {
		throw new AssertionFailure( "Cannot add property to a collection" );
	}

	public KeyValue getIdentifier() {
		throw new AssertionFailure( "Identifier collection not yet managed" );
	}

	public PersistentClass getPersistentClass() {
		return collection.getOwner();
	}

	public boolean isComponent() {
		return false;
	}

	public String getEntityName() {
		return collection.getOwner().getEntityName();
	}

	public void addProperty(Property prop, Ejb3Column[] columns) {
		//Ejb3Column.checkPropertyConsistency( ); //already called earlier
		throw new AssertionFailure("addProperty to a join table of a collection: does it make sense?");
	}
}
