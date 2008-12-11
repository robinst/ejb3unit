package com.bm.ejb3metadata.annotations.analyzer.classes;

import org.hibernate.repackage.cglib.asm.Type;
import com.bm.ejb3metadata.annotations.analyzer.AnnotationType;
import com.bm.ejb3metadata.annotations.analyzer.ObjectArrayAnnotationVisitor;
import com.bm.ejb3metadata.annotations.impl.JLocal;
import com.bm.ejb3metadata.annotations.metadata.ClassAnnotationMetadata;

/**
 * This class manages the handling of &#64;{@link javax.ejb.Local} annotation.
 * @author Daniel Wiese
 */
public class JavaxEjbLocalVisitor extends ObjectArrayAnnotationVisitor<ClassAnnotationMetadata, Type>  implements AnnotationType {

    /**
     * Type of annotation.
     */
    public static final String TYPE = "Ljavax/ejb/Local;";

    /**
     * Constructor.
     * @param classAnnotationMetadata linked to a class metadata.
     */
    public JavaxEjbLocalVisitor(final ClassAnnotationMetadata classAnnotationMetadata) {
        super(classAnnotationMetadata);
    }

    /**
     * Visits the end of the annotation.<br>
     * Creates the object and store it.
     */
    @Override
    public void visitEnd() {
        JLocal jLocal = new JLocal();
        for (Type s : getArrayObjects()) {
            jLocal.addInterface(s.getInternalName());
        }
        getAnnotationMetadata().setLocalInterfaces(jLocal);

    }

    /**
     * @return type of the annotation (its description).
     */
    public String getType() {
        return TYPE;
    }

}
