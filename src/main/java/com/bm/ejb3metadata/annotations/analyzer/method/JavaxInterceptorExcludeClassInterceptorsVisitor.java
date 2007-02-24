package com.bm.ejb3metadata.annotations.analyzer.method;

import com.bm.ejb3metadata.annotations.analyzer.AbsAnnotationVisitor;
import com.bm.ejb3metadata.annotations.analyzer.AnnotationType;
import com.bm.ejb3metadata.annotations.metadata.MethodAnnotationMetadata;

/**
 * This class manages the handling of &#64;{@link javax.interceptor.ExcludeClassInterceptors}
 * annotation.
 * @author Daniel Wiese
 */
public class JavaxInterceptorExcludeClassInterceptorsVisitor extends AbsAnnotationVisitor<MethodAnnotationMetadata> implements
        AnnotationType {

    /**
     * Type of annotation.
     */
    public static final String TYPE = "Ljavax/interceptor/ExcludeClassInterceptors;";

    /**
     * Constructor.
     * @param methodAnnotationMetadata linked to a method metadata.
     */
    public JavaxInterceptorExcludeClassInterceptorsVisitor(final MethodAnnotationMetadata methodAnnotationMetadata) {
        super(methodAnnotationMetadata);
    }

    /**
     * Visits the end of the annotation.<br>
     * Creates the object and store it.
     */
    @Override
    public void visitEnd() {
        // set flag on method
        getAnnotationMetadata().setExcludeClassInterceptors(true);

    }

    /**
     * @return type of the annotation (its description)
     */
    public String getType() {
        return TYPE;
    }

}
