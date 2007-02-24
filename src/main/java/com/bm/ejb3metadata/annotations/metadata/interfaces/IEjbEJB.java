package com.bm.ejb3metadata.annotations.metadata.interfaces;

import com.bm.ejb3metadata.annotations.impl.JEjbEJB;

/**
 * This interface represents methods which can be call on
 * ClassAnnotationMetadata, MethodAnnotationMetadata and FieldAnnotationMetadata.<br>
 * It manages &#64;{@link javax.ejb.EJB} annotation.
 * @author Daniel Wiese
 */
public interface IEjbEJB {

    /**
     * @return JEjbEJB object representing javax.ejb.EJB annotation.
     */
    JEjbEJB getJEjbEJB();

    /**
     * Set jEjbEJB object.
     * @param jEjbEJB object representing javax.ejb.EJB annotation.
     */
    void setJEjbEJB(JEjbEJB jEjbEJB);

}
