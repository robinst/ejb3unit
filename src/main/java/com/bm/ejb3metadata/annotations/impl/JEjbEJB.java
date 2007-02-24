package com.bm.ejb3metadata.annotations.impl;


/**
 * Acts as an implementation of &#64;{@link javax.ejb.EJB} annotation.
 * @author Daniel Wiese
 */
public class JEjbEJB {

    /**
     * Name (resource to be looked up).
     */
    private String name = null;

    /**
     * Business or home Interface.
     */
    private String beanInterface = null;

    /**
     * Bean name (Name of stateless, stateful, etc. or ejb-name).
     */
    private String beanName = null;

    /**
     * mapped name.
     */
    private String mappedName = null;


    /**
     * Constructor.<br>
     * Build object with default values.
     */
    public JEjbEJB() {
        this.name = "";
        this.beanName = "";
        this.mappedName = null;
    }

    /**
     * @return Name (resource to be looked up).
     */
    public String getName() {
        return name;
    }

    /**
     * Sets Name (resource to be looked up).
     * @param name the given name.
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * @return Bean name (Name of stateless, stateful, etc. or ejb-name).
     */
    public String getBeanName() {
        return beanName;
    }

    /**
     * Sets bean name (Name of stateless, stateful, etc. or ejb-name).
     * @param beanName the given name.
     */
    public void setBeanName(final String beanName) {
        this.beanName = beanName;
    }


    /**
     * @return business or home Interface.
     */
    public String getBeanInterface() {
        return beanInterface;
    }

    /**
     * Sets the business or home Interface.
     * @param beanInterface the given interface.
     */
    public void setBeanInterface(final String beanInterface) {
        this.beanInterface = beanInterface;
    }


    /**
     * @return MappedName.
     */
    public String getMappedName() {
        return mappedName;
    }

    /**
     * Sets mapped Name.
     * @param mappedName the given mappedName.
     */
    public void setMappedName(final String mappedName) {
        this.mappedName = mappedName;
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
        sb.append(", mappedName=");
        sb.append(mappedName);

        // beanInterface
        sb.append(", beanInterface=");
        sb.append(beanInterface);

        // property value
        sb.append(", beanName=");
        sb.append(beanName);

        sb.append("]");
        return sb.toString();
    }
}
