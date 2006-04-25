//$Id: Max.java,v 1.1 2006/04/17 12:11:07 daniel_wiese Exp $
package org.hibernate.validator;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Target;

/**
 * max restriction on a numeric annotated element
 *
 * @author Gavin King
 */
@Documented
@ValidatorClass(MaxValidator.class)
@Target({METHOD, FIELD})
@Retention(RUNTIME)
public @interface Max {
	int value();

	String message() default "{validator.max}";
}
