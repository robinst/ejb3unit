//$Id: Table.java,v 1.1 2006/04/17 12:11:07 daniel_wiese Exp $
package org.hibernate.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Complementary information to a table either primary or secondary
 *
 * @author Emmanuel Bernard
 */
@Target({TYPE}) @Retention(RUNTIME)
public @interface Table {
	/**
	 * name of the targeted table
	 */
	String appliesTo();

	/**
	 * Indexes
	 */
	Index[] indexes() default {};

}
