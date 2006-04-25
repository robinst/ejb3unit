package org.hibernate.reflection.java;

import java.beans.Introspector;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;

import org.hibernate.reflection.Filter;
import org.hibernate.reflection.XProperty;
import org.hibernate.reflection.java.generics.TypeEnvironment;
import org.hibernate.reflection.java.generics.TypeUtils;

/**
 * @author Paolo Perrotta
 * @author Davide Marchignoli
 */
class JavaXProperty extends JavaXMember implements XProperty {

	static boolean isProperty(Field f, Type boundType, Filter filter) {
		return isPropertyType( boundType )
				&& !f.isSynthetic()
				&& ( filter.returnStatic() || ! Modifier.isStatic( f.getModifiers() ) )
				&& ( filter.returnTransient() || ! Modifier.isTransient( f.getModifiers() ) );
	}

	private static boolean isPropertyType(Type type) {
//		return TypeUtils.isArray( type ) || TypeUtils.isCollection( type ) || ( TypeUtils.isBase( type ) && ! TypeUtils.isVoid( type ) );
		return !TypeUtils.isVoid( type );
	}

	static boolean isProperty(Method m, Type boundType, Filter filter) {
		return isPropertyType( boundType )
				&&!m.isSynthetic()
				&& !m.isBridge()
				&& ( filter.returnStatic() || !Modifier.isStatic( m.getModifiers() ) )
				&& m.getParameterTypes().length == 0
				&& ( m.getName().startsWith( "get" ) || m.getName().startsWith( "is" ) );
		// TODO should we use stronger checking on the naming of getters/setters, or just leave this to the validator?
	}

	static JavaXProperty create(Member member, final TypeEnvironment context, final JavaXFactory factory) {
		final Type propType = typeOf( member, context );
		JavaXType xType = factory.toXType( context, propType );
		return new JavaXProperty( member, propType, context, factory, xType );
	}

	private JavaXProperty(Member member, Type type, TypeEnvironment env, JavaXFactory factory, JavaXType xType) {
		super(member, type, env, factory, xType);
		assert member instanceof Field || member instanceof Method;
	}

	public String getName() {
		String fullName = getMember().getName();
		if ( getMember() instanceof Method ) {
			if ( fullName.startsWith( "get" ) )
				return Introspector.decapitalize( fullName.substring( "get".length() ) );
			if ( fullName.startsWith( "is" ) )
				return Introspector.decapitalize( fullName.substring( "is".length() ) );
			throw new RuntimeException( "Method " + fullName + " is not a property getter" );
		}
		else
			return fullName;
	}

	public Object invoke(Object target, Object... parameters) {
		if (parameters.length != 0) throw new IllegalArgumentException("An XProperty cannot have invoke parameters");
		try {
			if (getMember() instanceof Method) {
				return ( (Method) getMember() ).invoke( target );
			}
			else {
				return ( (Field) getMember() ).get( target );
			}
		}
		catch (NullPointerException e) {
			throw new IllegalArgumentException( "Invoking " + getName() + " on a  null object", e );
		}
		catch (IllegalArgumentException e) {
			throw new IllegalArgumentException( "Invoking " + getName() + " with wrong parameters", e );
		}
		catch (Exception e) {
			throw new IllegalStateException( "Unable to invoke " + getName(), e );
		}
	}
	
	@Override
	public String toString() {
		return getName();
	}
}