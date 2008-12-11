package com.bm.ejb3metadata.annotations.analyzer;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.repackage.cglib.asm.AnnotationVisitor;
import org.hibernate.repackage.cglib.asm.commons.EmptyVisitor;

import com.bm.ejb3metadata.annotations.metadata.interfaces.ISharedMetadata;

/**
 * This classes analyses annotation (could be class, method, attribute, etc). It
 * is extended by classes for a specific type Class, Method, Attribute.
 * 
 * @param <T>
 *            the kind of metadata visited by this visitor.
 * @author Daniel Wiese
 */
public abstract class ScanCommonVisitor<T extends ISharedMetadata> extends
		EmptyVisitor {

	/**
	 * Annotation visitor which do nothing.
	 */
	private EmptyVisitor emptyVisitor = null;

	/**
	 * Map of annotation visitors used by this visitor.<br>
	 * The key is the annotation descriptor (asm)
	 */
	private Map<String, AnnotationVisitor> annotationVisitors = null;

	/**
	 * Constructor Build map of visitors.
	 */
	public ScanCommonVisitor() {
		annotationVisitors = new HashMap<String, AnnotationVisitor>();
	}

	/**
	 * Build visitors used by this one.
	 * 
	 * @param annotationMetadata
	 *            metadata used
	 */
	protected void initVisitors(final T annotationMetadata) {
		emptyVisitor = new EmptyVisitor();

		// add @EJB
		annotationVisitors.put(JavaxEjbEJBVisitor.TYPE,
				new JavaxEjbEJBVisitor<T>(annotationMetadata));

		// add @Resource
		annotationVisitors.put(JavaxAnnotationResourceVisitor.TYPE,
				new JavaxAnnotationResourceVisitor<T>(annotationMetadata));

		// add @PersistenceContext
		annotationVisitors.put(JavaxPersistencePersistenceContextVisitor.TYPE,
				new JavaxPersistencePersistenceContextVisitor<T>(
						annotationMetadata));

		// add @PersistenceUnit
		annotationVisitors.put(JavaxPersistencePersistenceUnitVisitor.TYPE,
				new JavaxPersistencePersistenceUnitVisitor<T>(
						annotationMetadata));

	}

	/**
	 * @return empty visitor
	 */
	protected EmptyVisitor getEmptyVisitor() {
		return emptyVisitor;
	}

	/**
	 * Visits an annotation of the class.
	 * 
	 * @param desc
	 *            the class descriptor of the annotation class.
	 * @param visible
	 *            <tt>true</tt> if the annotation is visible at runtime.
	 * @return a non null visitor to visit the annotation values.
	 */
	@Override
	public AnnotationVisitor visitAnnotation(final String desc,
			final boolean visible) {
		AnnotationVisitor av = annotationVisitors.get(desc);
		if (av != null) {
			return av;
		}
		return emptyVisitor;
	}

	/**
	 * @return the visitors used by this scanner.
	 */
	protected Map<String, AnnotationVisitor> getAnnotationVisitors() {
		return annotationVisitors;
	}

}
