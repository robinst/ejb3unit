//$Id: AnnotationException.java,v 1.1 2006/04/17 12:11:13 daniel_wiese Exp $
package org.hibernate;

/**
 * Annotation related exception.
 * The EJB3 EG will probably set a generic exception.
 * I'll then use this one.
 * 
 * @author Emmanuel Bernard
 */
public class AnnotationException extends MappingException {

	public AnnotationException(String msg, Throwable root) {
		super(msg, root);
	}

	public AnnotationException(Throwable root) {
		super(root);
	}

	public AnnotationException(String s) {
		super(s);
	}
}
