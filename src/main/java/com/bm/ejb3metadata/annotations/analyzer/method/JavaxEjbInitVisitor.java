package com.bm.ejb3metadata.annotations.analyzer.method;

import com.bm.ejb3metadata.annotations.analyzer.AnnotationType;
import com.bm.ejb3metadata.annotations.analyzer.ObjectArrayAnnotationVisitor;
import com.bm.ejb3metadata.annotations.metadata.MethodAnnotationMetadata;

/**
 * This class manages the handling of &#64;{@link javax.ejb.Init} annotation.
 * @author Daniel Wiese
 */
public class JavaxEjbInitVisitor
         extends ObjectArrayAnnotationVisitor<MethodAnnotationMetadata, String>
         implements AnnotationType {

    /**
     * Type of annotation.
     */
    public static final String TYPE = "Ljavax/ejb/Init;";

    /**
     * Constructor.
     * @param methodAnnotationMetadata linked to a method metadata
     */
    public JavaxEjbInitVisitor(final MethodAnnotationMetadata methodAnnotationMetadata) {
        super(methodAnnotationMetadata);
    }

    /**
     * Visits the end of the annotation.<br>
     * Creates the object and store it.
     */
    @Override
    public void visitEnd() {
        //TODO: Implements Init !
        //getMethodAnnotationMetadata().setXXX

    }

    /**
     * @return type of the annotation (its description).
     */
    public String getType() {
        return TYPE;
    }

}
