package org.jboss.annotation.ejb;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Service annotation from Jboss.
 * @author Daniel Wiese
 * @since 24.02.2007
 */
@Target(TYPE)
@Retention(RUNTIME)
public @interface Service {
	
	/**
	 * The object name.
	 * @author Daniel Wiese
	 * @since 24.02.2007
	 */
	String objectName() default "";

}
