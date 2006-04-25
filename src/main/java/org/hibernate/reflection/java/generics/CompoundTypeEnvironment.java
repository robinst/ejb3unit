package org.hibernate.reflection.java.generics;

import java.lang.reflect.Type;

/**
 * A composition of two <code>TypeEnvironment</code> functions.
 * 
 * @author Davide Marchignoli
 * @author Paolo Perrotta
 */
class CompoundTypeEnvironment implements TypeEnvironment {

	private final TypeEnvironment f;

	private final TypeEnvironment g;

	public CompoundTypeEnvironment( TypeEnvironment f, TypeEnvironment g ) {
		this.f = f;
		this.g = g;
	}

	public Type bind(Type type) {
		return f.bind( g.bind( type ) );
	}
}
