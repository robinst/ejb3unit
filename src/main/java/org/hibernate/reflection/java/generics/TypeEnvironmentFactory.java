package org.hibernate.reflection.java.generics;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.HashMap;
import java.util.Map;

/**
 * Returns the type context for a given <code>Class</code> or <code>ParameterizedType</code>.
 * <p>
 * Does not support bindings involving inner classes, nor upper/lower bounds.
 * 
 * @author Davide Marchignoli
 * @author Paolo Perrotta
 */
public class TypeEnvironmentFactory {

	private final Map<Type, TypeEnvironment> typeToEnvironment = new HashMap<Type, TypeEnvironment>();

	/**
	 * @return Returns a type environment suitable for resolving types occurring
	 *         in subclasses of the context class.
	 */
	public TypeEnvironment getEnvironment(Class context) {
		return doGetEnvironment( context );
	}

	public TypeEnvironment getEnvironment(ParameterizedType context) {
		return doGetEnvironment( context );
	}

	public TypeEnvironment toApproximatingEnvironment(TypeEnvironment context) {
		return new CompoundTypeEnvironment( new ApproximatingTypeEnvironment(), context );
	}

	private TypeEnvironment doGetEnvironment(Type context) {
		if ( context == null )
			return IdentityTypeEnvironment.INSTANCE;
		TypeEnvironment result = typeToEnvironment.get( context );
		if ( result == null ) {
			result = createEnvironment( context );
			typeToEnvironment.put( context, result );
		}
		return result;
	}

	private TypeEnvironment createEnvironment(Type context) {
		return new TypeSwitch<TypeEnvironment>() {
			@Override
			public TypeEnvironment caseClass(Class classType) {
				return new CompoundTypeEnvironment( createSuperTypeEnvironment( classType ),
						getEnvironment( classType.getSuperclass() ) );
			}

			@Override
			public TypeEnvironment caseParameterizedType(ParameterizedType parameterizedType) {
				return createEnvironment( parameterizedType );
			}

			@Override
			public TypeEnvironment defaultCase(Type t) {
				throw new IllegalArgumentException( "Invalid type for generating environment: " + t );
			}
		}.doSwitch( context );
	}

	private TypeEnvironment createSuperTypeEnvironment(Class clazz) {
		Class superclass = clazz.getSuperclass();
		if ( superclass == null )
			return IdentityTypeEnvironment.INSTANCE;

		Type[] formalArgs = superclass.getTypeParameters();
		Type genericSuperclass = clazz.getGenericSuperclass();

		if ( genericSuperclass instanceof Class )
			return IdentityTypeEnvironment.INSTANCE;

		if ( genericSuperclass instanceof ParameterizedType ) {
			Type[] actualArgs = ( (ParameterizedType) genericSuperclass ).getActualTypeArguments();
			return new SimpleTypeEnvironment( formalArgs, actualArgs );
		}

		throw new AssertionError( "Should be unreachable" );
	}

	private TypeEnvironment createEnvironment(ParameterizedType t) {
		Type[] tactuals = t.getActualTypeArguments();
		Type rawType = t.getRawType();
		if ( rawType instanceof Class ) {
			TypeVariable[] tparms = ( (Class) rawType ).getTypeParameters();
			return new SimpleTypeEnvironment( tparms, tactuals );
		}
		return IdentityTypeEnvironment.INSTANCE;
	}
}
