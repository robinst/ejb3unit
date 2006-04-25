package org.hibernate.reflection.java;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

import org.hibernate.reflection.XAnnotatedElement;
import org.hibernate.reflection.java.xml.XMLContext;

/**
 * @author Paolo Perrotta
 * @author Davide Marchignoli
 */
abstract class JavaXAnnotatedElement implements XAnnotatedElement {

	// responsible for extracting annotations
	private JavaAnnotationReader annotationReader;
	private final XMLContext xmlContext;

	private final JavaXFactory factory;

	private final AnnotatedElement annotatedElement;
	
	public JavaXAnnotatedElement( AnnotatedElement annotatedElement, JavaXFactory factory ) {
		this.annotationReader = new JavaAnnotationReader( annotatedElement );
		this.factory = factory;
		this.annotatedElement = annotatedElement;
		this.xmlContext = factory.getXMLContext();
	}

	protected JavaXFactory getFactory() {
		return factory;
	}

	private JavaAnnotationReader getAnnotationReader() {
		return annotationReader;
	}

	public <T extends Annotation> T getAnnotation(Class<T> annotationType) {
		return getAnnotationReader().getAnnotation( annotationType );
	}

	public <T extends Annotation> boolean isAnnotationPresent(Class<T> annotationType) {
		return getAnnotationReader().isAnnotationPresent( annotationType );
	}

	public Annotation[] getAnnotations() {
		return getAnnotationReader().getAnnotations();
	}

	AnnotatedElement toAnnotatedElement() {
		return annotatedElement;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || ! (obj instanceof JavaXAnnotatedElement) ) return false;
		JavaXAnnotatedElement other = (JavaXAnnotatedElement) obj;
		return toAnnotatedElement().equals( other.toAnnotatedElement() );
	}

	@Override
	public int hashCode() {
		return toAnnotatedElement().hashCode();
	}

	@Override
	public String toString() {
		return toAnnotatedElement().toString();
	}
}
