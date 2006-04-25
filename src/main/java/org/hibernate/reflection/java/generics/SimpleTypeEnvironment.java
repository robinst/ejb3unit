package org.hibernate.reflection.java.generics;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;

/**
 * @author Davide Marchignoli
 * @author Paolo Perrotta
 */
class SimpleTypeEnvironment implements TypeEnvironment {

	final Type[] formalArguments;

	final Type[] actualArguments;

	private final TypeSwitch<Type> substitute = new TypeSwitch<Type>() {
		@Override
		public Type caseClass(Class classType) {
			return classType;
		}

		@Override
		public Type caseGenericArrayType(GenericArrayType genericArrayType) {
			Type originalComponentType = genericArrayType.getGenericComponentType();
			Type boundComponentType = bind( originalComponentType );
			// try to keep the original type if possible
			if( originalComponentType == boundComponentType )
				return genericArrayType;
			return TypeFactory.createArrayType( boundComponentType );
		}

		@Override
		public Type caseParameterizedType(ParameterizedType parameterizedType) {
			Type[] originalArguments = parameterizedType.getActualTypeArguments();
			Type[] boundArguments = substitute( originalArguments );
			// try to keep the original type if possible
			if( areSame( originalArguments, boundArguments ) )
				return parameterizedType;
			return TypeFactory.createParameterizedType( parameterizedType.getRawType(), boundArguments, parameterizedType.getOwnerType() );
		}

		private boolean areSame(Object[] array1, Object[] array2) {
			if( array1.length != array2.length )
				return false;
			for ( int i = 0; i < array1.length; i++ ) {
				if( array1[i] != array2[i] )
					return false;
			}
			return true;
		}

		@Override
		public Type caseTypeVariable(TypeVariable typeVariable) {
			int idx = indexOf( formalArguments, typeVariable );
			return ( idx >= 0 ) ? actualArguments[idx] : typeVariable;
		}

		private int indexOf(Object[] array, Object o) {
			for ( int i = 0; i < array.length; i++ )
				if ( array[i].equals( o ) )
					return i;
			return -1;
		}

		@Override
		public Type caseWildcardType(WildcardType wildcardType) {
			return wildcardType;
		}
	};

	public SimpleTypeEnvironment( Type[] formal, Type[] actual ) {
		actualArguments = actual;
		formalArguments = formal;
	}

	public Type bind(Type type) {
		return substitute.doSwitch( type );
	}

	private Type[] substitute(Type[] types) {
		Type[] substTypes = new Type[types.length];
		for ( int i = 0; i < substTypes.length; i++ )
			substTypes[i] = bind( types[i] );
		return substTypes;
	}
}
