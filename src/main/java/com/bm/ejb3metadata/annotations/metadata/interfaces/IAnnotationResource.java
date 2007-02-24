package com.bm.ejb3metadata.annotations.metadata.interfaces;

import com.bm.ejb3metadata.annotations.impl.JAnnotationResource;

/**
 * This interface represents methods which can be call on
 * ClassAnnotationMetadata, MethodAnnotationMetadata and FieldAnnotationMetadata.<br>
 * It manages &#64;{@link javax.annotation.Resource} annotation.
 * @author Daniel Wiese
 */
public interface IAnnotationResource {

    /**
     * @return JAnnotationResource object representing javax.annotation.Resource annotation.
     */
    JAnnotationResource getJAnnotationResource();

    /**
     * Set JAnnotationResource object.
     * @param jAnnotationResource object representing javax.annotation.Resource annotation.
     */
    void setJAnnotationResource(JAnnotationResource jAnnotationResource);

}
