package com.bm.ejb3metadata.annotations.analyzer.classes;

import java.util.ArrayList;
import java.util.List;

import com.bm.ejb3metadata.annotations.analyzer.JavaxPersistencePersistenceContextVisitor;
import com.bm.ejb3metadata.annotations.impl.JavaxPersistenceContext;
import com.bm.ejb3metadata.annotations.metadata.ClassAnnotationMetadata;

/**
 * This class manages the handling of &#64;{@link javax.persistence.PersistenceContexts}
 * annotation.
 * @author Daniel Wiese
 */
public class JavaxPersistencePersistenceContextsVisitor extends
        JavaxPersistencePersistenceContextVisitor<ClassAnnotationMetadata> {

    /**
     * Type of annotation.
     */
    public static final String TYPE = "Ljavax/persistence/PersistenceContexts;";

    /**
     * List of JavaxPersistenceContext object.
     */
    private List<JavaxPersistenceContext> javaxPersistenceContexts = null;

    /**
     * Object is added to the list.
     */
    private boolean isAdded = false;

    /**
     * Constructor.
     * @param annotationMetadata linked to a class or method metadata
     */
    public JavaxPersistencePersistenceContextsVisitor(final ClassAnnotationMetadata annotationMetadata) {
        super(annotationMetadata);
        javaxPersistenceContexts = new ArrayList<JavaxPersistenceContext>();
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
        if (javaxPersistenceContexts.size() > 0 && isAdded) {
            setJavaxPersistenceContext(new JavaxPersistenceContext());
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
            javaxPersistenceContexts.add(getJavaxPersistenceContext());
            isAdded = true;
        }

        // update list
        getAnnotationMetadata().setJavaxPersistencePersistenceContexts(javaxPersistenceContexts);
    }

    /**
     * @return type of the annotation (its description)
     */
    @Override
    public String getType() {
        return TYPE;
    }

}
