package com.bm.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.bm.introspectors.EmbeddedClassIntrospector;
import com.bm.introspectors.EntityBeanIntrospector;
import com.bm.introspectors.Property;
import com.bm.introspectors.relations.EntityReleationInfo;
import com.bm.introspectors.relations.ManyToOneReleation;
import com.bm.introspectors.relations.OneToManyReleation;
import com.bm.introspectors.relations.RelationType;

/**
 * This class generates a undo script for all DB oprations excuted in jUnit. tests
 * 
 * @author Daniel Wiese
 * @param <T> -
 *            the type of the entity bean
 * @since 07.10.2005
 */
public class UndoScriptGenerator<T> {

    private static final Logger log = Logger.getLogger(UndoScriptGenerator.class);

    private final Set<T> createdObjects = new HashSet<T>();

    private final List<String> createdSQLScripts = new ArrayList<String>();

    private final EntityBeanIntrospector<T> inspector;

    private UndoScriptGenerator rootGenerator = null;

    /**
     * if a bean has releated bens, a undo generatro for each releation is constructed
     */
    private final Map<Class, UndoScriptGenerator> subUndoGens = new HashMap<Class, UndoScriptGenerator>();
    /**
     * if a bean has releated bens, a undo generatro for each releation is constructed
     */
    private final Map<Class, DeleteOrder> subUndoGensOrder = new HashMap<Class, DeleteOrder>();

    /**
     * Default constructor.
     * 
     * @param inspector -
     *            the inspector
     */
    public UndoScriptGenerator(EntityBeanIntrospector<T> inspector) {
        this(inspector, null);
    }

    /**
     * Default constructor.
     * 
     * @param inspector -
     *            the inspector
     * @param root -
     *            the root generator
     */
    private UndoScriptGenerator(EntityBeanIntrospector<T> inspector, UndoScriptGenerator root) {
        this.inspector = inspector;
        this.rootGenerator = root;
    }

    /**
     * Returns the createdObjects.
     * 
     * @return Returns the createdObjects.
     */
    public List<Object> getCreatedObjects() {
        final List<Object> back = new ArrayList<Object>();
        addSubDeleteObjects(back, DeleteOrder.DELETE_FIRST);
        back.addAll(this.createdObjects);
        addSubDeleteObjects(back, DeleteOrder.DELETE_AFTER);
        back.addAll(this.createdObjects);

        return back;
    }

    /**
     * This method returns a delete all statement for the table (bean type). ATTENTION: This method will not
     * delete referenced collections
     * 
     * @return delete all statement
     */
    public String getOneDeleteAllStatement() {
        final StringBuilder sb = new StringBuilder();
        sb.append("DELETE FROM ").append(this.inspector.getTableName());
        return sb.toString();
    }

    /**
     * Returns a list of SQL undo statements.
     * 
     * @return SQL statements
     */
    public List<String> getSQLUndoStatements() {
        final List<String> back = new ArrayList<String>();
        // rekursive solution
        addSubDeleteScripts(back, DeleteOrder.DELETE_FIRST);
        back.addAll(this.createdSQLScripts);
        addSubDeleteScripts(back, DeleteOrder.DELETE_AFTER);

        return back;
    }

    /**
     * This method protocoll a bean creation.
     * 
     * @param toCreate -
     *            the ben wich sould be protocolled as created
     */
    public void protokollCreate(T toCreate) {
        final StringBuilder sb = new StringBuilder();
        this.createdObjects.add(toCreate);
        sb.append("DELETE FROM ").append(this.inspector.getTableName()).append(" WHERE ");
        sb.append(this.getPkCondition(toCreate));
        this.createdSQLScripts.add(sb.toString());

        // now iterate over persistent field and search for
        // 1:N, N:1 or 1:! releations
        List<Property> fields = this.inspector.getPersitentFields();
        for (Property akt : fields) {
            EntityReleationInfo eri = this.inspector.getPresistentFieldInfo(akt).getEntityReleationInfo();
            // don't store unidirectional relations
            if (eri != null && this.isRootGenerator()) {
                this.processReletatedObjects(eri, toCreate);
            }
        }

    }

    /**
     * The toString method- returns the undo-script.
     * 
     * @return - list of undo statements
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (String akt : this.createdSQLScripts) {
            sb.append(akt).append("\n");
        }

        return sb.toString();
    }

    private void addSubDeleteObjects(final List<Object> back, DeleteOrder deleteOrder) {
        final Set<Class> values = this.subUndoGens.keySet();
        for (Class current : values) {
            if (this.subUndoGensOrder.get(current).equals(deleteOrder)) {
                UndoScriptGenerator<?> akt = this.subUndoGens.get(current);
                back.addAll(akt.getCreatedObjects());
            }
        }
    }

    private void addSubDeleteScripts(final List<String> back, DeleteOrder deleteOrder) {
        final Set<Class> values = this.subUndoGens.keySet();
        for (Class current : values) {
            if (this.subUndoGensOrder.get(current).equals(deleteOrder)) {
                UndoScriptGenerator<?> akt = this.subUndoGens.get(current);
                back.addAll(akt.getSQLUndoStatements());
            }
        }
    }

    /**
     * Returns a value of an field
     * 
     * @param instance -
     *            the instance
     * @param toGet -
     *            the field to read the value
     * @return - the readed value
     * @throws IllegalAccessException
     */
    private Object getField(Object instance, Property toGet) throws IllegalAccessException {
        return toGet.getField(instance);
    }

