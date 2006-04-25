//$Id: JavaXMethod.java,v 1.1 2006/04/17 12:11:12 daniel_wiese Exp $
package org.hibernate.reflection.java;

import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

import org.hibernate.reflection.XMethod;
import org.hibernate.reflection.java.generics.TypeEnvironment;

/**
 * @author Emmanuel Bernard
 */
public class JavaXMethod extends JavaXMember implements XMethod {

	static JavaXMethod create(Member member, TypeEnvironment context, JavaXFactory factory) {
		final Type propType = typeOf( member, context );
		JavaXType xType = factory.toXType( context, propType );
		return new JavaXMethod( member, propType, context, factory, xType);
	}

	private JavaXMethod(Member member, Type type, TypeEnvironment env, JavaXFactory factory, JavaXType xType) {
		super(member, type, env, factory, xType);
		assert member instanceof Method;
	}

	public String getName() {
		return getMember().getName();
	}

	public Object invoke(Object target, Object... parameters) {
		try {
			return ( (Method) getMember() ).invoke( target, parameters );
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
}
