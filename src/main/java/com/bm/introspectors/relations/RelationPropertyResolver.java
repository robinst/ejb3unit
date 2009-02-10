package com.bm.introspectors.relations;



import com.bm.introspectors.EntityBeanIntrospector;
import com.bm.introspectors.Property;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Helper class to find properties wich are inwolved in releations.
 * 
 * @author Daniel Wiese
 * 
 */
public final class RelationPropertyResolver {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(RelationPropertyResolver.class);

    private RelationPropertyResolver() {
        // empty to avoid contructions
    }

    /**
     * Search the attribute representing the releation on the !other side!
     * 
     * @param declaredInClass -
     *            the other side class holding the mappedBy attribute
     * @param mappedBy -
     *            the attribute name mapping the property (on the other side)
     * @return - the property (if found)
     */
    public static Property findAttributeForRelationAtOtherSide(Class<Object> declaredInClass, String mappedBy) {
        // now look for the other if exist at the global store
        log.debug("declaredInClass=" + declaredInClass.getName());
        Property relProp = GlobalRelationStore.getStore().getProperty(declaredInClass, mappedBy);
        if (relProp == null) {
            // use a new instrospector to put the relation to the global
            // store
            final EntityBeanIntrospector<Object> tmpIn = EntityBeanIntrospector.getEntityBeanIntrospector(declaredInClass);
            log.debug("Dependent class: " + tmpIn.getTableName());
            // now it should be in theglobal store

            relProp = GlobalRelationStore.getStore().getProperty(declaredInClass, mappedBy);
        }
        return relProp;
    }

    /**
     * Search the attribute representing the relation on the !other side!
     * 
     * @param aktProperty -
     *            the current property representing this side
     * @return - the property (if found)
     */
    @SuppressWarnings("unchecked")
    public static Property findAttributeForRelationAtOtherSide(Property aktProperty) {
        // now look for the other if exist at the global store
        // WIR M�SSEN DIE N SEITE FINDEN: die als target uns hat (aktProperty)
        // TODO WIR M�SSEN DIE N SEITE FINDEN:
        Property relProp = GlobalRelationStore.getStore().getProperty(aktProperty.getType(),
                aktProperty.getDeclaringClass());
        if (relProp == null) {
            // use a new instrospector to put the relation to the global
            // store
            final EntityBeanIntrospector<Object> tmpIn = EntityBeanIntrospector.getEntityBeanIntrospector(aktProperty
                    .getType());
            
            // now it should be in the global store
            relProp = GlobalRelationStore.getStore().getProperty(aktProperty.getType(),
                    aktProperty.getDeclaringClass());
        }
        return relProp;
    }
    
    public static Set<Property> findPkAttributesAtTheOtherSide(Property aktProperty) {
        @SuppressWarnings("unchecked")
        final EntityBeanIntrospector<Object> tmpIn = EntityBeanIntrospector.getEntityBeanIntrospector(aktProperty.
                getType());
        Set<Property> pkFields = tmpIn.getPkFields();
        
        return pkFields;
    }

}
