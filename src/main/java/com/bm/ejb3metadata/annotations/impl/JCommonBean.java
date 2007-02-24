package com.bm.ejb3metadata.annotations.impl;


/**
 * Defines common methods used by Session Bean and Message Driven beans.
 * @author Daniel Wiese
 */
public class JCommonBean {

    /**
     * Name of the bean.
     */
    private String name = null;

    /**
     * Mapped name (could be used as JNDI name).
     */
    private String mappedName = null;

    /**
     * Description.
     */
    private String description = null;

    /**
     * Build an object that will be shared by EJB (Session + MDB).
     */
    public JCommonBean() {

    }

    /**
     * @return the description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description.
     * @param description value of description
     */
    public void setDescription(final String description) {
        this.description = description;
    }

    /**
     * @return the mapped name (JNDI ?)
     */
    public String getMappedName() {
        return mappedName;
    }

    /**
     * Sets the mapped name.
     * @param mappedName the value to set
     */
    public void setMappedName(final String mappedName) {
        this.mappedName = mappedName;
    }

    /**
     * @return name of the bean.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the bean name.
     * @param name the bean's name
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * @return string representation
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        // classname
        sb.append(this.getClass().getName().substring(this.getClass().getPackage().getName().length() + 1));

        // name
        sb.append("[name=");
        sb.append(name);

        // mappedName
        sb.append("[mappedName=");
        sb.append(mappedName);

        // description
        sb.append("[description=");
        sb.append(description);

        sb.append("]");
        return sb.toString();
    }
}
