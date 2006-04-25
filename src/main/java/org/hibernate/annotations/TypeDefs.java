//$Id: TypeDefs.java,v 1.1 2006/04/17 12:11:07 daniel_wiese Exp $
package org.hibernate.annotations;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.ElementType.PACKAGE;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Target;
import java.lang.annotation.Retention;

/**
 * Type definition array
 * @author Emmanuel Bernard
 */
@Target({TYPE, PACKAGE}) @Retention(RUNTIME)
public @interface TypeDefs {
	TypeDef[] value();
}
