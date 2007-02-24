package com.bm.ejb3metadata.annotations.metadata.interfaces;

import com.bm.ejb3metadata.annotations.impl.JavaxPersistenceUnit;

/**
 * This interface represents methods which can be call on
 * ClassAnnotationMetadata, MethodAnnotationMetadata and FieldAnnotationMetadata.<br>
 * It manages &#64;{@link javax.persistence.PersistenceUnit} annotation.
 * @author Daniel Wiese
 */
public interface IPersistenceUnit {

    /**
     * @return true if this field is used as a persistence unit.
     */
    boolean isPersistenceUnit();

    /**
     * @return the persistence unit infos.
     */
    JavaxPersistenceUnit getJavaxPersistenceUnit();

    /**
     * Sets the persistence unit info on this field.
     * @param javaxPersistenceUnit information on persistence unit.
     */
    void setJavaxPersistenceUnit(JavaxPersistenceUnit javaxPersistenceUnit);

}
