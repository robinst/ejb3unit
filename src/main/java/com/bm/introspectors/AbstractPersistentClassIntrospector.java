package com.bm.introspectors;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import org.apache.commons.beanutils.PropertyUtilsBean;

import com.bm.introspectors.relations.GlobalRelationStore;
import com.bm.introspectors.relations.ManyToOneReleation;
import com.bm.introspectors.relations.OneToManyReleation;
import com.bm.introspectors.relations.RelationPropertyResolver;

/**
 * This class implements the common methodes for all concrete inspectors.
 * 
 * @author Daniel Wiese
 * @param <T> -
 *            the type of the class to inspect
 * @since 07.10.2005
 */
public abstract class AbstractPersistentClassIntrospector<T> implements Introspector<T> {

    // private final static Logger log =
    // Logger.getLogger(AbstractIntrospector.class);

    /** if no lengts is defined, use a large number as default length. * */
    private static final int DEFAULTLENGTH = 652345;

    /** holds the persitent fields of the class * */
    private final List<Property> persitentFields = new ArrayList<Property>();

    /** holds the transient fields of the class * */
    private final List<Property> transientFields = new ArrayList<Property>();

    /** holds the meta information for persistent fields * */
    private final Map<Property, PersistentPropertyInfo> fieldInfo = new HashMap<Property, PersistentPropertyInfo>();

    /** holds the pk fileds of the class * */
    private final Set<Property> pkFields = new HashSet<Property>();

    /** holds the meta information for primary key fields * */
    protected final Map<Property, PrimaryKeyInfo> pkFieldInfo = new HashMap<Property, PrimaryKeyInfo>();

    private final PropertyUtilsBean propUtils = new PropertyUtilsBean();

    /** the class represented by this introspector (e.g. the entity bean)* */
    private Class<T> representingClass;

    /**
     * If the access typeis field, we will extract all the neccessary meta informations form the fields
     * 
     * @param toInspect -
     *            the class to inspect
     */
    protected void processAccessTypeField(Class<T> toInspect) {
        this.representingClass = toInspect;
        // extract meta information
        // TODO process also inherited fields???
        Field[] fields = toInspect.getDeclaredFields();
        for (Field aktField : fields) {
            // dont�s introspect fields generated by hibernate
            if (!this.isStatic(aktField) && !aktField.getName().startsWith("$")) {
                Annotation[] fieldAnnotations = aktField.getAnnotations();
                this.processAnnotations(toInspect, new Property(aktField), fieldAnnotations);
            }
        }
    }

    /**
     * If the access type is property, we will extract all the neccessary meta informations form the getter
     * mehods
     * 
     * @param toInspect -
     *            the class to inspect
     */
    protected void processAccessTypeProperty(Class<T> toInspect) {
        this.representingClass = toInspect;
        // extract meta information
        PropertyDescriptor[] properties = this.propUtils.getPropertyDescriptors(toInspect);

        for (PropertyDescriptor aktProperty : properties) {
            // dont�s introspect fields generated by hibernate
            if (!aktProperty.getReadMethod().getName().equals("getClass")) {
                Annotation[] methodAnnotations = aktProperty.getReadMethod().getAnnotations();
                this.processAnnotations(toInspect, new Property(toInspect, aktProperty), methodAnnotations);

            }
        }
    }

    /**
     * This method returns informations about a peristent field.
     * 
     * @param toCheck
     *            the property for persistent db field
     * @see com.bm.introspectors.Introspector#getPresistentFieldInfo(Property)
     * @return persistent property info
     */
    public PersistentPropertyInfo getPresistentFieldInfo(Property toCheck) {
        if (this.fieldInfo.containsKey(toCheck)) {
            return fieldInfo.get(toCheck);
        }
        throw new RuntimeException("The field " + toCheck.getName() + " is not a persitent field");

    }

    /**
     * This method returns informations about a peristent field.
     * 
     * @param toCheck
     *            the property for primary key
     * @see com.bm.introspectors.Introspector#getPrimaryKeyInfo(Property)
     * @return pk info
     */
    public PrimaryKeyInfo getPrimaryKeyInfo(Property toCheck) {
        if (this.pkFieldInfo.containsKey(toCheck)) {
            return pkFieldInfo.get(toCheck);
        }
        throw new RuntimeException("The field " + toCheck.getName() + " is not a primary key field");
    }

    /**
     * Returns the persistent fields.
     * 
     * @see com.bm.introspectors.Introspector#getPersitentFields()
     * @return the list of persistent fields
     */
    public List<Property> getPersitentFields() {
        return persitentFields;
    }

    /**
     * Return the primary key fields.
     * 
     * @see com.bm.introspectors.Introspector#getPkFields()
     * @return the list of primary key fields
     */
    public Set<Property> getPkFields() {
        return pkFields;
    }

    /**
     * Returns the transientFields.
     * 
     * @return Returns the transientFields.
     */
    public List<Property> getTransientFields() {
        return transientFields;
    }

    /**
     * Returns a value of an field.
     * 
     * @author Daniel Wiese
     * @param instance -
     *            the instance (Typed)
     * @param toGet -
     *            the parameter to get
     * @return - the value of the paremeter of the instance
     * @throws IllegalAccessException -
     *             in error case
     * @since 15.10.2005
     */
    public Object getField(T instance, Property toGet) throws IllegalAccessException {

        return toGet.getField(instance);
    }

