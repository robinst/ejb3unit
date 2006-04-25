//$Id: CollectionOfElements.java,v 1.1 2006/04/17 12:11:07 daniel_wiese Exp $
package org.hibernate.annotations;

import java.lang.annotation.Target;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.FIELD;
import static javax.persistence.FetchType.LAZY;
import javax.persistence.FetchType;

/**
 * Annotation used to mark a collection as a collection of elements or
 * a collection of embedded objects
 * @author Emmanuel Bernard
 */
@Target({METHOD, FIELD}) @Retention(RUNTIME)
public @interface CollectionOfElements {
	/**
	 * Represent the element class in the collection
	 * Only useful if the collection does not use generics
	 */
	Class targetElement() default void.class;
	FetchType fetch() default LAZY;
}
