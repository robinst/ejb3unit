package com.bm.ejb3metadata.annotations.impl;

import java.util.ArrayList;
import java.util.List;


/**
 * List of interceptor classes.
 * @author Daniel Wiese
 */
public class JInterceptors {

    /**
     * List of classes.
     */
    private List<String> classes = null;

    /**
     * Constructor.
     */
    public JInterceptors() {
        classes = new ArrayList<String>();
    }

    /**
     * Add a class.
     * @param cls name of the class (asm)
     */
    public void addClass(final String cls) {
        classes.add(cls);
    }

    /**
     * @return list of classes.
     */
    public List<String> getClasses() {
        return classes;
    }

    /**
     * @param cls the name of an encoded class.
     * @return true if the class is already in the list.
     */
    public boolean contains(final String cls) {
        return classes.contains(cls);
    }

    /**
     * @return string representation
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        // classname
        sb.append(this.getClass().getName().substring(this.getClass().getPackage().getName().length() + 1));

        // list
        sb.append(classes);
        return sb.toString();
    }

}
