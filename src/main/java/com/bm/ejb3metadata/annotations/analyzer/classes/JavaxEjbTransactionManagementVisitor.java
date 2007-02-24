package com.bm.ejb3metadata.annotations.analyzer.classes;

import static javax.ejb.TransactionManagementType.BEAN;
import static javax.ejb.TransactionManagementType.CONTAINER;

import com.bm.ejb3metadata.annotations.analyzer.AnnotationType;
import com.bm.ejb3metadata.annotations.analyzer.EnumAnnotationVisitor;
import com.bm.ejb3metadata.annotations.metadata.ClassAnnotationMetadata;

/**
 * This class manages the handling of &#64;{@link javax.ejb.TransactionManagement} annotation.
 * @author Daniel Wiese
 */
public class JavaxEjbTransactionManagementVisitor
               extends EnumAnnotationVisitor<ClassAnnotationMetadata>
               implements AnnotationType {

    /**
     * Type of annotation.
     */
    public static final String TYPE = "Ljavax/ejb/TransactionManagement;";

    /**
     * Constructor.
     * @param classAnnotationMetadata linked to a class metadata
     */
    public JavaxEjbTransactionManagementVisitor(final ClassAnnotationMetadata classAnnotationMetadata) {
        super(classAnnotationMetadata);
    }

    /**
     * Visits the end of the annotation.<br>
     * Creates the object and store it.
     */
    @Override
    public void visitEnd() {
        String s = getValue();
        if (BEAN.name().equals(s)) {
            getAnnotationMetadata().setTransactionManagementType(BEAN);
        } else if (CONTAINER.name().equals(s)) {
            getAnnotationMetadata().setTransactionManagementType(CONTAINER);
        } else {
            // set default
            getAnnotationMetadata().setTransactionManagementType(CONTAINER);
        }

    }

    /**
     * @return type of the annotation (its description).
     */
    public String getType() {
        return TYPE;
    }

}
