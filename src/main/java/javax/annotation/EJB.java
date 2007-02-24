package javax.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * EJB 3 Annotaion.
 * @see <a href="http://www.jcp.org/en/jsr/detail?id=220">EJB 3.0 specification</a>
 * @author Daniel Wiese
 */
@Target( { TYPE, METHOD, FIELD })
@Retention(RUNTIME)
public @interface EJB {
	String name() default "";

	Class beanInterface() default Object.class;

	String beanName() default "";

	String mappedName() default "";

	String description() default "";
}
