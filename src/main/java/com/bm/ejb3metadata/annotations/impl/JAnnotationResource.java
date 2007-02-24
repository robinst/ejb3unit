package com.bm.ejb3metadata.annotations.impl;

import javax.annotation.Resource.AuthenticationType;


/**
 * Acts as an implementation of &#64;{@link javax.annotation.Resource} annotation.
 * @author Daniel Wiese
 */
public class JAnnotationResource {

    /**
     * Name.
     */
    private String name = null;

    /**
     * Type (class).
     */
    private String type = null;

    /**
     * Authentication type.
     */
    private AuthenticationType authenticationType = null;

    /**
     * Shareable (true/false).
     */
    private boolean shareable;

    /**
     * Description.
     */
    private String description = null;

    /**
     * mapped name.
     */
    private String mappedName = null;

    /**
     * Constructor.<br>
     * Build object with default values.
     */
    public JAnnotationResource() {
        // set default values
        this.name = "";
        this.type = "java/lang/Object";
        this.authenticationType = AuthenticationType.CONTAINER;
        shareable = true;
        description = "";
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
     * @return the authentication type.
     */
    public AuthenticationType getAuthenticationType() {
        return authenticationType;
    }


    /**
     * Sets the authentication type.
     * @param authenticationType value to set.
     */
    public void setAuthenticationType(final AuthenticationType authenticationType) {
        this.authenticationType = authenticationType;
    }


    /**
     * @return the description.
     */
    public String getDescription() {
        return description;
    }


    /**
     * Sets the description.
     * @param description value to set.
     */
    public void setDescription(final String description) {
        this.description = description;
    }


    /**
     * @return true if it is shareable.
     */
    public boolean isShareable() {
        return shareable;
    }


    /**
     * Sets the shareable attribute (false/true).
     * @param shareable a boolean.
     */
    public void setShareable(final boolean shareable) {
        this.shareable = shareable;
    }


    /**
     * @return the type of resource (class).
     */
    public String getType() {
        return type;
    }


    /**
     * Sets the class type of this object.
     * @param type the class value (as string format).
     */
    public void setType(final String type) {
        this.type = type;
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

        // type
        sb.append(", type=");
        sb.append(type);

        // Authentication type
        sb.append(", authenticationType=");
        sb.append(authenticationType);

        // Shareable
        sb.append(", shareable=");
        sb.append(shareable);

        // Description
        sb.append(", description=");
        sb.append(description);

        // MappedName
        sb.append(", mappedName=");
        sb.append(mappedName);

        sb.append("]");
        return sb.toString();
    }

}
