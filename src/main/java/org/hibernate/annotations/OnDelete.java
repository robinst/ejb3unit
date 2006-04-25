package org.hibernate.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;


/**
 * Strategy to use on collections, arrays and on joined subclasses delete
 * OnDelete of secondary tables currently not supported.
 * @author Emmanuel Bernard
 */
@Target({METHOD, FIELD, TYPE}) @Retention(RUNTIME)
public @interface OnDelete {
	OnDeleteAction action();
}
