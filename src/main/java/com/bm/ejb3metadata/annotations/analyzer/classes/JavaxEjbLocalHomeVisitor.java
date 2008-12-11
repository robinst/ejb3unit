package com.bm.ejb3metadata.annotations.analyzer.classes;

import org.hibernate.repackage.cglib.asm.Type;

import com.bm.ejb3metadata.annotations.analyzer.AnnotationType;
import com.bm.ejb3metadata.annotations.analyzer.ObjectAnnotationVisitor;
import com.bm.ejb3metadata.annotations.metadata.ClassAnnotationMetadata;

/**
 * This class manages the handling of &#64;{@link javax.ejb.LocalHome} annotation.
 * @author Daniel Wiese
 */
public class JavaxEjbLocalHomeVisitor extends ObjectAnnotationVisitor<ClassAnnotationMetadata, Type> implements AnnotationType {

    /**
     * Type of annotation.
     */
    public static final String TYPE = "Ljavax/ejb/LocalHome;";

    /**
     * Constructor.
     * @param classAnnotationMetadata linked to a class metadata
     */
    public JavaxEjbLocalHomeVisitor(final ClassAnnotationMetadata classAnnotationMetadata) {
        super(classAnnotationMetadata);
    }

    /**
     * Visits the end of the annotation.<br> Creates the object and store it.
     */
    @Override
    public void visitEnd() {
        Type t = getValue();
        String className = t.getInternalName();
        getAnnotationMetadata().setLocalHome(className);
    }

    /**
     * @return type of the annotation (its description).
     */
    public String getType() {
        return TYPE;
    }

}
