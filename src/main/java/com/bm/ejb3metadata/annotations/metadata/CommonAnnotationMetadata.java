package com.bm.ejb3metadata.annotations.metadata;

import java.util.Arrays;

import com.bm.ejb3metadata.annotations.impl.JAnnotationResource;
import com.bm.ejb3metadata.annotations.impl.JEjbEJB;
import com.bm.ejb3metadata.annotations.impl.JavaxPersistenceContext;
import com.bm.ejb3metadata.annotations.impl.JavaxPersistenceUnit;
import com.bm.ejb3metadata.annotations.metadata.interfaces.ISharedMetadata;

/**
 * Defines Metadata shared by Field, Method and Classes. For example
 * &#64;javax.annotation.EJB, &#64;javax.annotation.Resource, etc.
 * @author Daniel Wiese
 */
public class CommonAnnotationMetadata implements ISharedMetadata {

    /**
     * This field is used as a PersistenceContext.
     */
    private JavaxPersistenceContext javaxPersistenceContext = null;

    /**
     * This field is used as a PersistenceUnit.
     */
    private JavaxPersistenceUnit javaxPersistenceUnit = null;

    /**
     * Object representing &#64;{@link javax.ejb.EJB} annotation.
     */
    private JEjbEJB jEjbEJB = null;

    /**
     * Object representing &#64;{@link javax.annotation.Resource} annotation.
     */
    private JAnnotationResource jAnnotationResource = null;

    /**
     * @return JEjbEJB object representing javax.ejb.EJB annotation.
     */
    public JEjbEJB getJEjbEJB() {
        return jEjbEJB;
    }

    /**
     * Set JAnnotationEJB object.
     * @param jEjbEJB object representing javax.annotation.EJB annotation.
     */
    public void setJEjbEJB(final JEjbEJB jEjbEJB) {
        this.jEjbEJB = jEjbEJB;
    }

    /**
     * @return JAnnotationResource object representing javax.annotation.Resource
     *         annotation.
     */
    public JAnnotationResource getJAnnotationResource() {
        return jAnnotationResource;
    }

    /**
     * Set JAnnotationResource object.
     * @param jAnnotationResource object representing javax.annotation.Resource
     *        annotation.
     */
    public void setJAnnotationResource(final JAnnotationResource jAnnotationResource) {
        this.jAnnotationResource = jAnnotationResource;
    }

    /**
     * @return true if this field is used as a persistence context.
     */
    public boolean isPersistenceContext() {
        return javaxPersistenceContext != null;
    }

    /**
     * @return the persistence context infos.
     */
    public JavaxPersistenceContext getJavaxPersistenceContext() {
        return javaxPersistenceContext;
    }

    /**
     * Sets the persistence context info on this field.
     * @param javaxPersistenceContext information on persistence context.
     */
    public void setJavaxPersistenceContext(final JavaxPersistenceContext javaxPersistenceContext) {
        this.javaxPersistenceContext = javaxPersistenceContext;
    }

    /**
     * @return true if this field is used as a persistence unit.
     */
    public boolean isPersistenceUnit() {
        return javaxPersistenceUnit != null;
    }

    /**
     * @return the persistence unit infos.
     */
    public JavaxPersistenceUnit getJavaxPersistenceUnit() {
        return javaxPersistenceUnit;
    }

    /**
     * Sets the persistence unit info on this field.
     * @param javaxPersistenceUnit information on persistence unit.
     */
    public void setJavaxPersistenceUnit(final JavaxPersistenceUnit javaxPersistenceUnit) {
        this.javaxPersistenceUnit = javaxPersistenceUnit;
    }

    /**
     * @return string representation
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        String titleIndent = " ";
        // classname
        sb.append(titleIndent);
        sb.append(this.getClass().getName().substring(this.getClass().getPackage().getName().length() + 1));
        sb.append("[\n");

        // jEjbEJB
        concatStringBuilder("jEjbEJB", jEjbEJB, sb);

        // jAnnotationResource
        concatStringBuilder("jAnnotationResource", jAnnotationResource, sb);

        // javaxPersistenceContext
        concatStringBuilder("javaxPersistenceContext", javaxPersistenceContext, sb);

        // javaxPersistenceUnit
        concatStringBuilder("javaxPersistenceUnit", javaxPersistenceUnit, sb);

        sb.append(titleIndent);
        sb.append("]\n");
        return sb.toString();
    }

    /**
     * Adds an entry to the given StringBuilder.
     * @param name the name of the entry.
     * @param object object to add.
     * @param sb the string builder object on which add the given element.
     * @param indent the indent to add at each line.
     */
    protected static void concatStringBuilder(final String name, final Object object, final StringBuilder sb,
            final String indent) {
        if (object != null) {
            sb.append(indent);
            sb.append(name);
            sb.append("=");
            // For arrays, use asList
            if (object instanceof Object[]) {
                sb.append(Arrays.asList((Object[]) object));
            } else {
                sb.append(object);
            }
            sb.append("\n");
        }
    }

    /**
     * Adds an entry to the given StringBuilder.
     * @param name the name of the entry.
     * @param object object to add.
     * @param sb the string builder object on which add the given element.
     */
    protected static void concatStringBuilder(final String name, final Object object, final StringBuilder sb) {
        concatStringBuilder(name, object, sb, "    ");
    }

}
