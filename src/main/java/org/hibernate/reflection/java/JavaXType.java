//$Id: JavaXType.java,v 1.1 2006/04/17 12:11:12 daniel_wiese Exp $
package org.hibernate.reflection.java;

import java.lang.reflect.Type;
import java.util.Collection;

import org.hibernate.reflection.XClass;
import org.hibernate.reflection.java.generics.TypeEnvironment;
import org.hibernate.reflection.java.generics.TypeUtils;

/**
 * The Java X-layer equivalent to a Java <code>Type</code>.
 * 
 * @author Emmanuel Bernard
 * @author Paolo Perrotta
 */
abstract class JavaXType {
	
	private final TypeEnvironment context;
	private final JavaXFactory factory;
	private final Type approximatedType;
	private final Type boundType;
	
	protected JavaXType( Type unboundType, TypeEnvironment context, JavaXFactory factory ) {
		this.context = context;
		this.factory = factory;
		this.boundType = context.bind( unboundType );
		this.approximatedType = factory.toApproximatingEnvironment( context ).bind( unboundType );
	}
	
	abstract public boolean isArray();
	abstract public boolean isCollection();
	abstract public XClass getElementClass();
	abstract public XClass getClassOrElementClass();
	abstract public Class<? extends Collection> getCollectionClass();
	abstract public XClass getMapKey();
	abstract public XClass getType();

	public boolean isResolved() {
		return TypeUtils.isResolved( boundType );
	}

	protected Type approximate() {
		return approximatedType;
	}
	
	protected XClass toXClass(Type type) {
		return factory.toXClass( type, context );
	}
}
