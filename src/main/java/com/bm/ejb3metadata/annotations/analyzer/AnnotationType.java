package com.bm.ejb3metadata.annotations.analyzer;

/**
 * Interface allowing to know the type of annotation.<br>
 * ie : "Ljavax/ejb/Local;" for &#64;{@link javax.ejb.Local}
 * @author Daniel Wiese
 */
public interface AnnotationType {

    /**
     * @return type of the annotation (its description).
     */
    String getType();
}
