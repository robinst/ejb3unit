package com.bm.ejb3metadata.annotations.metadata.interfaces;

import com.bm.ejb3metadata.annotations.impl.JavaxPersistenceContext;

/**
 * This interface represents methods which can be call on
 * ClassAnnotationMetadata, MethodAnnotationMetadata and FieldAnnotationMetadata.<br>
 * It manages &#64;{@link javax.persistence.PersistenceContext} annotation.
 * @author Daniel Wiese
 */
public interface IPersistenceContext {
    /**
     * @return true if this field is used as a persistence context.
     */
     boolean isPersistenceContext();

    /**
     * @return the persistence context infos.
     */
    JavaxPersistenceContext getJavaxPersistenceContext();

    /**
     * Sets the persistence context info on this field.
     * @param javaxPersistenceContext information on persistence context.
     */
    void setJavaxPersistenceContext(JavaxPersistenceContext javaxPersistenceContext);
}
