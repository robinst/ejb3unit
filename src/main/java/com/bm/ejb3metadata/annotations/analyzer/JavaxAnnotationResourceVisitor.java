package com.bm.ejb3metadata.annotations.analyzer;

import org.objectweb.asm.Type;

import com.bm.ejb3metadata.annotations.impl.JAnnotationResource;
import com.bm.ejb3metadata.annotations.metadata.interfaces.IAnnotationResource;

/**
 * This class manages the handling of &#64;{@link javax.annotation.Resource}
 * annotation.
 * @param <T> An implementation of IAnnotationResource interface.
 * @author Daniel Wiese
 */
public class JavaxAnnotationResourceVisitor<T extends IAnnotationResource> extends AbsAnnotationVisitor<T> implements
        AnnotationType {

    /**
     * Name attribute of the annotation.
     */
    private static final String NAME = "name";

    /**
     * Class with type attribute of the annotation.
     */
    private static final String CLASS_TYPE = "type";

    /**
     * Atuthentication type attribute of the annotation.
     */
    private static final String AUTHENTICATION_TYPE = "authenticationType";

    /**
     * Shareable attribute of the annotation.
     */
    private static final String SHAREABLE = "shareable";

    /**
     * Description of the annotation.
     */
    private static final String DESCRIPTION = "description";

    /**
     * Mapped name attribute of the annotation.
     */
    private static final String MAPPED_NAME = "mappedName";

    /**
     * Type of annotation.
     */
    public static final String TYPE = "Ljavax/annotation/Resource;";

    /**
     * Internal object used representing &#64;{@link javax.annotation.Resource}
     * annotation.
     */
    private JAnnotationResource jAnnotationResource = null;

    /**
     * Constructor.
     * @param annotationMetadata linked to a class or method metadata
     */
    public JavaxAnnotationResourceVisitor(final T annotationMetadata) {
        super(annotationMetadata);
        jAnnotationResource = new JAnnotationResource();
    }

    /**
     * Visits a primitive value of the annotation.<br>
     * @param name the value name.
     * @param value the actual value, whose type must be {@link Byte},
     *        {@link Boolean}, {@link Character}, {@link Short},
     *        {@link Integer}, {@link Long}, {@link Float}, {@link Double},
     *        {@link String} or {@link org.ejb3unit.asm.Type}.
     */
    @Override
    public void visit(final String name, final Object value) {
        if (name.equals(NAME)) {
            jAnnotationResource.setName((String) value);
        } else if (name.equals(CLASS_TYPE)) {
            Type type = (Type) value;
            jAnnotationResource.setType(type.getClassName());
        } else if (name.equals(SHAREABLE)) {
            jAnnotationResource.setShareable(((Boolean) value).booleanValue());
        } else if (name.equals(DESCRIPTION)) {
            jAnnotationResource.setDescription((String) value);
        } else if (name.equals(MAPPED_NAME)) {
            jAnnotationResource.setMappedName((String) value);
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
        if (name.equals(AUTHENTICATION_TYPE)) {
        	/**
            if (CONTAINER.name().equals(value)) {
                jAnnotationResource.setAuthenticationType(CONTAINER);
            } else if (APPLICATION.name().equals(value)) {
                jAnnotationResource.setAuthenticationType(APPLICATION);
            }
            **/
        }
    }

    /**
     * Visits the end of the annotation. <br>
     * Creates the object and store it.
     */
    @Override
    public void visitEnd() {
        // add object
        getAnnotationMetadata().setJAnnotationResource(jAnnotationResource);
    }

    /**
     * @return type of the annotation (its description)
     */
    public String getType() {
        return TYPE;
    }

    /**
     * @return Internal object used representing &#64;{@link javax.annotation.Resource}
     *         annotation.
     */
    protected JAnnotationResource getJAnnotationResource() {
        return jAnnotationResource;
    }

    /**
     * Sets the jAnnotationResource object.
     * @param jAnnotationResource the object which replaced the previous one.
     */
    protected void setJAnnotationResource(final JAnnotationResource jAnnotationResource) {
        this.jAnnotationResource = jAnnotationResource;
    }

}
