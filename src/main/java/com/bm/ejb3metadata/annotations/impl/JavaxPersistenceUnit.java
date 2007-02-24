package com.bm.ejb3metadata.annotations.impl;


/**
 * This class allow to set informations on javax.persistence.PersistenceUnit
 * annotation.
 * @author Daniel Wiese
 */
public class JavaxPersistenceUnit {

    /**
     * Name of this persistence unit.
     */
    private String name = null;

    /**
     * Unit name of this persistence context.
     */
    private String unitName = null;

    /**
     * Build new object with default values.
     */
    public JavaxPersistenceUnit() {
        // default values
        this.unitName = "";
        this.name = "";
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

}
