package com.bm.ejb3metadata.annotations.analyzer;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.repackage.cglib.asm.AnnotationVisitor;

/**
 * This class manages the handling of Array[] type like String[] value().
 * @param <T> the type of annotation metadata.
 * @param <V> the type of class / erasure (for values).
 * @author Daniel Wiese
 */
public abstract class ObjectArrayAnnotationVisitor<T, V>
                 extends AbsAnnotationVisitor<T>
                 implements AnnotationVisitor, AnnotationType {

    /**
     * Array.
     */
    private List<V> arrayObjects = null;

    /**
     * Init method.
     */
    public void init() {
        this.arrayObjects = new ArrayList<V>();
    }

    /**
     * Constructor.
     * @param annotationMetadata linked to an annotation metadata.
     */
    public ObjectArrayAnnotationVisitor(final T annotationMetadata) {
        super(annotationMetadata);
        init();
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
        this.arrayObjects.add((V) value);
    }

    /**
     * @return list of objects
     */
    public List<V> getArrayObjects() {
        return this.arrayObjects;
    }

}
