package com.bm.ejb3metadata.annotations.analyzer;

import com.bm.ejb3metadata.annotations.impl.JavaxPersistenceUnit;
import com.bm.ejb3metadata.annotations.metadata.interfaces.IPersistenceUnit;

/**
 * This class manages the handling of &#64;{@link javax.persistence.PersistenceUnit}
 * annotation.
 * @param <T> An implementation of IPersistenceUnit interface.
 * @author Daniel Wiese
 */
public class JavaxPersistencePersistenceUnitVisitor<T extends IPersistenceUnit> extends AbsAnnotationVisitor<T> implements
        AnnotationType {

    /**
     * Type of annotation.
     */
    public static final String TYPE = "Ljavax/persistence/PersistenceUnit;";

    /**
     * name attribute of the annotation.
     */
    private static final String NAME = "name";

    /**
     * UnitName attribute of the annotation.
     */
    private static final String UNIT_NAME = "unitName";

    /**
     * Persistence context information.
     */
    private JavaxPersistenceUnit javaxPersistenceUnit = null;

    /**
     * Constructor.
     * @param annotationMetadata linked to a class or method or field metadata
     */
    public JavaxPersistencePersistenceUnitVisitor(final T annotationMetadata) {
        super(annotationMetadata);
        javaxPersistenceUnit = new JavaxPersistenceUnit();
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
        if (NAME.equals(name)) {
            javaxPersistenceUnit.setName((String) value);
        } else if (name.equals(UNIT_NAME)) {
            javaxPersistenceUnit.setUnitName((String) value);
        }
    }

    /**
     * @return Internal object used representing &#64;{@link javax.persistence.PersistenceUnit} annotation.
     */
    protected JavaxPersistenceUnit getJavaxPersistenceUnit() {
        return javaxPersistenceUnit;
    }

    /**
     * Sets the javaxPersistenceUnit object.
     * @param javaxPersistenceUnit the object which replaced the previous one.
     */
    protected void setjavaxPersistenceUnit(final JavaxPersistenceUnit javaxPersistenceUnit) {
        this.javaxPersistenceUnit = javaxPersistenceUnit;
    }


    /**
     * Visits the end of the annotation.<br>
     * Creates the object and store it.
     */
    @Override
    public void visitEnd() {
        // set flag on field
        getAnnotationMetadata().setJavaxPersistenceUnit(javaxPersistenceUnit);
    }

    /**
     * @return type of the annotation (its description)
     */
    public String getType() {
        return TYPE;
    }

}
