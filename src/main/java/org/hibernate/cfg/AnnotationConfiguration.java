// $Id: AnnotationConfiguration.java,v 1.1 2006/04/17 12:11:10 daniel_wiese Exp $
package org.hibernate.cfg;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Collection;
import javax.persistence.MappedSuperclass;
import javax.persistence.Entity;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.hibernate.AnnotationException;
import org.hibernate.MappingException;
import org.hibernate.validator.ClassValidator;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Join;
import org.hibernate.mapping.Table;
import org.hibernate.mapping.UniqueKey;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.reflection.ReflectionManager;
import org.hibernate.reflection.XClass;
import org.hibernate.util.JoinedIterator;
import org.hibernate.util.ReflectHelper;
import org.hibernate.util.StringHelper;

/**
 * Add JSR 175 configuration capability.
 * For now, only programmatic configuration is available.
 *
 * @author Emmanuel Bernard
 */
public class AnnotationConfiguration extends Configuration {
	private static Log log = LogFactory.getLog( AnnotationConfiguration.class );
	public static final String ARTEFACT = "hibernate.mapping.precedence";
	public static final String DEFAULT_PRECEDENCE = "hbm, class";

	private Map namedGenerators;
	private Map<String, Map<String, Join>> joins;
	private Map<String, AnnotatedClassType> classTypes;
	private Map<String, Properties> generatorTables;
	private Map<Table, List<String[]>> tableUniqueConstraints;
	private Map<String, String> mappedByResolver;
	private Map<String, String> propertyRefResolver;
	private List<XClass> annotatedClasses;
	private Map<String, XClass> annotatedClassEntities;
	private Map<String, Document> hbmEntities;
	private List<CacheHolder> caches;
	private List<Document> hbmDocuments; //user ordering matters, hence the list
	private String precedence = null;
	private boolean inSecondPass = false;

	public AnnotationConfiguration() {
		super();
	}

	public AnnotationConfiguration(SettingsFactory sf) {
		super( sf );
	}

	protected List<XClass> orderAndFillHierarchy(List<XClass> original) {
		//TODO remove embeddable
		List<XClass> copy = new ArrayList<XClass>( original );
		//for each class, copy all the relevent hierarchy
		for (XClass clazz : original) {
			XClass superClass = clazz.getSuperclass();
			while ( ! ReflectionManager.INSTANCE.equals( superClass, Object.class ) && ! copy.contains( superClass ) ) {
				if (superClass.isAnnotationPresent( Entity.class )
						|| superClass.isAnnotationPresent( MappedSuperclass.class) ) {
					copy.add( superClass );
				}
				superClass = superClass.getSuperclass();
			}
		}
		List<XClass> workingCopy = new ArrayList<XClass>( copy );
		List<XClass> newList = new ArrayList<XClass>( copy.size() );
		while ( workingCopy.size() > 0 ) {
			XClass clazz = workingCopy.get( 0 );
			orderHierarchy( workingCopy, newList, copy, clazz );
		}
		return newList;
	}

	private static void orderHierarchy(List<XClass> copy, List<XClass> newList, List<XClass> original, XClass clazz) {
		if ( ReflectionManager.INSTANCE.equals( clazz, Object.class ) ) return;
		//process superclass first
		orderHierarchy( copy, newList, original, clazz.getSuperclass() );
		if ( original.contains( clazz ) ) {
			if ( !newList.contains( clazz ) ) {
				newList.add( clazz );
			}
			copy.remove( clazz );
		}
	}

	/**
	 * Read a mapping from the class annotation metadata (JSR 175).
	 *
	 * @param persistentClass the mapped class
	 * @return the configuration object
	 */
	public AnnotationConfiguration addAnnotatedClass(Class persistentClass) throws MappingException {
		XClass persistentXClass = ReflectionManager.INSTANCE.toXClass( persistentClass );
		try {
			if ( persistentXClass.isAnnotationPresent( Entity.class ) ) {
				annotatedClassEntities.put( persistentXClass.getName(), persistentXClass );
			}
			annotatedClasses.add( persistentXClass );
			return this;
		}
		catch (MappingException me) {
			log.error( "Could not compile the mapping annotations", me );
			throw me;
		}
	}

