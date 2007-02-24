package com.bm.ejb3metadata.annotations.analyzer;

import static javax.ejb.TransactionAttributeType.MANDATORY;
import static javax.ejb.TransactionAttributeType.REQUIRED;
import static javax.ejb.TransactionAttributeType.REQUIRES_NEW;
import static javax.ejb.TransactionAttributeType.SUPPORTS;
import static javax.ejb.TransactionAttributeType.NOT_SUPPORTED;
import static javax.ejb.TransactionAttributeType.NEVER;

import com.bm.ejb3metadata.annotations.metadata.interfaces.ITransactionAttribute;

/**
 * This class manages the handling of &#64;{@link javax.ejb.TransactionAttribute} annotation.
 * @param <T> An implementation of ITransactionAttribute interface.
 * @author Daniel Wiese
 */
public class JavaxEjbTransactionAttributeVisitor<T extends ITransactionAttribute>
          extends EnumAnnotationVisitor<T> implements AnnotationType {

    /**
     * Type of annotation.
     */
    public static final String TYPE = "Ljavax/ejb/TransactionAttribute;";

    /**
     * Constructor.
     * @param annotationMetadata linked to a class or method metadata
     */
    public JavaxEjbTransactionAttributeVisitor(final T annotationMetadata) {
        super(annotationMetadata);
    }

    /**
     * Visits the end of the annotation. <br>
     * Creates the object and store it.
     */
    @Override
    public void visitEnd() {
        String s = getValue();
        // TYPE annotation
        if (getAnnotationMetadata() != null) {
            if (MANDATORY.name().equals(s)) {
                getAnnotationMetadata().setTransactionAttributeType(MANDATORY);
            } else if (REQUIRED.name().equals(s)) {
                getAnnotationMetadata().setTransactionAttributeType(REQUIRED);
            } else if (REQUIRES_NEW.name().equals(s)) {
                getAnnotationMetadata().setTransactionAttributeType(REQUIRES_NEW);
            } else if (SUPPORTS.name().equals(s)) {
                getAnnotationMetadata().setTransactionAttributeType(SUPPORTS);
            } else if (NOT_SUPPORTED.name().equals(s)) {
                getAnnotationMetadata().setTransactionAttributeType(NOT_SUPPORTED);
            } else if (NEVER.name().equals(s)) {
                getAnnotationMetadata().setTransactionAttributeType(NEVER);
            } else {
                // set default
                getAnnotationMetadata().setTransactionAttributeType(REQUIRED);
            }
        }
    }

    /**
     * @return type of the annotation (its description)
     */
    public String getType() {
        return TYPE;
    }

}
