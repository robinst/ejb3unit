package com.bm.ejb3metadata.annotations.analyzer;

import org.ejb3unit.asm.AnnotationVisitor;

/**
 * This class manages the handling of single type like String name().
 * @param <T> the type of annotation metadata.
 * @param <V> the type of class / erasure (for values).
 * @author Daniel Wiese
 */
public abstract class ObjectAnnotationVisitor<T, V> extends AbsAnnotationVisitor<T> implements AnnotationVisitor, AnnotationType {

    /**
     * Internal value in the given type.
     */
    private V value = null;


    /**
     * Constructor.
     * @param annotationMetadata linked to a metadata.
     */
    public ObjectAnnotationVisitor(final T annotationMetadata) {
        super(annotationMetadata);
    }


    /**
     * Visits a primitive value of the annotation.
     * @param name the value name.
     * @param value the actual value, whose type must be {@link Byte},
     *        {@link Boolean}, {@link Character}, {@link Short},
     *        {@link Integer}, {@link Long}, {@link Float}, {@link Double},
     *        {@link String} or {@link org.ejb3unit.asm.Type}.
     */
    @Override
    @SuppressWarnings("unchecked")
    public void visit(final String name, final Object value) {
        this.value = (V) value;
    }

    /**
     * @return value of the object
     */
    public V getValue() {
        return this.value;
    }


}
