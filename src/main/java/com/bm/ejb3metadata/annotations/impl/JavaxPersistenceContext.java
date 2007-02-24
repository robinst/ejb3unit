package com.bm.ejb3metadata.annotations.impl;

import javax.persistence.PersistenceContextType;

/**
 * This class allow to set informations on javax.persistence.PersistenceContext
 * annotation.
 * @author Daniel Wiese
 */
public class JavaxPersistenceContext {

    /**
     * Name of this persistence context.
     */
    private String name = null;

    /**
     * Unit name of this persistence context.
     */
    private String unitName = null;

    /**
     * Type of persistence context.
     */
    private PersistenceContextType type = null;

    /**
     * Build new object with default values.
     */
    public JavaxPersistenceContext() {
        // default values
        this.name = "";
        this.unitName = "";
        this.type = PersistenceContextType.TRANSACTION;
    }

    /**
     * @return the type of persistence context.
     */
    public PersistenceContextType getType() {
        return type;
    }

    /**
     * Sets the persistence context type.
     * @param type given type.
     */
    public void setType(final PersistenceContextType type) {
        this.type = type;
    }

    /**
     * @return the unit name used by this persistence context.
     */
    public String getUnitName() {
        return unitName;
    }

    /**
     * sets the unit name of this persistence context.
     * @param unitName the name of the persistence unit
     */
    public void setUnitName(final String unitName) {
        this.unitName = unitName;
    }

    /**
     * @return the unit name used by this persistence context.
     */
    public String getName() {
        return name;
    }

    /**
     * sets the name of this persistence context.
     * @param name the name of the persistence context
     */
    public void setName(final String name) {
        this.name = name;
    }

}
