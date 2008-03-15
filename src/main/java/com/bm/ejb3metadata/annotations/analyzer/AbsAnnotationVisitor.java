package com.bm.ejb3metadata.annotations.analyzer;

import org.ejb3unit.asm.jar.AnnotationVisitor;

/**
 * This class manages the setter/getter of annotation visitor.
 * 
 * @param <T>
 *            a metadata object.
 * @author Daniel Wiese
 */
public abstract class AbsAnnotationVisitor<T> implements AnnotationVisitor,
		AnnotationType {

	/**
	 * Linked to an AnnotationMetadata ?
	 */
	private T annotationMetadata;

	/**
	 * Constructor (default).
	 */
	public AbsAnnotationVisitor() {

	}

	/**
	 * Constructor.
	 * 
	 * @param annotationMetadata
	 *            linked to an annotation metadata
	 */
	public AbsAnnotationVisitor(final T annotationMetadata) {
		this();
		this.annotationMetadata = annotationMetadata;
	}

	/**
	 * Visits a primitive value of the annotation.
	 * 
	 * @param name
	 *            the value name.
	 * @param value
	 *            the actual value, whose type must be {@link Byte},
	 *            {@link Boolean}, {@link Character}, {@link Short},
	 *            {@link Integer}, {@link Long}, {@link Float},
	 *            {@link Double}, {@link String} or
	 *            {@link org.ejb3unit.asm.Type}.
	 */
	public void visit(final String name, final Object value) {

	}

	/**
	 * Visits an enumeration value of the annotation.
	 * 
	 * @param name
	 *            the value name.
	 * @param desc
	 *            the class descriptor of the enumeration class.
	 * @param value
	 *            the actual enumeration value.
	 */
	public void visitEnum(final String name, final String desc,
			final String value) {

	}

	/**
	 * Visits a nested annotation value of the annotation.
	 * 
	 * @param name
	 *            the value name.
	 * @param desc
	 *            the class descriptor of the nested annotation class.
	 * @return a non null visitor to visit the actual nested annotation value.
	 *         <i>The nested annotation value must be fully visited before
	 *         calling other methods on this annotation visitor</i>.
	 */
	public AnnotationVisitor visitAnnotation(final String name,
			final String desc) {
		return this;
	}

	/**
	 * Visits an array value of the annotation.
	 * 
	 * @param name
	 *            the value name.
	 * @return a non null visitor to visit the actual array value elements. The
	 *         'name' parameters passed to the methods of this visitor are
	 *         ignored. <i>All the array values must be visited before calling
	 *         other methods on this annotation visitor</i>.
	 */
	public AnnotationVisitor visitArray(final String name) {
		return this;
	}

	/**
	 * Visits the end of the annotation.
	 */
	public abstract void visitEnd();

	/**
	 * class/method metadata.
	 * 
	 * @return class/method metadata
	 */
	public T getAnnotationMetadata() {
		return annotationMetadata;
	}

}