    /**
     * Sets a value of the field.
     * 
     * @author Daniel Wiese
     * @param instance -
     *            the instance (Typed)
     * @param toSet -
     *            the parameter to set
     * @param value -
     *            the new value
     * @throws IllegalAccessException -
     *             in error case
     * @since 15.10.2005
     */
    public void setField(T instance, Property toSet, Object value) throws IllegalAccessException {
        toSet.setField(instance, value);
    }

    /**
     * Check if the field is static
     * 
     * @param toCheck
     *            -the field to check
     * @return - true if static
     */
    private boolean isStatic(Field toCheck) {
        return Modifier.isStatic(toCheck.getModifiers());

    }

    /**
     * Anylse the annotation of a (field or getterMethod)
     * 
     * @param aktProperty -
     *            the property
     * @param propertyAnnotations -
     *            the corresponding annotations
     * @param classToInspect -
     *            the class to inspect
     */
    private void processAnnotations(Class<T> classToInspect, Property aktProperty,
            Annotation[] propertyAnnotations) {
        boolean isTransient = false;

        // create a isntance if the file is a peristent field
        final PersistentPropertyInfo aktFieldInfo = new PersistentPropertyInfo();
        // initial name
        aktFieldInfo.setDbName(aktProperty.getName());
        aktFieldInfo.setLength(DEFAULTLENGTH);

        // look into the annotations
        for (Annotation a : propertyAnnotations) {
            // skip transient fields
            if (a instanceof Transient) {
                isTransient = true;
            }

            // extract column size & name
            if (a instanceof Column) {
                final Column ac = (Column) a;
                aktFieldInfo.setLength(ac.length());
                aktFieldInfo.setDbName(ac.name());
                aktFieldInfo.setNullable(ac.nullable());

            }

            // store PK field separataly
            if (a instanceof Id) {
                this.pkFields.add(aktProperty);
                PrimaryKeyInfo info = new PrimaryKeyInfo(((Id) a));
                this.extractGenerator(propertyAnnotations, info);
                this.pkFieldInfo.put(aktProperty, info);
            }

            if (a instanceof EmbeddedId) {
                this.pkFields.add(aktProperty);
                PrimaryKeyInfo info = new PrimaryKeyInfo(((EmbeddedId) a));
                this.extractGenerator(propertyAnnotations, info);
                this.pkFieldInfo.put(aktProperty, info);
            }

            if (a instanceof EmbeddedId) {
                this.pkFields.add(aktProperty);
                PrimaryKeyInfo info = new PrimaryKeyInfo(((EmbeddedId) a));
                this.pkFieldInfo.put(aktProperty, info);
            }

            // releations
            if (a instanceof OneToMany) {
                final OneToMany aC = (OneToMany) a;
                // put this property to the global store
                GlobalRelationStore.getStore().put(classToInspect, aktProperty);
                String mappedBy = aC.mappedBy();

                if (aktProperty.getGenericTypeClass() != null) {

                    // the n side> source side is a collection
                    Class<Object> ty = aktProperty.getGenericTypeClass();
                    Property relProp = RelationPropertyResolver.findAttributeForRelationAtOtherSide(
                            ty, mappedBy);
                    final OneToManyReleation o2mReleation = new OneToManyReleation(classToInspect, ty,
                            aktProperty, relProp, aC);

                    aktFieldInfo.setEntityReleationInfo(o2mReleation);

                } else {
                    throw new RuntimeException(
                            "The N part (Collection) of a OneToMany collection must be Parametrized");
                }

            } else if (a instanceof ManyToOne) {
                final ManyToOne aC = (ManyToOne) a;
                // put this property to the global store
                GlobalRelationStore.getStore().put(classToInspect, aktProperty);

                Property relProp = RelationPropertyResolver.findAttributeForRelationAtOtherSide(aktProperty);
                // the one side > the target class is decaring class
                final ManyToOneReleation o2mReleation = new ManyToOneReleation(classToInspect, relProp
                        .getDeclaringClass(), aktProperty, relProp, aC);

                aktFieldInfo.setEntityReleationInfo(o2mReleation);
            } else if (a instanceof JoinColumn) {
                //TODO fix that
            	//final JoinColumn jC = (JoinColumn) a;
                // put this property to the global store
                GlobalRelationStore.getStore().put(classToInspect, aktProperty);
            }

            // TODO create one2one introspection

        }

        if (!isTransient) {
            final Property toAdd = aktProperty;
            this.persitentFields.add(toAdd);
            this.fieldInfo.put(toAdd, aktFieldInfo);
        }
    }

    /**
     * Returns the representingClass.
     * 
     * @return Returns the representingClass.
     */
    public Class<T> getRepresentingClass() {
        return this.representingClass;
    }

    protected void extractGenerator(Annotation[] propertyAnnotations, PrimaryKeyInfo info) {
        for (Annotation current : propertyAnnotations) {
            if (current instanceof GeneratedValue) {
                info.setGenValue((GeneratedValue) current);
                break;
            }
        }
    }

}
