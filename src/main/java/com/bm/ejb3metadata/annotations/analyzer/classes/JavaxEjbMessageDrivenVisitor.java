package com.bm.ejb3metadata.annotations.analyzer.classes;

import static com.bm.ejb3metadata.annotations.ClassType.MDB;

import org.ejb3unit.asm.jar.AnnotationVisitor;
import org.ejb3unit.asm.jar.commons.EmptyVisitor;
import org.objectweb.asm.Type;

import com.bm.ejb3metadata.annotations.analyzer.AnnotationType;
import com.bm.ejb3metadata.annotations.impl.JActivationConfigProperty;
import com.bm.ejb3metadata.annotations.impl.JMessageDriven;
import com.bm.ejb3metadata.annotations.metadata.ClassAnnotationMetadata;

/**
 * This class manages the handling of &#64;{@link javax.ejb.MessageDriven}
 * annotation.
 * @author Daniel Wiese
 */
public class JavaxEjbMessageDrivenVisitor extends AbsCommonEjbVisitor<JMessageDriven> implements AnnotationType {

    /**
     * Type of annotation.
     */
    public static final String TYPE = "Ljavax/ejb/MessageDriven;";

    /**
     * messageListenerInterface attribute of the annotation.
     */
    private static final String MESSAGE_LISTENER_INTERFACE = "messageListenerInterface";

    /**
     * activationConfig attribute of the annotation.
     */
    private static final String ACTIVATION_CONFIG = "activationConfig";

    /**
     * Message listener Interface.
     */
    private String messageListenerInterface = null;

    /**
     * Constructor.
     * @param classAnnotationMetadata linked to a class metadata
     */
    public JavaxEjbMessageDrivenVisitor(final ClassAnnotationMetadata classAnnotationMetadata) {
        super(classAnnotationMetadata);
    }

    /**
     * Visits a primitive value of the annotation.<br>
     * @param name the value name.
     * @param value the actual value, whose type must be {@link Byte},
     *        {@link Boolean}, {@link Character}, {@link Short},
     *        {@link Integer}, {@link Long}, {@link Float}, {@link Double},
     *        {@link String} or {@link Type}.
     */
    @Override
    public void visit(final String name, final Object value) {
        super.visit(name, value);
        if (name.equals(MESSAGE_LISTENER_INTERFACE)) {
            this.messageListenerInterface = ((Type) value).getInternalName();
        }
    }

    /**
     * Visits an enumeration value of the annotation.
     * @param name the value name.
     * @param desc the class descriptor of the enumeration class.
     * @param value the actual enumeration value.
     */
    @Override
    public void visitEnum(final String name, final String desc, final String value) {

    }

    /**
     * Visits a nested annotation value of the annotation.
     * @param name the value name.
     * @param desc the class descriptor of the nested annotation class.
     * @return a non null visitor to visit the actual nested annotation value.
     *         <i>The nested annotation value must be fully visited before
     *         calling other methods on this annotation visitor</i>.
     */
    @Override
    public AnnotationVisitor visitAnnotation(final String name, final String desc) {
        return this;
    }

    /**
     * Visits an array value of the annotation.
     * @param name the value name.
     * @return a non null visitor to visit the actual array value elements. The
     *         'name' parameters passed to the methods of this visitor are
     *         ignored. <i>All the array values must be visited before calling
     *         other methods on this annotation visitor</i>.
     */
    @Override
    public AnnotationVisitor visitArray(final String name) {
        if (name.equals(ACTIVATION_CONFIG)) {
            return new ActivationConfigPropertyVisitor();
        }
        return this;
    }

    /**
     * Visits the end of the annotation. Creates the object and store it
     */
    @Override
    public void visitEnd() {
        super.visitEnd();

        // message listener interface
        getJCommonBean().setMessageListenerInterface(messageListenerInterface);

        // Set type
        getAnnotationMetadata().setClassType(MDB);

    }

    /**
     * @return type of the annotation (its description)
     */
    public String getType() {
        return TYPE;
    }

    /**
     * Classes manages the parsing of activationConfig[] array of &#64;{@link javax.ejb.MessageDriven}
     * annotation.
     * @author Florent Benoit
     */
    class ActivationConfigPropertyVisitor extends EmptyVisitor {

        /**
         * Attribute for property name.
         */
        private static final String PROPERTY_NAME = "propertyName";

        /**
         * Attribute for property value.
         */
        private static final String PROPERTY_VALUE = "propertyValue";

        /**
         * Property name.
         */
        private String propertyName = null;

        /**
         * Property value.
         */
        private String propertyValue = null;

        /**
         * Visits a primitive value of the annotation.
         * @param name the value name.
         * @param value the actual value, whose type must be {@link Byte},
         *        {@link Boolean}, {@link Character}, {@link Short},
         *        {@link Integer}, {@link Long}, {@link Float},
         *        {@link Double}, {@link String} or {@link Type}.
         */
        @Override
        public void visit(final String name, final Object value) {
            if (name.equals(PROPERTY_NAME)) {
                propertyName = (String) value;
            } else if (name.equals(PROPERTY_VALUE)) {
                propertyValue = (String) value;
            }
        }

        /**
         * Visits the end of the annotation. Creates the object and store it
         */
        @Override
        public void visitEnd() {
            // Add an activation property
            JActivationConfigProperty jActivationConfigProperty = new JActivationConfigProperty(propertyName,
                    propertyValue);
            getJCommonBean().addActivationConfigProperty(jActivationConfigProperty);

        }
    }

    /**
     * @return the object representing common bean.
     */
    @Override
    public JMessageDriven getJCommonBean() {
        JMessageDriven jMessageDriven = getAnnotationMetadata().getJMessageDriven();
        if (jMessageDriven == null) {
            jMessageDriven = new JMessageDriven();
            getAnnotationMetadata().setJMessageDriven(jMessageDriven);
        }
        return jMessageDriven;
    }

}
