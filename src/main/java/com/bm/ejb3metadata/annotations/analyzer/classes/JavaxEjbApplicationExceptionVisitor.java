package com.bm.ejb3metadata.annotations.analyzer.classes;

import javax.ejb.ApplicationException;

import com.bm.ejb3metadata.annotations.analyzer.AnnotationType;
import com.bm.ejb3metadata.annotations.analyzer.ObjectAnnotationVisitor;
import com.bm.ejb3metadata.annotations.impl.JApplicationException;
import com.bm.ejb3metadata.annotations.metadata.ClassAnnotationMetadata;

/**
 * This class manages the handling of the &#64;{@link javax.ejb.ApplicationException} annotation.<br>
 * @author Daniel Wiese
 */
public class JavaxEjbApplicationExceptionVisitor extends ObjectAnnotationVisitor<ClassAnnotationMetadata, Boolean> implements
        AnnotationType {

    /**
     * Type of annotation.
     */
    public static final String TYPE = "Ljavax/ejb/ApplicationException;";

    /**
     * Constructor.
     * @param classAnnotationMetadata linked to a class metadata
     */
    public JavaxEjbApplicationExceptionVisitor(final ClassAnnotationMetadata classAnnotationMetadata) {
        super(classAnnotationMetadata);
    }

    /**
     * Visits the end of the annotation. Creates the object and store it.
     */
    @Override
    public void visitEnd() {
        Boolean b = getValue();

        ApplicationException jApplicationException;
        // no value --> default value
        if (b == null) {
            jApplicationException = new JApplicationException();
        } else {
            jApplicationException = new JApplicationException(b.booleanValue());
        }

        getAnnotationMetadata().setApplicationException(jApplicationException);

    }

    /**
     * @return type of the annotation (its description)
     */
    public String getType() {
        return TYPE;
    }

}
