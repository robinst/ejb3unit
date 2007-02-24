package com.bm.ejb3metadata.annotations.analyzer.classes;

import org.ejb3unit.asm.Type;

import com.bm.ejb3metadata.annotations.analyzer.AnnotationType;
import com.bm.ejb3metadata.annotations.analyzer.ObjectArrayAnnotationVisitor;
import com.bm.ejb3metadata.annotations.impl.JRemote;
import com.bm.ejb3metadata.annotations.metadata.ClassAnnotationMetadata;

/**
 * This class manages the handling of &#64;{@link javax.ejb.Remote} annotation.
 * @author Daniel Wiese
 */
public class JavaxEjbRemoteVisitor extends ObjectArrayAnnotationVisitor<ClassAnnotationMetadata, Type> implements AnnotationType {

    /**
     * Type of annotation.
     */
    public static final String TYPE = "Ljavax/ejb/Remote;";

    /**
     * Constructor.
     * @param classAnnotationMetadata linked to a class metadata
     */
    public JavaxEjbRemoteVisitor(final ClassAnnotationMetadata classAnnotationMetadata) {
        super(classAnnotationMetadata);
    }

    /**
     * Visits the end of the annotation.<br>
     * Creates the object and store it.
     */
    @Override
    public void visitEnd() {
        JRemote jRemote = new JRemote();
        for (Type s : getArrayObjects()) {
            jRemote.addInterface(s.getInternalName());
        }

        getAnnotationMetadata().setRemoteInterfaces(jRemote);

    }

    /**
     * @return type of the annotation (its description)
     */
    public String getType() {
        return TYPE;
    }

}
