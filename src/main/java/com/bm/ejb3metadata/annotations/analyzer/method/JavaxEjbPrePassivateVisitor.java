package com.bm.ejb3metadata.annotations.analyzer.method;

import com.bm.ejb3metadata.annotations.analyzer.AbsAnnotationVisitor;
import com.bm.ejb3metadata.annotations.analyzer.AnnotationType;
import com.bm.ejb3metadata.annotations.metadata.MethodAnnotationMetadata;

/**
 * This class manages the handling of &#64;{@link javax.ejb.PrePassivate} annotation.
 * @author Daniel Wiese
 */
public class JavaxEjbPrePassivateVisitor extends AbsAnnotationVisitor<MethodAnnotationMetadata> implements AnnotationType {

    /**
     * Type of annotation.
     */
    public static final String TYPE = "Ljavax/ejb/PrePassivate;";

    /**
     * Constructor.
     * @param methodAnnotationMetadata linked to a method metadata
     */
    public JavaxEjbPrePassivateVisitor(final MethodAnnotationMetadata methodAnnotationMetadata) {
        super(methodAnnotationMetadata);
    }

    /**
     * Visits the end of the annotation. <br>
     * Creates the object and store it.
     */
    @Override
    public void visitEnd() {
        getAnnotationMetadata().setPrePassivate(true);

        // set method on super class
        getAnnotationMetadata().getClassAnnotationMetadata().addPrePassivateMethodMetadata(getAnnotationMetadata());

    }

    /**
     * @return type of the annotation (its description).
     */
    public String getType() {
        return TYPE;
    }

}
