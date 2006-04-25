//$Id: ParamDef.java,v 1.1 2006/04/17 12:11:07 daniel_wiese Exp $
package org.hibernate.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * A parameter definition
 *
 * @author Emmanuel Bernard
 */
@Target({}) @Retention(RUNTIME)
public @interface ParamDef {
	String name();
	String type();
}
