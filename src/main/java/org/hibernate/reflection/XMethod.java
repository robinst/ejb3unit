//$Id: XMethod.java,v 1.1 2006/04/17 12:11:13 daniel_wiese Exp $
package org.hibernate.reflection;

/**
 * Represent an invokable method
 *
 * The underlying layer does not guaranty that xProperty == xMethod
 * if the underlying artefact is the same
 * However xProperty.equals(xMethod) is supposed to return true
 *
 * @author Emmanuel Bernard
 */
public interface XMethod extends XMember {}
