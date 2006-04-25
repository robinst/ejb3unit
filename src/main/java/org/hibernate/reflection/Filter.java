//$Id: Filter.java,v 1.1 2006/04/17 12:11:13 daniel_wiese Exp $
package org.hibernate.reflection;

/**
 * Filter properties
 *
 * @author Emmanuel Bernard
 */
public interface Filter {
	boolean returnStatic();
	boolean returnTransient();
}