	/**
	 * Read package level metadata
	 *
	 * @param packageName java package name
	 * @return the configuration object
	 */
	public AnnotationConfiguration addPackage(String packageName) throws MappingException {
		log.info( "Mapping package " + packageName );
		try {
			AnnotationBinder.bindPackage( packageName, createExtendedMappings() );
			return this;
		}
		catch (MappingException me) {
			log.error( "Could not compile the mapping annotations", me );
			throw me;
		}
	}

	public ExtendedMappings createExtendedMappings() {
		return new ExtendedMappings(
				classes,
				collections,
				tables,
				namedQueries,
				namedSqlQueries,
				sqlResultSetMappings,
				imports,
				secondPasses,
				propertyReferences,
				namingStrategy,
				typeDefs,
				filterDefinitions,
				namedGenerators,
				joins,
				classTypes,
				extendsQueue,
				tableNameBinding, columnNameBindingPerTable, auxiliaryDatabaseObjects,
				generatorTables,
				tableUniqueConstraints,
				mappedByResolver,
				propertyRefResolver
		);
	}

	@Override
	public void setCacheConcurrencyStrategy(
			String clazz, String concurrencyStrategy, String region, boolean cacheLazyProperty
	) throws MappingException {
		caches.add( new CacheHolder( clazz, concurrencyStrategy, region, true, cacheLazyProperty ) );
	}

	@Override
	public void setCollectionCacheConcurrencyStrategy(String collectionRole, String concurrencyStrategy, String region)
			throws MappingException {
		caches.add( new CacheHolder( collectionRole, concurrencyStrategy, region, false, false ) );
	}

	@Override
	protected void reset() {
		super.reset();
		namedGenerators = new HashMap();
		joins = new HashMap<String, Map<String, Join>>();
		classTypes = new HashMap<String, AnnotatedClassType>();
		generatorTables = new HashMap<String, Properties>();
		tableUniqueConstraints = new HashMap<Table, List<String[]>>();
		mappedByResolver = new HashMap<String, String>();
		propertyRefResolver = new HashMap<String, String>();
		annotatedClasses = new ArrayList<XClass>();
		caches = new ArrayList<CacheHolder>();
		hbmEntities = new HashMap<String, Document>();
		annotatedClassEntities = new HashMap<String, XClass>();
		hbmDocuments = new ArrayList<Document>();
		namingStrategy = EJB3NamingStrategy.INSTANCE;
		setEntityResolver( new EJB3DTDEntityResolver() );
	}

	@Override
	protected void secondPassCompile() throws MappingException {
		log.debug( "Execute first pass mapping processing" );
		if ( precedence == null ) precedence = getProperties().getProperty( ARTEFACT );
		if ( precedence == null ) precedence = DEFAULT_PRECEDENCE;
		StringTokenizer precedences = new StringTokenizer( precedence, ",; ", false );
		if ( ! precedences.hasMoreElements() ) {
			throw new MappingException( ARTEFACT + " cannot be empty: " + precedence );
		}
		while ( precedences.hasMoreElements() ) {
			String artifact = (String) precedences.nextElement();
			removeConflictedArtifact( artifact );
			processArtifactsOfType( artifact );
		}

		int cacheNbr = caches.size();
		for ( int index = 0; index < cacheNbr ; index++ ) {
			CacheHolder cacheHolder = caches.get( index );
			if ( cacheHolder.isClass ) {
				super.setCacheConcurrencyStrategy(
						cacheHolder.role, cacheHolder.usage, cacheHolder.region, cacheHolder.cacheLazy
				);
			}
			else {
				super.setCollectionCacheConcurrencyStrategy( cacheHolder.role, cacheHolder.usage, cacheHolder.region );
			}
		}
		caches.clear();

		log.debug( "processing manytoone fk mappings" );
		Iterator iter = secondPasses.iterator();
		while ( iter.hasNext() ) {
			SecondPass sp = (SecondPass) iter.next();
			//do the second pass of fk before the others and remove them
			if ( sp instanceof FkSecondPass ) {
				sp.doSecondPass( classes, Collections.EMPTY_MAP ); // TODO: align meta-attributes with normal bind...
				iter.remove();
			}
		}
		inSecondPass = true;
		super.secondPassCompile();
		inSecondPass = false;
		Iterator tables = (Iterator<Map.Entry<Table, List<String[]>>>) tableUniqueConstraints.entrySet().iterator();
		Table table;
		Map.Entry entry;
		String keyName;
		int uniqueIndexPerTable;
		while ( tables.hasNext() ) {
			entry = (Map.Entry) tables.next();
			table = (Table) entry.getKey();
			List<String[]> uniqueConstraints = (List<String[]>) entry.getValue();
			uniqueIndexPerTable = 0;
			for ( String[] columnNames : uniqueConstraints ) {
				keyName = "key" + uniqueIndexPerTable++;
				buildUniqueKeyFromColumnNames( columnNames, table, keyName );
			}
		}
		for (PersistentClass persistentClazz : ( Collection < PersistentClass > ) classes.values() ) {
			//integrate the validate framework
			// TODO: migrate the Validator to the X layer
			String className = persistentClazz.getClassName();
			if ( StringHelper.isNotEmpty( className ) ) {
				try {
					new ClassValidator( ReflectHelper.classForName( className ) ).apply( persistentClazz );
				}
				catch (ClassNotFoundException e) {
					//swallow them
				}
			}
		}
	}

