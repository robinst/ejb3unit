//$Id: Min.java,v 1.1 2006/04/17 12:11:07 daniel_wiese Exp $
package org.hibernate.validator;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Target;

/**
 * min restriction on a numeric annotated elemnt (or the string representation of a numeric)
 *
 * @author Gavin King
 */
@Documented
@ValidatorClass(MinValidator.class)
@Target({METHOD, FIELD})
@Retention(RUNTIME)
public @interface Min {
	int value();

	String message() default "{validator.min}";
}
