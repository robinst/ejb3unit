package com.bm.introspectors;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Set;
import java.util.Map.Entry;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorValue;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.IdClass;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

import org.slf4j.Logger;

import com.bm.introspectors.relations.EntityReleationInfo;
import com.bm.introspectors.relations.GlobalPrimaryKeyStore;
import com.bm.introspectors.relations.ManyToOneRelation;
import com.bm.introspectors.relations.OneToOneRelation;
import com.bm.utils.AccessType;
import com.bm.utils.AccessTypeFinder;
import com.bm.utils.IdClassInstanceGen;
import java.util.HashMap;
import java.util.Map;

/**
 * This class inspects all relevant fields of an entity bean and holds the
 * information.
 * 
 * @author Daniel Wiese
 * @author Istvan Devai
 * @param <T> -
 *            the type of the class to inspect
 * @since 07.10.2005
 */
public class EntityBeanIntrospector<T> extends AbstractPersistentClassIntrospector<T>
		implements Introspector<T> {

	static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(EntityBeanIntrospector.class);

	/** true if the class has an composed pk field. * */
	private boolean hasPKClass = false;

	/** the introspector for the embedded class. * */
	private EmbeddedClassIntrospector<Object> embeddedPKClass = null;

	/** the table name. * */
	private String tableName;

	/** the table name. * */
	private String schemaName;

	private boolean hasSchema = false;

	private Class<?> idClass = null;

	private final Class<T> toInspect;

	/** the inheritance type used by the entity class (null when no inheritance used) */
	private InheritanceType inheritanceStrategy;

	/** the name of the discriminator (if a single-table inheritance strategy is used) */
	private String discriminatorName;

	/** the value of the discriminator for the introspected class (if a single-table inheritance strategy is used) */
	private String discriminatorValue;

	/** the type of the discriminator (if a single-table inheritance strategy is used) */
	private Class<?> discriminatorType;

        /** the base entity class of the hierarchy */
        private Class<? super T> baseClass;

        /** type of the accessor */
        private boolean isAccessTypeField = false;

        private static Map<Class, EntityBeanIntrospector> introspectors = new HashMap<Class, EntityBeanIntrospector>();
        
        @SuppressWarnings("unchecked")
        public static synchronized EntityBeanIntrospector getEntityBeanIntrospector(Class toInspect) {
            
            EntityBeanIntrospector ebi = introspectors.get(toInspect);
            if (ebi == null) {
                ebi = new EntityBeanIntrospector(toInspect);
                ebi.basicInitialization();
                introspectors.put(toInspect, ebi);
                ebi.processAnnotations();
            }
            
            return ebi;
        }
        
	/**
	 * Constructor with the class to inspect.
	 * 
	 * @param toInspect -
	 *            the class to inspect
	 */
	private  EntityBeanIntrospector(Class<T> toInspect) {
            this.toInspect = toInspect;
	}

        private void basicInitialization() {
		Annotation[] classAnnotations = toInspect.getAnnotations();
		boolean isEntityBean = false;
		boolean isTableNameSpecified = false;
		
		Entity entityAnnotation = null;
                isAccessTypeField = false;

		// iterate over the basic annotations
		for (Annotation a : classAnnotations) {
			if (a instanceof Entity) {
				log.debug("The class to introspect " + toInspect.getCanonicalName()
						+ " is an Entity-Bean");
				isEntityBean = true;
				if (AccessTypeFinder.findAccessType(toInspect).equals(AccessType.FIELD)) {
					isAccessTypeField = true;
				}
				entityAnnotation = (Entity) a;

			} else if (a instanceof Table) {
				Table table = (Table) a;
				this.tableName = table.name();
				this.hasSchema = !table.schema().equals("");
				this.schemaName = table.schema();
				isTableNameSpecified = true;
			} else if (a instanceof IdClass) {
				this.idClass = ((IdClass) a).value();
			}
		}

		// check for mandatory conditions
		if (!isEntityBean) {
			throw new RuntimeException("The class " + toInspect.getSimpleName()
					+ " is not a entity bean");
		}

		if (!isTableNameSpecified) {
			this.tableName = this.generateDefautTableName(toInspect, entityAnnotation);
			log.debug("The class " + toInspect.getSimpleName()
					+ " does not specify a table name! Using default Name: "
					+ this.tableName);
		}

        }
        
        private void processAnnotations() {

            if (isAccessTypeField) {
			this.processAccessTypeField(toInspect);
		} else {
			this.processAccessTypeProperty(toInspect);
		}

		// Process Entity inheritance annotations (if any)
		processInheritance(toInspect);
		
		postProcessRelationProperties();
            
        }

	/**
	 * Overide the abstract implementation of this method, to handle with
	 * embedded classes
	 * 
	 * @author Daniel Wiese
	 * @since 15.10.2005
	 * @see com.bm.introspectors.AbstractPersistentClassIntrospector#processAccessTypeField(java.lang.Class)
	 */
	@Override
	protected void processAccessTypeField(Class<T> toInspect) {
		// class the super method
		super.processAccessTypeField(toInspect);
		// extract meta information
		Field[] fields = toInspect.getDeclaredFields();
		for (Field aktField : fields) {
			// don't introspect fields generated by Hibernate
			Annotation[] fieldAnnotations = aktField.getAnnotations();

			// look into the annotations
			for (Annotation a : fieldAnnotations) {
				// set the embedded class, if any
				if (a instanceof EmbeddedId) {
					this.embeddedPKClass = new EmbeddedClassIntrospector<Object>(
							new Property(aktField));
					this.hasPKClass = true;

					// set the akt field information
					final Property aktProperty = new Property(aktField);
					if (this.getPresistentFieldInfo(aktProperty) != null) {
						final PersistentPropertyInfo fi = this
								.getPresistentFieldInfo(aktProperty);
						fi.setEmbeddedClass(true);
						fi.setNullable(false);
					}

					// set the akt pk information> Ebedded classes are not
					// generated
					PrimaryKeyInfo info = new PrimaryKeyInfo(((EmbeddedId) a));
					this.extractGenerator(fieldAnnotations, info);
					this.pkFieldInfo.put(aktProperty, info);
				}
			}
		}
	}

	/**
	 * Returns the pk to delete one entity bean.
	 * 
	 * @param entityBean -
	 *            the entity bean instance
	 * @return - return the pk or the pk class
	 */
	public Object getPrimaryKey(T entityBean) {
		try {
			if (this.hasEmbeddedPKClass()) {
				// return the embedded class instance
				return this.getField(entityBean, this.embeddedPKClass.getAttibuteName());
			} else if (this.getPkFields().size() == 1) {
				// return the single element
				Property toRead = this.getPkFields().iterator().next();
				return this.getField(entityBean, toRead);
			} else if (this.getPkFields().size() > 0 && hasIdClass()) {
				IdClassInstanceGen idClassInstanceGen = new IdClassInstanceGen(this
						.getPkFields(), this.idClass, entityBean);
				return idClassInstanceGen.getIDClassIntance();

			} else {
				throw new RuntimeException(
						"Multiple PK fields detected, use EmbeddedPKClass or IDClass");
			}
		} catch (IllegalAccessException e) {
			log.error("Unable to retrieve primary key", e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * Returns the tableName.
	 * 
	 * @return Returns the tableName.
	 */
	public String getTableName() {
		return tableName;
	}

	/**
	 * Returns the tableName.
	 * 
	 * @return Returns the tableName.
	 */
	public String getShemaName() {
		return this.schemaName;
	}

	/**
	 * Returns if a chema name is persent.
	 * 
	 * @return a chema name is persent.
	 */
	public boolean hasSchema() {
		return this.hasSchema;
	}


        /**
	 * Returns if a full table name including schema if required.
         *
         * @param useSchemaName if true, schema will be prefixed (if available)
         *
	 * @return full table name "schema.table"
	 */
	public String getFullTableName(boolean useSchemaName) {
		if (useSchemaName && hasSchema()) {
			return getShemaName() + "."
					+ getTableName();
		}
		return getTableName();
	}


	/**
	 * Returns the embeddedPKClass.
	 * 
	 * @return Returns the embeddedPKClass.
	 */
	public EmbeddedClassIntrospector<Object> getEmbeddedPKClass() {
		return embeddedPKClass;
	}

	/**
	 * Returns the hasPKClass.
	 * 
	 * @return Returns the hasPKClass.
	 */
	public boolean hasEmbeddedPKClass() {
		return hasPKClass;
	}

	/**
	 * Returns the hasPKClass.
	 * 
	 * @return Returns the hasPKClass.
	 */
	public boolean hasIdClass() {
		return idClass != null;
	}

	/**
	 * If no table name is specifed hgenrate a JSR 220 table name form class
	 * 
	 * @param clazz -
	 *            the clss name
	 * @return - the JSR 220 default table name
	 */
	private String generateDefautTableName(Class<?> clazz, Entity entityAnnotation) {

		if (entityAnnotation != null && !entityAnnotation.name().equals("")) {
			return entityAnnotation.name().toUpperCase();
		}

		String back = clazz.getName();
		if (back.lastIndexOf(".") > 0 && back.lastIndexOf(".") + 1 < back.length()) {
			back = back.substring(back.lastIndexOf(".") + 1, back.length());
			return back.toUpperCase();
		} else {
			return back.toUpperCase();
		}
	}

	/**
	 * Returns the name of the class to inspect.
	 */
	public String getPersistentClassName() {
		return this.toInspect.getName();

	}

	/**
	 * Returns the name of the class to inspect.
	 */
	public Class<T> getPersistentClass() {
		return this.toInspect;
	}
        
        
	/**
	 * Returns the logger for this class.
	 * @return
	 */
	protected Logger getLogger() {
		return log;
	}
	
	/**
	 * Perform post processing on relation properties. Has to be done after the 
	 * EntityBeanIntrospectors have processed all properties, to avoid cyclic
	 * dependencies.
	 */
	private void postProcessRelationProperties() {

		for (Entry<Property, PersistentPropertyInfo> entry: fieldInfo.entrySet()) {
			if (entry.getValue().isReleation()) {
				EntityReleationInfo relation = entry.getValue().getEntityReleationInfo();
                                
				// TODO (Pd): see if we can generalize this, to other relation types
				if (relation instanceof ManyToOneRelation && !relation.isUnidirectional()) {
					Class<?> targetClass = ((ManyToOneRelation) relation).getTargetClass();
					Set<Property> keyProps = GlobalPrimaryKeyStore.getStore().getPrimaryKeyInfo(targetClass);
					((ManyToOneRelation) relation).setTargetKeyProperty(keyProps);
                                        setKeyProps(entry, keyProps);
				}
				if (relation instanceof OneToOneRelation && !relation.isUnidirectional()) {
					Class<?> targetClass = ((OneToOneRelation) relation).getTargetClass();
					Set<Property> keyProps = GlobalPrimaryKeyStore.getStore().getPrimaryKeyInfo(targetClass);
					((OneToOneRelation) relation).setTargetKeyProperty(keyProps);
                                        setKeyProps(entry, keyProps);
				}
			}
		}
	}

        /**
         *  Check that database name is set (it's explicitly unset while processing the
          * ManyToOne annotation, to be able to recognize the case when it's not
          * specified by a Column annotation).
          */
        private void setKeyProps(Entry<Property, PersistentPropertyInfo> entry, Set<Property> keyProps) {
                if (entry.getValue().getDbName() == null) {
                        // Currently, only single key columns are supported
                        String keyName = keyProps.iterator().next().getName();
                        String dbName = entry.getKey().getName() + "_" + keyName;
                        entry.getValue().setDbName(dbName);
                        log.debug("No db name set for relation; using default " + entry.getValue().getDbName());
                }
        }


	/**
	 * Determines whether the inspected entity class uses entity inheritance and of what type.
	 * Also determines discriminator attributes for the single-table inheritance strategy.
	 * @param toInspect		the class that is inspected
	 */
	private void processInheritance(Class<T> toInspect) {
		// Find the root of the entity class hierarchy
		baseClass = toInspect;
		while (baseClass.getSuperclass().getAnnotation(Entity.class) != null) {
			baseClass = baseClass.getSuperclass();
		}
		// If root is same as class to inspect, there is no inheritance. 
		if (!baseClass.equals(toInspect)) {
			Inheritance inheritanceAnnotation = baseClass.getAnnotation(Inheritance.class);
			inheritanceStrategy = inheritanceAnnotation != null? inheritanceAnnotation.strategy(): null;
			if (inheritanceStrategy == null) {
				log.debug("strategy is null -> taking default");
				inheritanceStrategy = InheritanceType.SINGLE_TABLE;
			} else {
				log.debug("strategy is: " + inheritanceStrategy);
			}
			
			if (inheritanceStrategy.equals(InheritanceType.SINGLE_TABLE)) {
				// Table is specified as annotation on root entity
				Table tableAnnotation = baseClass.getAnnotation(Table.class);
				if (tableAnnotation != null) {
					this.tableName = tableAnnotation.name();
				}
				else {
					this.tableName = generateDefautTableName(baseClass, baseClass.getAnnotation(Entity.class));
				}
                        }
                        
                        if (inheritanceStrategy.equals(InheritanceType.SINGLE_TABLE)
                                || inheritanceStrategy.equals(InheritanceType.JOINED)) {
				// Determine what discriminator to use
				DiscriminatorColumn discriminatorColumn = baseClass.getAnnotation(DiscriminatorColumn.class);
				if (discriminatorColumn != null) {
					this.discriminatorName = discriminatorColumn.name();
					switch (discriminatorColumn.discriminatorType()) {
					case INTEGER:
						discriminatorType = Integer.class;
						break;
					case STRING:
						discriminatorType = String.class;
						break;
					case CHAR:
						discriminatorType = Character.class;
						break;
					}
					//this.discriminatorProperty = new DiscriminatorProperty(toInspect, discriminatorName, type); 
				}
				else {
					// Use defaults:
					this.discriminatorName = "DTYPE";
					this.discriminatorType = String.class;
				}
				// Find discriminator value
				DiscriminatorValue discriminatorValueAnnotation = toInspect.getAnnotation(DiscriminatorValue.class);
				if (discriminatorValueAnnotation != null) {
					this.discriminatorValue = discriminatorValueAnnotation.value();
				}
			}
			else {
                                final String errorMessage = "Inheritance strategy " + inheritanceStrategy + " not (yet) supported.";
                                log.debug(errorMessage);
                                throw new RuntimeException(errorMessage);
			}
		}
	}

	/**
	 * @return	true when entity uses single-table inheritance strategy
	 */
	public boolean usesSingleTableInheritance() {
		return inheritanceStrategy != null && inheritanceStrategy.equals(InheritanceType.SINGLE_TABLE);
	}

	/**
	 * @return	true when entity uses single-table inheritance strategy
	 */
	public boolean usesJoinedInheritance() {
		return inheritanceStrategy != null && inheritanceStrategy.equals(InheritanceType.JOINED);
	}

	/**
	 * @return	name of discriminator (column), or null if not used.
	 */
	public String getDiscriminatorName() {
		return discriminatorName;
	}

	/**
	 * @return	discriminator value used for the class that is inspected by this object,
	 * or null if not used.
	 */
	public String getDiscriminatorValue() {
		return discriminatorValue;
	}

	/**
	 * Returns discriminator type used for the class that is inspected by this object,
	 * or null if not used.
	 * @return		one of Integer.class, String.class or Character.class
	 */
	public Class<?> getDiscriminatorType() {
		return discriminatorType;
	}


        /**
         * Return the base entity class in the entity hierarchy.
         *
         */
        public Class<? super T> getBaseClass() {
            return baseClass;
        }
}