	private void processArtifactsOfType(String artifact) {
		if ( "hbm".equalsIgnoreCase( artifact ) ) {
			log.debug( "Process hbm files" );
			for ( Document document : hbmDocuments ) {
				super.add( document );
			}
			hbmDocuments.clear();
			hbmEntities.clear();
		}
		else if ( "class".equalsIgnoreCase( artifact ) ) {
			log.debug( "Process annotated classes" );
			//bind classes in the correct order calculating some inheritance state
			List<XClass> orderedClasses = orderAndFillHierarchy( annotatedClasses );
			Map<XClass, InheritanceState> inheritanceStatePerClass = AnnotationBinder.buildInheritanceStates(
					orderedClasses
			);
			ExtendedMappings mappings = createExtendedMappings();
			for ( XClass clazz : orderedClasses ) {
				//todo use the same extended mapping
				AnnotationBinder.bindClass( clazz, inheritanceStatePerClass, mappings );
			}
			annotatedClasses.clear();
			annotatedClassEntities.clear();
		}
		else {
			log.warn( "Unknown artifact: " + artifact );
		}
	}

	private void removeConflictedArtifact(String artifact) {
		if ( "hbm".equalsIgnoreCase( artifact ) ) {
			for ( String entity : hbmEntities.keySet() ) {
				if ( annotatedClassEntities.containsKey( entity ) ) {
					annotatedClasses.remove( annotatedClassEntities.get( entity ) );
					annotatedClassEntities.remove( entity );
				}
			}
		}
		else if ( "class".equalsIgnoreCase( artifact ) ) {
			for ( String entity : annotatedClassEntities.keySet() ) {
				if ( hbmEntities.containsKey( entity ) ) {
					hbmDocuments.remove( hbmEntities.get( entity ) );
					hbmEntities.remove( entity );
				}
			}
		}
	}

	private void buildUniqueKeyFromColumnNames(String[] columnNames, Table table, String keyName) {
		UniqueKey uc;
		int size = columnNames.length;
		Column[] columns = new Column[size];
		Set<Column> unbound = new HashSet<Column>();
		ExtendedMappings mappings = createExtendedMappings();
		for ( int index = 0; index < size ; index++ ) {
			columns[index] = new Column( mappings.getPhysicalColumnName( columnNames[index], table ) );
			unbound.add( columns[index] );
			//column equals and hashcode is based on column name
		}
		for ( Column column : columns ) {
			if ( table.containsColumn( column ) ) {
				uc = table.getOrCreateUniqueKey( keyName );
				uc.addColumn( table.getColumn( column ) );
				unbound.remove( column );
			}
		}
		if ( unbound.size() > 0 ) {
			StringBuilder sb = new StringBuilder( "Unable to create unique key constraint (" );
			for ( String columnName : columnNames ) {
				sb.append( columnName ).append( ", " );
			}
			sb.setLength( sb.length() - 2 );
			sb.append( ") on table " ).append( table.getName() ).append( ": " );
			for ( Column column : unbound ) {
				sb.append( column.getName() ).append( ", " );
			}
			sb.setLength( sb.length() - 2 );
			sb.append( " not found" );
			throw new AnnotationException( sb.toString() );
		}
	}

