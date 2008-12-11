package com.bm.ejb3metadata.annotations.analyzer;

import org.hibernate.repackage.cglib.asm.AnnotationVisitor;

/**
 * This class manages the handling of enum values.
 * @param <T> the ClassAnnotationMetadata or MethodAnnotationMetadata.
 * @author Daniel Wiese
 */
public abstract class EnumAnnotationVisitor<T> extends AbsAnnotationVisitor<T> implements AnnotationVisitor, AnnotationType {

    /**
     * Value.
     */
    private String value = null;


    /**
     * Constructor.
     * @param annotationMetadata linked to a &lt;T&gt; metadata.
     */
    public EnumAnnotationVisitor(final T annotationMetadata) {
        super(annotationMetadata);
    }

    /**
     * Visits an enumeration value of the annotation.
     * @param name the value name.
     * @param desc the class descriptor of the enumeration class.
     * @param value the actual enumeration value.
     */
    @Override
    public void visitEnum(final String name, final String desc, final String value) {
        this.value = value;
    }

    /**
     * @return value of the object
     */
    public String getValue() {
        return this.value;
    }


}
