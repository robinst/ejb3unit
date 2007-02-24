package com.bm.ejb3metadata.annotations.impl;

import java.util.ArrayList;
import java.util.List;

/**
 * List of interfaces.
 * @author Daniel Wiese
 */
public abstract class JInterface {

    /**
     * List of interfaces.
     */
    private List<String> interfaces = null;

    /**
     * Constructor.
     */
    public JInterface() {
        interfaces = new ArrayList<String>();
    }

    /**
     * Add an interface.
     * @param itf name of the interface (asm)
     */
    public void addInterface(final String itf) {
        interfaces.add(itf);
    }

    /**
     * @return list of interfaces.
     */
    public List<String> getInterfaces() {
        return interfaces;
    }

    /**
     * @param itf the name of an encoded interface.
     * @return true if the interface is already in the list.
     */
    public boolean contains(final String itf) {
        return interfaces.contains(itf);
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
        sb.append(interfaces);
        return sb.toString();
    }
}
