package com.bm.ejb3metadata.annotations.analyzer.classes;

import java.util.ArrayList;
import java.util.List;

import com.bm.ejb3metadata.annotations.analyzer.JavaxEjbEJBVisitor;
import com.bm.ejb3metadata.annotations.impl.JEjbEJB;
import com.bm.ejb3metadata.annotations.metadata.ClassAnnotationMetadata;

/**
 * This class manages the handling of &#64;{@link javax.ejb.EJBs} annotation.
 * @author Daniel Wiese
 */
public class JavaxEjbEJBsVisitor extends JavaxEjbEJBVisitor<ClassAnnotationMetadata> {

    /**
     * Type of annotation.
     */
    public static final String TYPE = "Ljavax/ejb/EJBs;";

    /**
     * List of jEjbEJB object.
     */
    private List<JEjbEJB> jEjbEJBs = null;

    /**
     * Object is added to the list.
     */
    private boolean isAdded = false;

    /**
     * Constructor.
     * @param annotationMetadata linked to a class or method metadata
     */
    public JavaxEjbEJBsVisitor(final ClassAnnotationMetadata annotationMetadata) {
        super(annotationMetadata);
        jEjbEJBs = new ArrayList<JEjbEJB>();
    }

    /**
     * Visits a primitive value of the annotation.<br>
     * @param name the value name.
     * @param value the actual value, whose type must be {@link Byte},
     *        {@link Boolean}, {@link Character}, {@link Short},
     *        {@link Integer}, {@link Long}, {@link Float}, {@link Double},
     *        {@link String} or {@link org.ejb3unit.asm.Type}.
     */
    @Override
    public void visit(final String name, final Object value) {
        // list not empty, need to create another reference
        // at the first item found
        if (jEjbEJBs.size() > 0 && isAdded) {
            setJEjbEJB(new JEjbEJB());
            isAdded = false;
        }

        // do super code
        super.visit(name, value);
    }

    /**
     * Visits the end of the annotation. <br>
     * Creates the object and store it.
     */
    @Override
    public void visitEnd() {
        // add object in the list
        if (!isAdded) {
            jEjbEJBs.add(getJEjbEJB());
            isAdded = true;
        }

        // update list
        getAnnotationMetadata().setJEjbEJBs(jEjbEJBs);
    }

    /**
     * @return type of the annotation (its description)
     */
    @Override
    public String getType() {
        return TYPE;
    }

}
