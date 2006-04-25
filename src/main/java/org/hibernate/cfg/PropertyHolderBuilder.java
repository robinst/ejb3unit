//$Id: PropertyHolderBuilder.java,v 1.1 2006/04/17 12:11:10 daniel_wiese Exp $
package org.hibernate.cfg;

import java.util.Map;

import org.hibernate.mapping.Collection;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.Join;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.reflection.XClass;
import org.hibernate.reflection.XProperty;

/**
 * This factory is here ot build a PropertyHolder and prevent .mapping interface adding
 *
 * @author Emmanuel Bernard
 */
public final class PropertyHolderBuilder {
	private PropertyHolderBuilder() {
	}

	public static PropertyHolder buildPropertyHolder(
			XClass clazzToProcess,
			PersistentClass persistentClass,
			Map<String, Join> joins
	) {
		return (PropertyHolder) new ClassPropertyHolder( persistentClass, clazzToProcess, joins );
	}

	/**
	 * build a component property holder
	 *
	 * @param component component to wrap
	 * @param path	  component path
	 * @return PropertyHolder
	 */
	public static PropertyHolder buildPropertyHolder(
			Component component, String path, PropertyData inferredData, PropertyHolder parent
	) {
		return (PropertyHolder) new ComponentPropertyHolder( component, path, inferredData, parent );
	}

	/**
	 * buid a property holder on top of a collection
	 */
	public static PropertyHolder buildPropertyHolder(Collection collection, String path, XClass clazzToProcess, XProperty property) {
		return new CollectionPropertyHolder( collection, path, clazzToProcess, property);
	}

	public static PropertyHolder buildPropertyHolder(PersistentClass persistentClass, Map<String, Join> joins) {
		return buildPropertyHolder( null, persistentClass,  joins );
	}

}
