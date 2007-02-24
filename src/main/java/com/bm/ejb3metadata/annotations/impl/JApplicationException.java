package com.bm.ejb3metadata.annotations.impl;

import java.lang.annotation.Annotation;

import javax.ejb.ApplicationException;

/**
 * Acts as an implementation of &#64;{@link javax.ejb.ApplicationException} annotation.
 * @author Daniel Wiese
 */
public class JApplicationException implements ApplicationException {

    /**
     * Container rollback the transaction ? if an exception is thrown.
     */
    private boolean rollback = false;

    /**
     * Build an object which represents &#64;{@link javax.ejb.ApplicationException} annotation.<br>
     * Default value for rollback is false.
     */
    public JApplicationException() {
        this(false);
    }

    /**
     * Build an object which represents &#64;{@link javax.ejb.ApplicationException} annotation with a given boolean.<br>
     * @param rollback true/false (false is a default value)
     */
    public JApplicationException(final boolean rollback) {
        this.rollback = rollback;
    }

    /**
     * @return the retainIfException value (true/false)
     */
    public boolean rollback() {
        return rollback;
    }

    /**
     * @return annotation type
     */
    public Class<? extends Annotation> annotationType() {
        return ApplicationException.class;
    }

    /**
     * @return string representation
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        // classname
        sb.append(this.getClass().getName().substring(this.getClass().getPackage().getName().length() + 1));
        // rollback
        sb.append("[rollback=");
        sb.append(rollback);

        sb.append("]");
        return sb.toString();
    }
}
