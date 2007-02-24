package com.bm.ejb3metadata.annotations.analyzer.classes;

import com.bm.ejb3metadata.annotations.analyzer.AbsAnnotationVisitor;
import com.bm.ejb3metadata.annotations.impl.JCommonBean;
import com.bm.ejb3metadata.annotations.metadata.ClassAnnotationMetadata;
/**
 * This class manages the handling of common annotations used by beans.
 * @param <T> a class extending JCommonBean.
 * @author Daniel Wiese
 */
public abstract class AbsCommonEjbVisitor<T extends JCommonBean> extends AbsAnnotationVisitor<ClassAnnotationMetadata> {

    /**
     * Name attribute of the annotation.
     */
    private static final String NAME = "name";

    /**
     * mappedName attribute of the annotation.
     */
    private static final String MAPPED_NAME = "mappedName";

    /**
     * description attribute of the annotation.
     */
    private static final String DESCRIPTION = "description";

     /**
     * Constructor.
     * @param classAnnotationMetadata linked to a class metadata
     */
    public AbsCommonEjbVisitor(final ClassAnnotationMetadata classAnnotationMetadata) {
        super(classAnnotationMetadata);
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
            getJCommonBean().setName((String) value);
        } else if (name.equals(MAPPED_NAME)) {
            getJCommonBean().setMappedName((String) value);
        } else if (name.equals(DESCRIPTION)) {
            getJCommonBean().setDescription((String) value);
        }
    }

    /**
     * Visits the end of the annotation. <br> Creates the object and store it.
     */
    @Override
    public void visitEnd() {
        getAnnotationMetadata().setJCommonBean(getJCommonBean());
    }

    /**
     * @return the object used by all beans.
     */
    public abstract T getJCommonBean();


}
