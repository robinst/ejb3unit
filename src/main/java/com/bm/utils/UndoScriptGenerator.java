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
import com.bm.introspectors.releations.EntityReleationInfo;
import com.bm.introspectors.releations.ManyToOneReleation;
import com.bm.introspectors.releations.OneToManyReleation;
import com.bm.introspectors.releations.RelationType;

/**
 * This class generates a undo script for all DB oprations excuted in jUnit.
 * tests
 * 
 * @author Daniel Wiese
 * @param <T> -
 *            the type of the entity bean
 * @since 07.10.2005
 */
public class UndoScriptGenerator<T> {

	private static final Logger log = Logger
			.getLogger(UndoScriptGenerator.class);

	private UndoScriptGenerator rootGenerator = null;

	private final Set<T> createdObjects = new HashSet<T>();

	private final List<String> createdSQLScripts = new ArrayList<String>();

	private final EntityBeanIntrospector<T> inspector;

	/**
	 * if a bean has releated bens, a undo generatro for each releation is
	 * constructed
	 */
	private final Map<Class, UndoScriptGenerator> subUndoGens = new HashMap<Class, UndoScriptGenerator>();

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
	private UndoScriptGenerator(EntityBeanIntrospector<T> inspector,
			UndoScriptGenerator root) {
		this.inspector = inspector;
		this.rootGenerator = root;
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
		sb.append("DELETE FROM ").append(this.inspector.getTableName()).append(
				" WHERE ");
		sb.append(this.getPkCondition(toCreate));
		this.createdSQLScripts.add(sb.toString());

		// now iterate over persistent field and search for
		// 1:N, N:1 or 1:! releations
		List<Property> fields = this.inspector.getPersitentFields();
		for (Property akt : fields) {
			EntityReleationInfo eri = this.inspector
					.getPresistentFieldInfo(akt).getEntityReleationInfo();
			if (eri != null && this.isRootGenerator()) {
				this.processReletatedObjects(eri, toCreate);
			}
		}

	}

	/**
	 * This method returns a delete all statement for the table (bean type).
	 * ATTENTION: This method will not delete referenced collections
	 * 
	 * @return delete all statement
	 */
	public String getDeleteAllStatement() {
		final StringBuilder sb = new StringBuilder();
		sb.append("DELETE FROM ").append(this.inspector.getTableName());
		return sb.toString();
	}

	/**
	 * Return a registered undo generator for a class or returns a registered
	 * one
	 * 
	 * @param forClass
	 * @return - a undo generator for a type
	 */
	@SuppressWarnings("unchecked")
	private UndoScriptGenerator getInnerUndoScriptGen(Class forClass) {
		if (this.subUndoGens.containsKey(forClass)) {
			return this.subUndoGens.get(forClass);
		} else {
			final EntityBeanIntrospector targetIntro = new EntityBeanIntrospector(
					forClass);
			final UndoScriptGenerator innerUndo = new UndoScriptGenerator(
					targetIntro, this);
			this.subUndoGens.put(forClass, innerUndo);
			return innerUndo;
		}
	}

	/**
	 * If a object has relations to other entity beans, this method will create
	 * undo scripts for this objects also
	 * 
	 * @param eri -
	 *            the entity relation info
	 * @param toCreate -
	 *            the main bean to create
	 */
	@SuppressWarnings("unchecked")
	private void processReletatedObjects(EntityReleationInfo eri, T toCreate) {
		// TODO generate undo relations for OneToOne
		if (eri.getReleationType() == RelationType.OneToMany) {
			OneToManyReleation o2m = (OneToManyReleation) eri;
			try {
				final Collection relatedObjects = (Collection) o2m
						.getSourceProperty().getField(toCreate);
				if (relatedObjects != null) {
					final UndoScriptGenerator innerUndo = this
							.getInnerUndoScriptGen(o2m.getTargetClass());
					// protokoll assotiated objects
					for (Object akt : relatedObjects) {
						innerUndo.protokollCreate(akt);
					}
				}

			} catch (IllegalAccessException e) {
				throw new RuntimeException(
						"Can´t generate undo script for One to Many relation");
			}
		} else if (eri.getReleationType() == RelationType.ManyToOne) {
			ManyToOneReleation o2m = (ManyToOneReleation) eri;
			try {
				final Object relatedObject = o2m.getSourceProperty().getField(
						toCreate);
				if (relatedObject != null) {
					final UndoScriptGenerator innerUndo = this
							.getInnerUndoScriptGen(o2m.getTargetClass());
					// protokoll assotiated object
					innerUndo.protokollCreate(relatedObject);
				}
			} catch (IllegalAccessException e) {
				throw new RuntimeException(
						"Can´t generate undo script for One to Many relation");
			}
		}
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
			if (this.inspector.hasPKClass()) {
				final EmbeddedClassIntrospector<Object> emci = this.inspector
						.getEmbeddedPKClass();
				pkProperties = emci.getPersitentFields();
			} else {
				pkProperties = new ArrayList<Property>(inspector.getPkFields());
			}

			// process the pk list
			for (int i = 0; i < pkProperties.size(); i++) {
				final Property aktProperty = pkProperties.get(i);
				aktFieldName = aktProperty.getName();
				if (this.inspector.hasPKClass()) {
					sb.append(this.inspector.getEmbeddedPKClass()
							.getPresistentFieldInfo(aktProperty).getDbName());
					sb.append("=");
					Object pkClassInstance = this.getField(toCreate,
							this.inspector.getEmbeddedPKClass()
									.getAttibuteName());
					sb.append(this.getField(pkClassInstance, aktProperty));
				} else {
					sb.append(this.inspector
							.getPresistentFieldInfo(aktProperty).getDbName());
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
	 * Returns a value of an field
	 * 
	 * @param instance -
	 *            the instance
	 * @param toGet -
	 *            the field to read the value
	 * @return - the readed value
	 * @throws IllegalAccessException
	 */
	private Object getField(Object instance, Property toGet)
			throws IllegalAccessException {
		return toGet.getField(instance);
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

	/**
	 * Returns a list of SQL undo statements.
	 * 
	 * @return SQL statements
	 */
	public List<String> getSQLUndoStatements() {
		final List<String> back = new ArrayList<String>();
		back.addAll(this.createdSQLScripts);
		// rekursive solution
		final Collection<UndoScriptGenerator> values = this.subUndoGens
				.values();
		for (UndoScriptGenerator<Object> akt : values) {
			back.addAll(akt.getSQLUndoStatements());
		}

		return back;
	}

	/**
	 * Returns the createdObjects.
	 * 
	 * @return Returns the createdObjects.
	 */
	public List<Object> getCreatedObjects() {
		final List<Object> back = new ArrayList<Object>();
		back.addAll(this.createdObjects);
		// rekursive solution
		final Collection<UndoScriptGenerator> values = this.subUndoGens
				.values();
		for (UndoScriptGenerator<Object> akt : values) {
			back.addAll(akt.getCreatedObjects());
		}

		return back;
	}

	/**
	 * True if this generator is the root
	 * 
	 * @return - true if the generator is root generator
	 */
	private boolean isRootGenerator() {
		return this.rootGenerator == null;
	}
}
