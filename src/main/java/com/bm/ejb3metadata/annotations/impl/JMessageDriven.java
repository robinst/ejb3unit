package com.bm.ejb3metadata.annotations.impl;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.ActivationConfigProperty;

/**
 * Acts as an implementation of &#64;{@link javax.ejb.MessageDriven} annotation.
 * @author Daniel Wiese
 */
public class JMessageDriven extends JCommonBean {

    /**
     * List of ActivationConfigProperty.
     */
    private List<ActivationConfigProperty> activationConfigProperties = null;

    /**
     * Message listener Interface.
     */
    private String messageListenerInterface = null;

    /**
     * Build an object which represents &#64;{@link javax.ejb.MessageDriven} object.
     */
    public JMessageDriven() {
        super();
        activationConfigProperties = new ArrayList<ActivationConfigProperty>();
    }

    /**
     * Adds an activation config property.
     * @param activationConfigProperty object to add in the list
     */
    public void addActivationConfigProperty(final ActivationConfigProperty activationConfigProperty) {
        activationConfigProperties.add(activationConfigProperty);
    }

    /**
     * Gets the activation config properties.
     * @return the list of activation config properties
     */
    public List<ActivationConfigProperty> getActivationConfigProperties() {
        return activationConfigProperties;
    }

    /**
     * @return message listener interface.
     */
    public String getMessageListenerInterface() {
        return messageListenerInterface;
    }

    /**
     * Sets the message listener interface.
     * @param messageListenerInterface the given interface.
     */
    public void setMessageListenerInterface(final String messageListenerInterface) {
        this.messageListenerInterface = messageListenerInterface;
    }

    /**
     * @return string representation
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        // classname
        sb.append(this.getClass().getName().substring(this.getClass().getPackage().getName().length() + 1));

        sb.append(super.toString());

        // messageListenerInterface
        sb.append("[messageListenerInterface=");
        sb.append(messageListenerInterface);

        // property value
        sb.append(", activationConfigProperties=");
        sb.append(activationConfigProperties);

        sb.append("]");
        return sb.toString();
    }
}
