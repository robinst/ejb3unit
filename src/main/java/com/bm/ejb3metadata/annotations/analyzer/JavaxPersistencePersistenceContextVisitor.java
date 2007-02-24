package com.bm.ejb3metadata.annotations.analyzer;

import javax.persistence.PersistenceContextType;

import com.bm.ejb3metadata.annotations.impl.JavaxPersistenceContext;
import com.bm.ejb3metadata.annotations.metadata.interfaces.IPersistenceContext;

/**
 * This class manages the handling of &#64;{@link javax.persistence.PersistenceContext}
 * annotation.
 * @param <T> An implementation of IPersistenceContext interface.
 * @author Daniel Wiese
 */
public class JavaxPersistencePersistenceContextVisitor<T extends IPersistenceContext> extends AbsAnnotationVisitor<T> implements
        AnnotationType {

    /**
     * Type of annotation.
     */
    public static final String TYPE = "Ljavax/persistence/PersistenceContext;";

    /**
     * name attribute of the annotation.
     */
    private static final String NAME = "name";


    /**
     * UnitName attribute of the annotation.
     */
    private static final String UNIT_NAME = "unitName";

    /**
     * Persistence Context type attribute of the annotation.
     */
    private static final String PERSISTENCECONTEXT_TYPE = "type";

    /**
     * Transaction type.
     */
    private static final String PERSISTENCECONTEXT_TTRANSACTION_TYPE = "TRANSACTION";


    /**
     * Persistence context information.
     */
    private JavaxPersistenceContext javaxPersistenceContext = null;

    /**
     * Constructor.
     * @param annotationMetadata linked to a class or method or field metadata
     */
    public JavaxPersistencePersistenceContextVisitor(final T annotationMetadata) {
        super(annotationMetadata);
        javaxPersistenceContext = new JavaxPersistenceContext();
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
        if (UNIT_NAME.equals(name)) {
            javaxPersistenceContext.setUnitName((String) value);
        } else if (NAME.equals(name)) {
            javaxPersistenceContext.setName((String) value);
        }
    }

    /**
     * Visits an enumeration value of the annotation.
     * @param name the value name.
     * @param desc the class descriptor of the enumeration class.
     * @param value the actual enumeration value.
     */
    @Override
    public void visitEnum(final String name, final String desc, final String value) {
       if (name.equals(PERSISTENCECONTEXT_TYPE)) {
            if (PERSISTENCECONTEXT_TTRANSACTION_TYPE.equals(value)) {
                javaxPersistenceContext.setType(PersistenceContextType.TRANSACTION);
            } else {
                javaxPersistenceContext.setType(PersistenceContextType.EXTENDED);
            }

        }
    }


    /**
     * @return Internal object used representing &#64;{@link javax.persistence.PersistenceContext} annotation.
     */
    protected JavaxPersistenceContext getJavaxPersistenceContext() {
        return javaxPersistenceContext;
    }

    /**
     * Sets the javaxPersistenceContext object.
     * @param javaxPersistenceContext the object which replaced the previous one.
     */
    protected void setJavaxPersistenceContext(final JavaxPersistenceContext javaxPersistenceContext) {
        this.javaxPersistenceContext = javaxPersistenceContext;
    }

    /**
     * Visits the end of the annotation.<br>
     * Creates the object and store it.
     */
    @Override
    public void visitEnd() {
        // set flag on field
        getAnnotationMetadata().setJavaxPersistenceContext(javaxPersistenceContext);
    }

    /**
     * @return type of the annotation (its description)
     */
    public String getType() {
        return TYPE;
    }

}
