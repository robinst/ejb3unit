package com.bm.ejb3metadata.annotations.analyzer.classes;

import static com.bm.ejb3metadata.annotations.ClassType.STATELESS;

import com.bm.ejb3metadata.annotations.analyzer.AnnotationType;
import com.bm.ejb3metadata.annotations.impl.JStateless;
import com.bm.ejb3metadata.annotations.metadata.ClassAnnotationMetadata;

/**
 * This class manages the handling of &#64;{@link javax.ejb.Stateless}
 * annotation.
 * @author Daniel Wiese
 */
public class JavaxEjbStatelessVisitor extends AbsCommonEjbVisitor<JStateless> implements AnnotationType {

    /**
     * Type of annotation.
     */
    public static final String TYPE = "Ljavax/ejb/Stateless;";

    /**
     * Constructor.
     * @param classAnnotationMetadata linked to a class metadata
     */
    public JavaxEjbStatelessVisitor(final ClassAnnotationMetadata classAnnotationMetadata) {
        super(classAnnotationMetadata);
    }

    /**
     * Visits the end of the annotation. <br> Creates the object and store it.
     */
    @Override
    public void visitEnd() {
        super.visitEnd();
        getAnnotationMetadata().setClassType(STATELESS);
    }

    /**
     * @return the object representing common bean.
     */
    @Override
    public JStateless getJCommonBean() {
        JStateless jStateless = getAnnotationMetadata().getJStateless();
        if (jStateless == null) {
            jStateless = new JStateless();
            getAnnotationMetadata().setJStateless(jStateless);
        }
        return jStateless;
    }

    /**
     * @return type of the annotation (its description)
     */
    public String getType() {
        return TYPE;
    }

}
