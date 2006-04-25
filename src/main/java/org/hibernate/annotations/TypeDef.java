//$Id: TypeDef.java,v 1.1 2006/04/17 12:11:07 daniel_wiese Exp $
package org.hibernate.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.ElementType.PACKAGE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Type definition
 *
 * @author Emmanuel Bernard
 */
@Target({TYPE,PACKAGE}) @Retention(RUNTIME)
public @interface TypeDef {
	String  name();
	Class typeClass();
	Parameter[] parameters() default {};
}
