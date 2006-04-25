//$Id: ExtendedMappings.java,v 1.1 2006/04/17 12:11:10 daniel_wiese Exp $
package org.hibernate.cfg;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.persistence.Embeddable;
import javax.persistence.MappedSuperclass;
import javax.persistence.Entity;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.MappingException;
import org.hibernate.mapping.IdGenerator;
import org.hibernate.mapping.Join;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Table;
import org.hibernate.reflection.XClass;

/**
 * Allow annotation related mappings
 * <p/>
 * at least for named generators
 *
 * @author Emmanuel Bernard
 */
public class ExtendedMappings extends Mappings {

	private static final Log log = LogFactory.getLog( ExtendedMappings.class );

	private final Map<String, IdGenerator> namedGenerators;
	private final Map<String, Map<String, Join>> joins;
	private final Map<String, AnnotatedClassType> classTypes;
	private final Map<String, Properties> generatorTables;
	private final Map<Table, List<String[]>> tableUniqueConstraints;
	private final Map<String, String> mappedByResolver;
	private final Map<String, String> propertyRefResolver;

	ExtendedMappings(
			Map classes, Map collections, Map tables, Map queries, Map sqlqueries, Map sqlResultSetMappings,
			Map imports,
			List secondPasses, List propertyReferences, NamingStrategy namingStrategy, Map typeDefs,
			Map filterDefinitions, Map namedGenerators, Map<String, Map<String, Join>> joins, Map<String,
			AnnotatedClassType> classTypes, Map extendsQueue, Map<String, TableDescription> tableNameBinding,
											Map<Table, ColumnNames> columnNameBindingPerTable,
											final List auxiliaryDatabaseObjects,
											Map<String, Properties> generatorTables,
											Map<Table, List<String[]>> tableUniqueConstraints,
											Map<String, String> mappedByResolver,
											Map<String, String> propertyRefResolver
	) {
		super(
				classes,
				collections,
				tables,
				queries,
				sqlqueries,
				sqlResultSetMappings,
				imports,
				secondPasses,
				propertyReferences,
				namingStrategy,
				typeDefs,
				filterDefinitions,
				extendsQueue,
				auxiliaryDatabaseObjects,
				tableNameBinding,
				columnNameBindingPerTable
		);
		this.namedGenerators = namedGenerators;
		this.joins = joins;
		this.classTypes = classTypes;
		this.generatorTables = generatorTables;
		this.tableUniqueConstraints = tableUniqueConstraints;
		this.mappedByResolver = mappedByResolver;
		this.propertyRefResolver = propertyRefResolver;
	}

	public void addGenerator(IdGenerator generator) throws MappingException {
		Object old = namedGenerators.put( generator.getName(), generator );
		if ( old != null ) log.warn( "duplicate generator name: " + generator.getName() );
	}

	public void addJoins(PersistentClass persistentClass, Map<String, Join> joins) throws MappingException {
		Object old = this.joins.put( persistentClass.getEntityName(), joins );
		if ( old != null ) log.warn( "duplicate joins for class: " + persistentClass.getEntityName() );
	}

	public AnnotatedClassType addClassType(XClass clazz) {
		AnnotatedClassType type;
		if ( clazz.isAnnotationPresent( Entity.class ) ) {
			type = AnnotatedClassType.ENTITY;
		}
		else if ( clazz.isAnnotationPresent( Embeddable.class ) ) {
			type = AnnotatedClassType.EMBEDDABLE;
		}
		else if ( clazz.isAnnotationPresent( MappedSuperclass.class ) ) {
			type = AnnotatedClassType.EMBEDDABLE_SUPERCLASS;
		}
		else {
			type = AnnotatedClassType.NONE;
		}
		classTypes.put( clazz.getName(), type );
		return type;
	}

	/**
	 * get and maintain a cache of class type.
	 * A class can be an entity, a embedded objet or nothing.
	 */
	public AnnotatedClassType getClassType(XClass clazz) {
		AnnotatedClassType type = classTypes.get( clazz.getName() );
		if ( type == null ) {
			return addClassType( clazz );
		}
		else {
			return type;
		}
	}

	public IdGenerator getGenerator(String name) {
		return getGenerator( name, null );
	}

	public Map<String, Join> getJoins(String persistentClass) {
		return joins.get( persistentClass );
	}

	/**
	 * Try to find the generator from the localGenerators
	 * and then from the global generator list
	 *
	 * @param name			generator name
	 * @param localGenerators local generators to find to
	 * @return the appropriate idgenerator or null if not found
	 */
	public IdGenerator getGenerator(String name, Map<String, IdGenerator> localGenerators) {
		if ( localGenerators != null ) {
			IdGenerator result = localGenerators.get( name );
			if ( result != null ) return result;
		}
		return namedGenerators.get( name );
	}

	public void addGeneratorTable(String name, Properties params) {
		Object old = generatorTables.put( name, params );
		if ( old != null ) log.warn( "duplicate generator table: " + name );
	}

	public Properties getGeneratorTableProperties(String name, Map<String, Properties> localGeneratorTables) {
		if ( localGeneratorTables != null ) {
			Properties result = localGeneratorTables.get( name );
			if ( result != null ) return result;
		}
		return generatorTables.get( name );
	}

	public void addUniqueConstraints(Table table, List uniqueConstraints) {
		List oldConstraints = tableUniqueConstraints.get( table );
		if ( oldConstraints == null ) {
			oldConstraints = new ArrayList();
			tableUniqueConstraints.put( table, oldConstraints );
		}
		oldConstraints.addAll( uniqueConstraints );
	}

	public Map<Table, List<String[]>> getTableUniqueConstraints() {
		return tableUniqueConstraints;
	}

	public void addMappedBy(String entityName, String propertyName, String inversePropertyName) {
		mappedByResolver.put( entityName + "." + propertyName, inversePropertyName );
	}

	public String getFromMappedBy(String entityName, String propertyName) {
		return mappedByResolver.get( entityName + "." + propertyName );
	}

	public void addPropertyReferencedAssociation(String entityName, String propertyName, String propertyRef) {
		propertyRefResolver.put( entityName + "." + propertyName, propertyRef );
	}

	public String getPropertyReferencedAssociation(String entityName, String propertyName) {
		return propertyRefResolver.get( entityName + "." + propertyName );
	}

	@Override
	public void addUniquePropertyReference(String referencedClass, String propertyName) {
		super.addUniquePropertyReference( referencedClass, propertyName );
	}

	@Override
	public void addPropertyReference(String referencedClass, String propertyName) {
		super.addPropertyReference( referencedClass, propertyName );
	}
}