package com.bm.ejb3metadata.annotations.analyzer.method;

import javax.ejb.Remove;

import com.bm.ejb3metadata.annotations.analyzer.AnnotationType;
import com.bm.ejb3metadata.annotations.analyzer.ObjectAnnotationVisitor;
import com.bm.ejb3metadata.annotations.impl.JRemove;
import com.bm.ejb3metadata.annotations.metadata.MethodAnnotationMetadata;

/**
 * This class manages the handling of &#64;{@link javax.ejb.Remove} annotation.
 * @author Daniel Wiese
 */
public class JavaxEjbRemoveVisitor extends ObjectAnnotationVisitor<MethodAnnotationMetadata, Boolean>  implements AnnotationType {

    /**
     * Type of annotation.
     */
    public static final String TYPE = "Ljavax/ejb/Remove;";

    /**
     * Constructor.
     * @param methodAnnotationMetadata linked to a method metadata.
     */
    public JavaxEjbRemoveVisitor(final MethodAnnotationMetadata methodAnnotationMetadata) {
        super(methodAnnotationMetadata);
    }

    /**
     * Visits the end of the annotation.<br>
     * Creates the object and store it.
     */
    @Override
    public void visitEnd() {
        Boolean b = getValue();

        Remove jRemove;
        // no value --> default value
        if (b == null) {
            jRemove = new JRemove();
        } else {
            jRemove = new JRemove(b.booleanValue());
        }

        getAnnotationMetadata().setRemove(jRemove);

    }

    /**
     * @return type of the annotation (its description)
     */
    public String getType() {
        return TYPE;
    }

}
