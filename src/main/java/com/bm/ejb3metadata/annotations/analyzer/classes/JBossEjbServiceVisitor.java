package com.bm.ejb3metadata.annotations.analyzer.classes;

import static com.bm.ejb3metadata.annotations.ClassType.SERVICE;

import com.bm.ejb3metadata.annotations.analyzer.AnnotationType;
import com.bm.ejb3metadata.annotations.impl.JService;
import com.bm.ejb3metadata.annotations.metadata.ClassAnnotationMetadata;

/**
 * This class manages the handling of &#64;{@link org.jboss.annotation.ejb.Service}
 * annotation.
 * @author Daniel Wiese
 */
public class JBossEjbServiceVisitor extends AbsCommonEjbVisitor<JService> implements AnnotationType {

    /**
     * Type of annotation.
     */
    public static final String TYPE = "Lorg/jboss/annotation/ejb/Service;";

    /**
     * Constructor.
     * @param classAnnotationMetadata linked to a class metadata
     */
    public JBossEjbServiceVisitor(final ClassAnnotationMetadata classAnnotationMetadata) {
        super(classAnnotationMetadata);
    }

    /**
     * Visits the end of the annotation. <br> Creates the object and store it.
     */
    @Override
    public void visitEnd() {
        super.visitEnd();
        getAnnotationMetadata().setClassType(SERVICE);
    }

    /**
     * @return the object representing common bean.
     */
    @Override
    public JService getJCommonBean() {
        JService jService = getAnnotationMetadata().getJService();
        if (jService == null) {
            jService = new JService();
            getAnnotationMetadata().setJService(jService);
        }
        return jService;
    }

    /**
     * @return type of the annotation (its description)
     */
    public String getType() {
        return TYPE;
    }

}
