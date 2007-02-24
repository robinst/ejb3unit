package com.bm.ejb3metadata.annotations.impl;

import java.lang.annotation.Annotation;

import javax.ejb.Remove;

/**
 * Acts as an implementation of &#64;{@link javax.ejb.Remove} annotation.
 * @author Daniel Wiese
 */
public class JRemove implements Remove {

    /**
     * Don't remove object if there is an application exception.
     */
    private boolean retainIfException = false;

    /**
     * Build an object which represents&#64;{@link javax.ejb.Remove}.<br>
     * Default value for retainIfException is false.
     */
    public JRemove() {
        this(false);
    }

    /**
     * Build an object which represents&#64;{@link javax.ejb.Remove} with a
     * given boolean.
     * @param retainIfException true/false (false is a default value)
     */
    public JRemove(final boolean retainIfException) {
        this.retainIfException = retainIfException;
    }

    /**
     * @return the retainIfException value (true/false).
     */
    public boolean retainIfException() {
        return retainIfException;
    }

    /**
     * @return annotation type.
     */
    public Class<? extends Annotation> annotationType() {
        return Remove.class;
    }

    /**
     * @return string representation.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        // classname
        sb.append(this.getClass().getName().substring(this.getClass().getPackage().getName().length() + 1));
        // retainIfException
        sb.append("[retainIfException=");
        sb.append(retainIfException);

        sb.append("]");
        return sb.toString();
    }
}
