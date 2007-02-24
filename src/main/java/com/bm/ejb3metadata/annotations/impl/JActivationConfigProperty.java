package com.bm.ejb3metadata.annotations.impl;

import java.lang.annotation.Annotation;

import javax.ejb.ActivationConfigProperty;

/**
 * Acts as an implementation of &#64;{@link javax.ejb.ActivationConfigProperty} annotation.
 * @author Daniel Wiese
 */
public class JActivationConfigProperty implements ActivationConfigProperty {

    /**
     * Name.
     */
    private String name = null;

    /**
     * Value.
     */
    private String value = null;

    /**
     * Constructor.<br>
     * Build an object with a given name and a given value.
     * @param name given name
     * @param value given value
     */
    public JActivationConfigProperty(final String name, final String value) {
        this.name = name;
        this.value = value;
    }


    /**
     * @return property name
     */
    public String propertyName() {
        return name;
    }

    /**
     * @return property value
     */
    public String propertyValue() {
        return value;
    }

    /**
     * @return annotation type
     */
    public Class<? extends Annotation> annotationType() {
        return ActivationConfigProperty.class;
    }

    /**
     * @return string representation
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        // classname
        sb.append(this.getClass().getName().substring(this.getClass().getPackage().getName().length() + 1));
        // property name
        sb.append("[propertyName=");
        sb.append(name);

        // property value
        sb.append(", propertyValue=");
        sb.append(value);

        sb.append("]");
        return sb.toString();
    }
}
