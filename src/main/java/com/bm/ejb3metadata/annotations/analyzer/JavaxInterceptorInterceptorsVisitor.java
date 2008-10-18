package com.bm.ejb3metadata.annotations.analyzer;

import org.ejb3unit.asm.jar.Type;

import com.bm.ejb3metadata.annotations.impl.JInterceptors;
import com.bm.ejb3metadata.annotations.metadata.interfaces.IEJBInterceptors;

/**
 * This class manages the handling of &#64;{@link javax.interceptor.Interceptors}
 * annotation. This can be applied on a Class or on Methods.
 * @param <T> An implementation of IEJBInterceptors interface.
 * @author Daniel Wiese
 */
public class JavaxInterceptorInterceptorsVisitor<T extends IEJBInterceptors> extends
        ObjectArrayAnnotationVisitor<T, Type> implements AnnotationType {

    /**
     * Type of annotation.
     */
    public static final String TYPE = "Ljavax/interceptor/Interceptors;";

    /**
     * Constructor.
     * @param annotationMetadata linked to a class/method metadata.
     */
    public JavaxInterceptorInterceptorsVisitor(final T annotationMetadata) {
        super(annotationMetadata);
    }

    /**
     * Visits the end of the annotation.<br>
     * Creates the object and store it.
     */
    @Override
    public void visitEnd() {
        JInterceptors jInterceptors = new JInterceptors();
        for (Type s : getArrayObjects()) {
            jInterceptors.addClass(s.getInternalName());
        }

        getAnnotationMetadata().setAnnotationsInterceptors(jInterceptors);

    }

    /**
     * @return type of the annotation (its description)
     */
    public String getType() {
        return TYPE;
    }

}
