//$Id: Size.java,v 1.1 2006/04/17 12:11:07 daniel_wiese Exp $
package org.hibernate.validator;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Target;

/**
 * Size range for Arrays, Collections or Maps
 *
 * @author Gavin King
 */
@Documented
@ValidatorClass(SizeValidator.class)
@Target({METHOD, FIELD})
@Retention(RUNTIME)
public @interface Size {
	int max() default Integer.MAX_VALUE;

	int min() default 0;

	String message() default "{validator.size}";
}
