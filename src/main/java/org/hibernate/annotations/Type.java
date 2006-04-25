//$Id: Type.java,v 1.1 2006/04/17 12:11:07 daniel_wiese Exp $
package org.hibernate.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Target;
import java.lang.annotation.Retention;

/**
 * hibernate type
 * @author Emmanuel Bernard
 */
@Target({FIELD,METHOD}) @Retention(RUNTIME)
public @interface Type {
	String type();
	Parameter[] parameters() default {};
}