    /**
     * Return a registered undo generator for a class or returns a registered one
     * 
     * @param forClass
     * @return - a undo generator for a type
     */
    @SuppressWarnings("unchecked")
    private UndoScriptGenerator getInnerUndoScriptGen(Class forClass, DeleteOrder deleteOrder) {
        if (this.subUndoGens.containsKey(forClass)) {
            return this.subUndoGens.get(forClass);
        }
        final EntityBeanIntrospector targetIntro = new EntityBeanIntrospector(forClass);
        final UndoScriptGenerator innerUndo = new UndoScriptGenerator(targetIntro, this);
        this.subUndoGens.put(forClass, innerUndo);
        this.subUndoGensOrder.put(forClass, deleteOrder);
        return innerUndo;

    }

    /**
     * Generated the pk where condition for the entity bean
     * 
     * @param toCreate -
     *            the entity bean with a pk
     * @return - the where clause with pk
     */
    private String getPkCondition(T toCreate) {
        String aktFieldName = "";
        try {
            final StringBuilder sb = new StringBuilder();
            List<Property> pkProperties = null;

            // check if is embedded or not
            if (this.inspector.hasEmbeddedPKClass()) {
                final EmbeddedClassIntrospector<Object> emci = this.inspector.getEmbeddedPKClass();
                pkProperties = emci.getPersitentFields();
            } else {
                pkProperties = new ArrayList<Property>(inspector.getPkFields());
            }

            // process the pk list
            for (int i = 0; i < pkProperties.size(); i++) {
                final Property aktProperty = pkProperties.get(i);
                aktFieldName = aktProperty.getName();
                if (this.inspector.hasEmbeddedPKClass()) {
                    sb.append(this.inspector.getEmbeddedPKClass().getPresistentFieldInfo(aktProperty)
                            .getDbName());
                    sb.append("=");
                    Object pkClassInstance = this.getField(toCreate, this.inspector.getEmbeddedPKClass()
                            .getAttibuteName());
                    sb.append(this.getField(pkClassInstance, aktProperty));
                } else {
                    sb.append(this.inspector.getPresistentFieldInfo(aktProperty).getDbName());
                    sb.append("=");
                    sb.append(this.getField(toCreate, aktProperty));
                }

                // is not last one?
                if (i + 1 < pkProperties.size()) {
                    sb.append(" AND ");
                }
            }

            return sb.toString();
        } catch (IllegalAccessException e) {
            log.error("Can´t read the field: " + aktFieldName);
            throw new RuntimeException("Can´t read the field: " + aktFieldName);
        }
    }

    /**
     * True if this generator is the root
     * 
     * @return - true if the generator is root generator
     */
    private boolean isRootGenerator() {
        return this.rootGenerator == null;
    }

    @SuppressWarnings("unchecked")
    private void processManyToOneRelation(EntityReleationInfo eri, T toCreate) {
        if (!eri.isCascadeOnDelete() && eri.getReleationType() == RelationType.ManyToOne) {
            ManyToOneReleation o2m = (ManyToOneReleation) eri;
            try {
                final Object relatedObject = o2m.getSourceProperty().getField(toCreate);
                if (relatedObject != null) {
                    final UndoScriptGenerator innerUndo = this.getInnerUndoScriptGen(o2m.getTargetClass(),
                            DeleteOrder.DELETE_AFTER);
                    // protokoll assotiated object
                    innerUndo.protokollCreate(relatedObject);
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Can´t generate undo script for One to Many relation");
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void processOneToManyRelation(EntityReleationInfo eri, T toCreate) {
        if (!eri.isCascadeOnDelete() && eri.getReleationType() == RelationType.OneToMany) {
            OneToManyReleation o2m = (OneToManyReleation) eri;
            // no need for subs delete scripts if cascae type is all
            try {
                final Collection relatedObjects = (Collection) o2m.getSourceProperty().getField(toCreate);
                if (relatedObjects != null) {
                    final UndoScriptGenerator innerUndo = this.getInnerUndoScriptGen(o2m.getTargetClass(),
                            DeleteOrder.DELETE_FIRST);
                    // protokoll assotiated objects
                    for (Object akt : relatedObjects) {
                        innerUndo.protokollCreate(akt);
                    }
                }

            } catch (IllegalAccessException e) {
                throw new RuntimeException("Can´t generate undo script for One to Many relation");
            }
        }

    }

    /**
     * If a object has relations to other entity beans, this method will create undo scripts for this objects
     * also
     * 
     * @param eri -
     *            the entity relation info
     * @param toCreate -
     *            the main bean to create
     */
    @SuppressWarnings("unchecked")
    private void processReletatedObjects(EntityReleationInfo eri, T toCreate) {
        // TODO generate undo relations for OneToOne
        processOneToManyRelation(eri, toCreate);
        processManyToOneRelation(eri, toCreate);
    }
}
