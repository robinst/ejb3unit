package com.bm.ejb3metadata.annotations.analyzer;

import org.ejb3unit.asm.Type;

import com.bm.ejb3metadata.annotations.impl.JEjbEJB;
import com.bm.ejb3metadata.annotations.metadata.interfaces.IEjbEJB;

/**
 * This class manages the handling of &#64;{@link javax.ejb.EJB} annotation.
 * @param <T> An implementation of IAnnotationEJB interface.
 * @author Daniel Wiese
 */
public class JavaxEjbEJBVisitor<T extends IEjbEJB>
          extends AbsAnnotationVisitor<T> implements AnnotationType {

    /**
     * Name attribute of the annotation.
     */
    private static final String NAME = "name";

    /**
     * Bean interface attribute of the annotation.
     */
    private static final String BEAN_INTERFACE = "beanInterface";

    /**
     * Bean name attribute of the annotation.
     */
    private static final String BEAN_NAME = "beanName";

    /**
     * Mapped name attribute of the annotation.
     */
    private static final String MAPPED_NAME = "mappedName";

    /**
     * Type of annotation.
     */
    public static final String TYPE = "Ljavax/ejb/EJB;";

    /**
     * Internal object used representing &#64;{@link javax.ejb.EJB} annotation.
     */
    private JEjbEJB jEjbEJB = null;

    /**
     * Constructor.
     * @param annotationMetadata linked to a class or method metadata
     */
    public JavaxEjbEJBVisitor(final T annotationMetadata) {
        super(annotationMetadata);
        jEjbEJB = new JEjbEJB();
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
            jEjbEJB.setName((String) value);
        } else if (name.equals(BEAN_INTERFACE)) {
            Type type = (Type) value;
            jEjbEJB.setBeanInterface(type.getClassName());
        } else if (name.equals(BEAN_NAME)) {
            jEjbEJB.setBeanName((String) value);
        } else if (name.equals(MAPPED_NAME)) {
            jEjbEJB.setMappedName((String) value);
        }
    }

    /**
     * Visits the end of the annotation. <br>
     * Creates the object and store it.
     */
    @Override
    public void visitEnd() {
        // add object
        getAnnotationMetadata().setJEjbEJB(jEjbEJB);
    }

    /**
     * @return type of the annotation (its description)
     */
    public String getType() {
        return TYPE;
    }


    /**
     * @return Internal object used representing &#64;{@link javax.ejb.EJB} annotation.
     */
    protected JEjbEJB getJEjbEJB() {
        return jEjbEJB;
    }

    /**
     * Sets the jEjbEJB object.
     * @param jEjbEJB the object which replaced the previous one.
     */
    protected void setJEjbEJB(final JEjbEJB jEjbEJB) {
        this.jEjbEJB = jEjbEJB;
    }

}
