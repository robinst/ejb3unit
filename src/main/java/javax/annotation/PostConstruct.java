package javax.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Ejb3 spec.
 * @author Daniel Wiese
 * @since 24.03.2007
 */

@Target({ElementType.METHOD}) @Retention(RetentionPolicy.RUNTIME)
public @interface PostConstruct{}