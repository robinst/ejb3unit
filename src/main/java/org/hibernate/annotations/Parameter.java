//$Id: Parameter.java,v 1.1 2006/04/17 12:11:07 daniel_wiese Exp $
package org.hibernate.annotations;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Target;
import java.lang.annotation.Retention;

/**
 * Parameter (basically key/value pattern)
 * 
 * @author Emmanuel Bernard
 */
@Target({}) @Retention(RUNTIME)
public @interface Parameter {
	String name();
	String value();
}
