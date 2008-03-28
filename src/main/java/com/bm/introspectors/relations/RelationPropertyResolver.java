package com.bm.introspectors.relations;

import org.apache.log4j.Logger;

import com.bm.introspectors.EntityBeanIntrospector;
import com.bm.introspectors.Property;

/**
 * Helper class to find properties wich are inwolved in releations.
 * 
 * @author Daniel Wiese
 * 
 */
public final class RelationPropertyResolver {

    private static final Logger log = Logger.getLogger(RelationPropertyResolver.class);

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
        Property relProp = GlobalRelationStore.getStore().getProperty(declaredInClass, mappedBy);
        if (relProp == null) {
            // use a new instrospector to put the relation to the global
            // store
            final EntityBeanIntrospector<Object> tmpIn = new EntityBeanIntrospector<Object>(declaredInClass);
            log.debug("Dependend class: " + tmpIn.getTableName());
            // now it should be in theglobal store

            relProp = GlobalRelationStore.getStore().getProperty(declaredInClass, mappedBy);
            if (relProp == null) {
                log.debug("The relation is unidirectional. Cant´t resolve releations for property (" + mappedBy
                        + ") decared in class (" + declaredInClass.getName() + ")");
            }
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
        // WIR MÜSSEN DIE N SEITE FINDEN: die als target uns hat (aktProperty)
        // TODO WIR MÜSSEN DIE N SEITE FINDEN:
        Property relProp = GlobalRelationStore.getStore().getProperty(aktProperty.getType(),
                aktProperty.getDeclaringClass());
        if (relProp == null) {
            // use a new instrospector to put the relation to the global
            // store
            final EntityBeanIntrospector<Object> tmpIn = new EntityBeanIntrospector<Object>(aktProperty
                    .getType());
            log.debug("Dependend class: " + tmpIn.getTableName());
            // now it should be in the global store
            relProp = GlobalRelationStore.getStore().getProperty(aktProperty.getType(),
                    aktProperty.getDeclaringClass());
            if (relProp == null) {
                throw new RuntimeException("Cant't resolve releations for property (" + aktProperty.getName()
                        + ") in class (" + aktProperty.getDeclaringClass() + ")");
            }
        }
        return relProp;
    }

}