	@Override
	protected void parseMappingElement(Element subelement, String name) {
		Attribute rsrc = subelement.attribute( "resource" );
		Attribute file = subelement.attribute( "file" );
		Attribute jar = subelement.attribute( "jar" );
		Attribute pckg = subelement.attribute( "package" );
		Attribute clazz = subelement.attribute( "class" );
		if ( rsrc != null ) {
			log.debug( name + "<-" + rsrc );
			addResource( rsrc.getValue() );
		}
		else if ( jar != null ) {
			log.debug( name + "<-" + jar );
			addJar( new File( jar.getValue() ) );
		}
		else if ( file != null ) {
			log.debug( name + "<-" + file );
			addFile( file.getValue() );
		}
		else if ( pckg != null ) {
			log.debug( name + "<-" + pckg );
			addPackage( pckg.getValue() );
		}
		else if ( clazz != null ) {
			log.debug( name + "<-" + clazz );
			Class loadedClass = null;
			try {
				loadedClass = ReflectHelper.classForName( clazz.getValue() );
			}
			catch (ClassNotFoundException cnf) {
				throw new MappingException(
						"Unable to load class declared as <mapping class=\"" + clazz.getValue() + "\"/> in the configuration:",
						cnf
				);
			} catch (NoClassDefFoundError ncdf) {
				throw new MappingException(
						"Unable to load class declared as <mapping class=\"" + clazz.getValue() + "\"/> in the configuration:",
						ncdf
					);
			}

			addAnnotatedClass( loadedClass );
		}
		else {
			throw new MappingException( "<mapping> element in configuration specifies no attributes" );
		}
	}

	@Override
	protected void add(org.dom4j.Document doc) throws MappingException {
		if (inSecondPass) {
			//if in second pass bypass the queueing, getExtendedQueue reuse this method
			super.add(doc);
		}
		else {
			final Element hmNode = doc.getRootElement();
			Attribute packNode = hmNode.attribute( "package" );
			String defaultPackage = packNode != null
					? packNode.getValue()
					: "";
			Set<String> entityNames = new HashSet<String>();
			findClassNames( defaultPackage, hmNode, entityNames );
			for ( String entity : entityNames ) {
				hbmEntities.put( entity, doc );
			}
			hbmDocuments.add( doc );
		}
	}

	private static void findClassNames(
			String defaultPackage, final Element startNode,
			final java.util.Set names
	) {
		// if we have some extends we need to check if those classes possibly could be inside the
		// same hbm.xml file...
		Iterator[] classes = new Iterator[4];
		classes[0] = startNode.elementIterator( "class" );
		classes[1] = startNode.elementIterator( "subclass" );
		classes[2] = startNode.elementIterator( "joined-subclass" );
		classes[3] = startNode.elementIterator( "union-subclass" );

		Iterator classIterator = new JoinedIterator( classes );
		while ( classIterator.hasNext() ) {
			Element element = (Element) classIterator.next();
			String entityName = element.attributeValue( "entity-name" );
			if ( entityName == null ) entityName = getClassName( element.attribute( "name" ), defaultPackage );
			names.add( entityName );
			findClassNames( defaultPackage, element, names );
		}
	}

	private static String getClassName(Attribute name, String defaultPackage) {
		if ( name == null ) return null;
		String unqualifiedName = name.getValue();
		if ( unqualifiedName == null ) return null;
		if ( unqualifiedName.indexOf( '.' ) < 0 && defaultPackage != null ) {
			return defaultPackage + '.' + unqualifiedName;
		}
		return unqualifiedName;
	}

	public void setPrecedence(String precedence) {
		this.precedence = precedence;
	}

	private class CacheHolder {
		public CacheHolder(String role, String usage, String region, boolean isClass, boolean cacheLazy) {
			this.role = role;
			this.usage = usage;
			this.region = region;
			this.isClass = isClass;
			this.cacheLazy = cacheLazy;
		}

		public String role;
		public String usage;
		public String region;
		public boolean isClass;
		public boolean cacheLazy;
	}
}
