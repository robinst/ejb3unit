package com.bm.ejb3metadata.annotations.analyzer.classes;

import static com.bm.ejb3metadata.annotations.ClassType.STATEFUL;

import com.bm.ejb3metadata.annotations.analyzer.AnnotationType;
import com.bm.ejb3metadata.annotations.impl.JStateful;
import com.bm.ejb3metadata.annotations.metadata.ClassAnnotationMetadata;

/**
 * This class manages the handling of &#64;{@link javax.ejb.Stateful}
 * annotation.
 * @author Daniel Wiese
 */
public class JavaxEjbStatefulVisitor extends AbsCommonEjbVisitor<JStateful> implements AnnotationType {

    /**
     * Type of annotation.
     */
    public static final String TYPE = "Ljavax/ejb/Stateful;";

    /**
     * Constructor.
     * @param classAnnotationMetadata linked to a class metadata
     */
    public JavaxEjbStatefulVisitor(final ClassAnnotationMetadata classAnnotationMetadata) {
        super(classAnnotationMetadata);
    }

    /**
     * Visits the end of the annotation. <br> Creates the object and store it.
     */
    @Override
    public void visitEnd() {
        super.visitEnd();
        getAnnotationMetadata().setClassType(STATEFUL);
    }

    /**
     * @return the object representing common bean.
     */
    @Override
    public JStateful getJCommonBean() {
        JStateful jStateful = getAnnotationMetadata().getJStateful();
        if (jStateful == null) {
            jStateful = new JStateful();
            getAnnotationMetadata().setJStateful(jStateful);
        }
        return jStateful;
    }

    /**
     * @return type of the annotation (its description)
     */
    public String getType() {
        return TYPE;
    }

}
