//$Id: Sort.java,v 1.1 2006/04/17 12:11:07 daniel_wiese Exp $
package org.hibernate.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Collection sort
 * (Java level sorting)
 * @author Emmanuel Bernard
 */
@Target({METHOD, FIELD}) @Retention(RUNTIME)
public @interface Sort {
	/**
	 * sort type
	 */
	SortType type() default SortType.UNSORTED;
	/**
	 * Sort comparator implementation
	 */
	//TODO find a way to use Class<Comparator>
	Class comparator() default void.class;
}
