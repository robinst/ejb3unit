//$Id: Ejb3Configuration.java,v 1.1 2006/04/17 12:11:08 daniel_wiese Exp $
package org.hibernate.ejb;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.EntityManagerFactory;
import javax.persistence.MappedSuperclass;
import javax.persistence.PersistenceException;
import javax.persistence.spi.PersistenceUnitInfo;
import javax.persistence.spi.PersistenceUnitTransactionType;
import javax.sql.DataSource;

import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.AssertionFailure;
import org.hibernate.HibernateException;
import org.hibernate.Interceptor;
import org.hibernate.MappingException;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.cfg.NamingStrategy;
import org.hibernate.cfg.Settings;
import org.hibernate.cfg.SettingsFactory;
import org.hibernate.ejb.connection.InjectedDataSourceConnectionProvider;
import org.hibernate.ejb.instrument.InterceptFieldClassFileTransformer;
import org.hibernate.ejb.packaging.JarVisitor;
import org.hibernate.ejb.packaging.PersistenceMetadata;
import org.hibernate.ejb.packaging.PersistenceXmlLoader;
import org.hibernate.ejb.transaction.JoinableCMTTransactionFactory;
import org.hibernate.ejb.util.LogHelper;
import org.hibernate.engine.FilterDefinition;
import org.hibernate.event.EventListeners;
import org.hibernate.mapping.AuxiliaryDatabaseObject;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.secure.JACCConfiguration;
import org.hibernate.transaction.JDBCTransactionFactory;
import org.hibernate.util.CollectionHelper;
import org.hibernate.util.ReflectHelper;
import org.hibernate.util.StringHelper;
import org.jboss.util.file.ArchiveBrowser;
import org.xml.sax.EntityResolver;

/**
 * @author Emmanuel Bernard
 */
public class Ejb3Configuration implements Serializable {
	private static final String IMPLEMENTATION_NAME = HibernatePersistence.class.getName();
	private static Log log = LogFactory.getLog( Ejb3Configuration.class );
	private AnnotationConfiguration cfg;
	private SettingsFactory settingsFactory;
	private EventListenerConfigurator listenerConfigurator;
	private PersistenceUnitTransactionType transactionType;

	public Ejb3Configuration() {
		settingsFactory = new InjectionSettingsFactory();
		cfg = new AnnotationConfiguration( settingsFactory );
		listenerConfigurator = new EventListenerConfigurator( this );
		transactionType = PersistenceUnitTransactionType.JTA; //default as per the spec
	}

	/**
	 * Used to inject a datasource object as the connection provider.
	 * If used, be sure to <b>not override</b> the {@link Environment.CONNECTION_PROVIDER}
	 * property
	 */
	public void setDataSource(DataSource ds) {
		if ( ds != null ) {
			Map cpInjection = new HashMap();
			cpInjection.put( "dataSource", ds );
			( (InjectionSettingsFactory) settingsFactory ).setConnectionProviderInjectionData( cpInjection );
			this.setProperty( Environment.CONNECTION_PROVIDER, InjectedDataSourceConnectionProvider.class.getName() );
		}
	}

	/**
	 * create a factory from a parsed persistence.xml
	 */
	private EntityManagerFactory createFactory(PersistenceMetadata metadata, Map overrides) {
		log.debug( "Creating Factory: " + metadata.getName() );

		if ( metadata.getMappingFiles().size() > 0 ) throw new RuntimeException( "<mapping-file not supported yet" );
		if ( StringHelper.isNotEmpty( metadata.getJtaDatasource() ) ) {
			this.setProperty( Environment.DATASOURCE, metadata.getJtaDatasource() );
		}
		else if ( StringHelper.isNotEmpty( metadata.getNonJtaDatasource() ) ) {
			this.setProperty( Environment.DATASOURCE, metadata.getNonJtaDatasource() );
		}

		Map workingVars = new HashMap();
		//workingVars.put( HibernatePersistence.TRANSACTION_TYPE, metadata.getTransactionType() );
		defineTransactionType( metadata.getTransactionType(), metadata.getName() );
		if ( metadata.getClasses().size() > 0 ) {
			workingVars.put( HibernatePersistence.CLASS_NAMES, metadata.getClasses() );
		}
		if ( metadata.getPackages().size() > 0 ) {
			workingVars.put( HibernatePersistence.PACKAGE_NAMES, metadata.getPackages() );
		}
		if ( metadata.getHbmfiles().size() > 0 ) {
			workingVars.put( HibernatePersistence.HBXML_FILES, metadata.getHbmfiles() );
		}
		Properties props = new Properties();
		props.putAll( metadata.getProps() );
		if ( overrides != null ) props.putAll( overrides ); //yuk!
		workingVars.put( HibernatePersistence.PERSISTENCE_UNIT_NAME, metadata.getName() );
		return createEntityManagerFactory( props, workingVars );
	}

	/**
	 * Get an entity manager factory by its entity manager name and given the
	 * appropriate extra properties. Those properties override the one get through
	 * the peristence.xml file.
	 *
	 * @param emName entity manager name
	 * @param integration	properties passed to the persistence provider
	 * @return initialized EntityManagerFactory
	 */
	public EntityManagerFactory createEntityManagerFactory(String emName, Map integration) {
		try {
			log.debug( "Trying to find persistence unit: " + emName );
			integration = integration == null ?
					integration = CollectionHelper.EMPTY_MAP :
					Collections.unmodifiableMap( integration );
			Enumeration<URL> xmls = Thread.currentThread()
					.getContextClassLoader()
					.getResources( "META-INF/persistence.xml" );
			while ( xmls.hasMoreElements() ) {
				URL url = xmls.nextElement();
				log.trace( "Analyse of persistence.xml: " + url );
				List<PersistenceMetadata> metadataFiles = PersistenceXmlLoader.deploy( 
						url,
						integration,
						cfg.getEntityResolver()
				);
				for ( PersistenceMetadata metadata : metadataFiles ) {
					JarVisitor.Filter[] filters = getFilters( metadata.getProps(), integration );

					if ( metadata.getProvider() == null || IMPLEMENTATION_NAME.equalsIgnoreCase(
							metadata.getProvider()
					) ) {
						log.trace( "Archive to be processed by hibernate Entity Manager implementation found" );
						//correct provider
						URL jarURL = JarVisitor.getJarURLFromURLEntry( url, "/META-INF/persistence.xml" );
						JarVisitor visitor = JarVisitor.getVisitor( jarURL, filters );
						if ( metadata.getName() == null ) {
							metadata.setName( visitor.getUnqualifiedJarName() );
						}
						if ( log.isTraceEnabled() ) log.trace( "Persistence unit name: " + metadata.getName() );

						log.trace( "emname:" + emName + " metadata: " + metadata.getName() );
						if ( emName == null && xmls.hasMoreElements() ) {
							throw new PersistenceException( "No name provided and several persistence units found" );
						}
						else if ( emName == null || metadata.getName().equals( emName ) ) {
							addMetadataFromVisitor( visitor, metadata );
							for ( String jarFile : metadata.getJarFiles() ) {
								visitor = JarVisitor.getVisitor( jarFile, filters );
								addMetadataFromVisitor( visitor, metadata );
							}
							return createFactory( metadata, integration );
						}
					}
				}
			}
			return null;
		}
		catch (Exception e) {
			throw new PersistenceException( e );
		}
	}

	private static void addMetadataFromVisitor(JarVisitor visitor, PersistenceMetadata metadata) throws IOException {
		Set[] entries = visitor.getMatchingEntries();
		JarVisitor.Filter[] filters = visitor.getFilters();
		int size = filters.length;
		List<String> classes = metadata.getClasses();
		List<String> packages = metadata.getPackages();
		List<InputStream> hbmFiles = metadata.getHbmfiles();
		for ( int index = 0; index < size ; index++ ) {
			Iterator homogeneousEntry = entries[index].iterator();
			while ( homogeneousEntry.hasNext() ) {
				JarVisitor.Entry entry = (JarVisitor.Entry) homogeneousEntry.next();
				if ( filters[index] instanceof JarVisitor.ClassFilter ) {
					classes.add( entry.getName() );
				}
				else if ( filters[index] instanceof JarVisitor.PackageFilter ) {
					packages.add( entry.getName() );
				}
				else if ( filters[index] instanceof JarVisitor.FileFilter ) {
					hbmFiles.add( entry.getInputStream() );
				}
			}
		}
	}

	/**
	 * Create a factory from a PersistenceInfo object
	 */
	public EntityManagerFactory createContainerEntityManagerFactory(PersistenceUnitInfo info, Map integration) {
		if ( log.isDebugEnabled() ) {
			log.debug( "Processing " + LogHelper.logPersistenceUnitInfo( info ) );
		}
		else {
			log.info( "Processing PersistenceUnitInfo [\n\tname: " + info.getPersistenceUnitName() + "\n\t...]" );
		}

		integration = integration != null ? Collections.unmodifiableMap(integration) : CollectionHelper.EMPTY_MAP;
		String provider = (String) integration.get( HibernatePersistence.PROVIDER );
		if (provider == null) provider = info.getPersistenceProviderClassName();
		if ( provider != null && ! provider.trim().startsWith( IMPLEMENTATION_NAME ) ) {
			log.info( "Required a different provider: " + provider );
			return null;
		}
		if ( info.getClassLoader() == null ) {
			throw new IllegalStateException("PersistenceUnitInfo.getClassLoader() id null");
		}
		//set the classloader
		Thread thread = Thread.currentThread();
		ClassLoader contextClassLoader = thread.getContextClassLoader();
		if ( ! info.getClassLoader().equals( contextClassLoader ) ) {
			thread.setContextClassLoader( info.getClassLoader() );
		}
		EntityManagerFactory entityManagerFactory;

		try {
			Map workingVars = new HashMap();
			workingVars.put( HibernatePersistence.PERSISTENCE_UNIT_NAME, info.getPersistenceUnitName() );
			List<String> entities = new ArrayList<String>( 50 );
			if ( info.getManagedClassNames() != null ) entities.addAll( info.getManagedClassNames() );
			List<InputStream> hbmFiles = new ArrayList<InputStream>();
			List<String> packages = new ArrayList<String>();
	//		Object overridenTxType = integration.get( HibernatePersistence.TRANSACTION_TYPE );
	//		if (overridenTxType != null) {
	//			defineTransactionType( overridenTxType, info.getPersistenceUnitName() );
	//		}
	//		else {
			defineTransactionType( info.getTransactionType(), info.getPersistenceUnitName() );
	//		}
			//workingVars.put( HibernatePersistence.TRANSACTION_TYPE, transactionType );
			boolean[] detectArtifact = getDetectedArtifacts( info.getProperties(), null );
			for ( URL jar : info.getJarFileUrls() ) {
				if ( detectArtifact[0] ) scanForClasses( jar, packages, entities );
				if ( detectArtifact[1] ) scanForHbmXmlFiles( jar, hbmFiles );
			}
			if ( ! info.excludeUnlistedClasses() ) {
				if ( detectArtifact[0] ) scanForClasses( info.getPersistenceUnitRootUrl(), packages, entities );
				if ( detectArtifact[1] ) scanForHbmXmlFiles( info.getPersistenceUnitRootUrl(), hbmFiles );
			}

			Properties properties = info.getProperties() != null ?
					info.getProperties() :
					new Properties();
			for ( Map.Entry entry : (Set<Map.Entry>) integration.entrySet() ) {
				if ( entry.getKey() instanceof String && entry.getValue() instanceof String ) {
					properties.setProperty( (String) entry.getKey(), (String) entry.getValue() );
				}
			}

			//FIXME send the appropriate entites.
			if ( "true".equalsIgnoreCase( properties.getProperty(HibernatePersistence.USE_CLASS_ENHANCER) ) ) {
				info.addTransformer( new InterceptFieldClassFileTransformer( entities ) );
			}

			workingVars.put( HibernatePersistence.CLASS_NAMES, entities );
			workingVars.put( HibernatePersistence.PACKAGE_NAMES, packages );
			if ( hbmFiles.size() > 0 ) workingVars.put( HibernatePersistence.HBXML_FILES, hbmFiles );

			//datasources
			boolean overridenDatasource = false;
			String dataSource = (String) integration.get( HibernatePersistence.JTA_DATASOURCE );
			if (dataSource != null) {
				overridenDatasource = true;
				properties.setProperty( Environment.DATASOURCE, dataSource );
			}
			dataSource = (String) integration.get( HibernatePersistence.NON_JTA_DATASOURCE );
			if (dataSource != null) {
				overridenDatasource = true;
				properties.setProperty( Environment.DATASOURCE, dataSource );
			}

			if ( ! overridenDatasource && (info.getJtaDataSource() != null || info.getNonJtaDataSource() != null ) ) {
				this.setDataSource(
						info.getJtaDataSource() != null ? info.getJtaDataSource() : info.getNonJtaDataSource()
				);
				this.setProperty( Environment.CONNECTION_PROVIDER, InjectedDataSourceConnectionProvider.class.getName() );
			}

			entityManagerFactory = createEntityManagerFactory( properties, workingVars );
		}
		finally {
			//After EMF, set the CCL back
			if ( ! info.getClassLoader().equals( contextClassLoader ) ) {
				thread.setContextClassLoader( contextClassLoader );
			}
		}
		return entityManagerFactory;
	}

	private void defineTransactionType(Object overridenTxType, String persistenceUnitName) {
		if (overridenTxType == null) {
			 if (transactionType == null) {
			 	transactionType = PersistenceUnitTransactionType.JTA; //this is the default value
			 }
		}
		else if (overridenTxType instanceof String) {
			transactionType = PersistenceXmlLoader.getTransactionType( (String) overridenTxType );
		}
		else if (overridenTxType instanceof PersistenceUnitTransactionType) {
			transactionType = (PersistenceUnitTransactionType) overridenTxType;
		}
		else {
			throw new PersistenceException(
					HibernatePersistence.TRANSACTION_TYPE  + " of the wrong class type"
							+ (persistenceUnitName != null ? " in unit " + persistenceUnitName : "")
							+ ": " + overridenTxType.getClass()
			);
		}

	}

	public Ejb3Configuration setProperty(String key, String value) {
		cfg.setProperty( key, value );
		return this;
	}

	private boolean[] getDetectedArtifacts(Properties properties, Map overridenProperties) {
		boolean[] result = new boolean[2];
		result[0] = false; //detect classes
		result[1] = false; //detect hbm
		String detect = overridenProperties != null ?
				(String) overridenProperties.get( HibernatePersistence.AUTODETECTION ) :
				null;
		detect = detect == null ?
				properties.getProperty( HibernatePersistence.AUTODETECTION, "class,hbm" ) :
				detect;
		StringTokenizer st = new StringTokenizer( detect, ",", false );
		while ( st.hasMoreElements() ) {
			String element = (String) st.nextElement();
			if ( "class".equalsIgnoreCase( element ) ) result[0] = true;
			if ( "hbm".equalsIgnoreCase( element ) ) result[1] = true;
		}
		log.debug( "Detect class: " + result[0] + "; detect hbm: " + result[1] );
		return result;
	}

	private JarVisitor.Filter[] getFilters(Properties properties, Map overridenProperties) {
		boolean[] result = getDetectedArtifacts( properties, overridenProperties );
		int size = ( result[0] ? 2 : 0 ) + ( result[1] ? 1 : 0 );
		JarVisitor.Filter[] filters = new JarVisitor.Filter[size];
		if ( result[0] ) {
			filters[0] = new JarVisitor.PackageFilter( false, null ) {
				public boolean accept(String javaElementName) {
					return true;
				}
			};
			filters[1] = new JarVisitor.ClassFilter(
					false, new Class[]{
					Entity.class,
					MappedSuperclass.class,
					Embeddable.class}
			) {
				public boolean accept(String javaElementName) {
					return true;
				}
			};
		}
		if ( result[1] ) {
			filters[size - 1] = new JarVisitor.FileFilter( true ) {
				public boolean accept(String javaElementName) {
					return javaElementName.endsWith( "hbm.xml" );
				}
			};
		}
		return filters;
	}

	private void scanForHbmXmlFiles(URL jar, List<InputStream> hbmxmls) {
		Iterator it = ArchiveBrowser.getBrowser(
				jar, new ArchiveBrowser.Filter() {
			public boolean accept(String filename) {
				return filename.endsWith( ".hbm.xml" );
			}
		}
		);

		while ( it.hasNext() ) {
			InputStream stream = (InputStream) it.next();
			hbmxmls.add( stream );
		}
	}

	private void scanForClasses(URL jar, List<String> packages, List<String> entities) {
		Iterator it = null;
		try {
			it = ArchiveBrowser.getBrowser(
					jar, new ArchiveBrowser.Filter() {
				public boolean accept(String filename) {
					return filename.endsWith( ".class" );
				}
			}
			);
		}
		catch (RuntimeException e) {
			throw new RuntimeException( "error trying to scan <jar-file>: " + jar.toString(), e );
		}

		// need to look into every entry in the archive to see if anybody has tags
		// defined.
		while ( it.hasNext() ) {
			InputStream stream = (InputStream) it.next();
			DataInputStream dstream = new DataInputStream( stream );
			ClassFile cf = null;
			try {
				try {
					cf = new ClassFile( dstream );
				}
				finally {
					dstream.close();
					stream.close();
				}
			}
			catch (IOException e) {
				throw new RuntimeException( e );
			}
			if ( cf.getName().endsWith( ".package-info" ) ) {
				int idx = cf.getName().indexOf( ".package-info" );
				String pkgName = cf.getName().substring( 0, idx );
				log.info( "found package: " + pkgName );
				packages.add( pkgName );
				continue;
			}

			AnnotationsAttribute visible = (AnnotationsAttribute) cf.getAttribute( AnnotationsAttribute.visibleTag );
			if ( visible != null ) {
				boolean isEntity = visible.getAnnotation( Entity.class.getName() ) != null;
				if ( isEntity ) {
					log.info( "found EJB3 Entity bean: " + cf.getName() );
					entities.add( cf.getName() );
				}
				boolean isEmbeddable = visible.getAnnotation( Embeddable.class.getName() ) != null;
				if ( isEmbeddable ) {
					log.info( "found EJB3 @Embeddable: " + cf.getName() );
					entities.add( cf.getName() );
				}
				boolean isEmbeddableSuperclass = visible.getAnnotation( MappedSuperclass.class.getName() ) != null;
				if ( isEmbeddableSuperclass ) {
					log.info( "found EJB3 @EmbeddableSuperclass: " + cf.getName() );
					entities.add( cf.getName() );
				}
			}
		}
	}

	/**
	 * create a factory from a list of properties and
	 * HibernatePersistence.CLASS_NAMES -> Collection<String> (use to list the classes from config files
	 * HibernatePersistence.PACKAGE_NAMES -> Collection<String> (use to list the mappings from config files
	 * HibernatePersistence.HBXML_FILES -> Collection<InputStream> (input streams of hbm files)
	 * HibernatePersistence.LOADED_CLASSES -> Collection<Class> (list of loaded classes)
	 * <p/>
	 * <b>Used by JBoss AS only</b>
	 */
	// This is used directly by JBoss so don't remove until further notice.  bill@jboss.org
	public EntityManagerFactory createEntityManagerFactory(Map workingVars) {
		Properties props = new Properties();
		if ( workingVars != null ) {
			props.putAll( workingVars );
			//remove huge non String elements for a clean props
			props.remove( HibernatePersistence.CLASS_NAMES );
			props.remove( HibernatePersistence.PACKAGE_NAMES );
			props.remove( HibernatePersistence.HBXML_FILES );
			props.remove( HibernatePersistence.LOADED_CLASSES );
		}
		return createEntityManagerFactory( props, workingVars );
	}

	/**
	 * Create an EntityManagerFactory <b>when</b> the configuration is ready
	 */
	public EntityManagerFactory createEntityManagerFactory() {
		return createEntityManagerFactory( cfg.getProperties(), new HashMap() );
	}

	private EntityManagerFactory buildEntityManagerFactory(boolean discardOnClose) {
		return new EntityManagerFactoryImpl(
				cfg.buildSessionFactory(),
				transactionType,
				discardOnClose
		);
	}

	/**
	 * create a factory from a canonical workingVars map and the overriden properties
	 */
	private EntityManagerFactory createEntityManagerFactory(
			Properties properties, Map workingVars
	) {
		Properties preparedProperties = prepareProperties( properties, workingVars );
		if ( workingVars == null ) workingVars = CollectionHelper.EMPTY_MAP;

		if ( preparedProperties.containsKey( HibernatePersistence.CFG_FILE ) ) {
			String cfgFileName = preparedProperties.getProperty( HibernatePersistence.CFG_FILE );
			cfg.configure( cfgFileName );
		}

		cfg.addProperties( preparedProperties ); //persistence.xml has priority over hibernate.Cfg.xml

		addClassesToSessionFactory( workingVars );

		//processes specific properties
		List<String> jaccKeys = new ArrayList<String>();


		Configuration defaultConf = new AnnotationConfiguration();
		Interceptor defaultInterceptor = defaultConf.getInterceptor();
		NamingStrategy defaultNamingStrategy = defaultConf.getNamingStrategy();

		Iterator propertyIt = preparedProperties.keySet().iterator();
		while ( propertyIt.hasNext() ) {
			Object uncastObject = propertyIt.next();
			//had to be safe
			if ( uncastObject != null && uncastObject instanceof String ) {
				String propertyKey = (String) uncastObject;
				if ( propertyKey.startsWith( HibernatePersistence.CLASS_CACHE_PREFIX ) ) {
					setCacheStrategy( propertyKey, preparedProperties, true );
				}
				else if ( propertyKey.startsWith( HibernatePersistence.COLLECTION_CACHE_PREFIX ) ) {
					setCacheStrategy( propertyKey, preparedProperties, false );
				}
				else if ( propertyKey.startsWith( HibernatePersistence.JACC_PREFIX )
						&& ! ( propertyKey.equals( HibernatePersistence.JACC_CONTEXT_ID )
						|| propertyKey.equals( HibernatePersistence.JACC_ENABLED ) ) ) {
					jaccKeys.add( propertyKey );
				}
			}
		}
		if ( preparedProperties.containsKey( HibernatePersistence.INTERCEPTOR )
				&& ( cfg.getInterceptor() == null
				|| cfg.getInterceptor().equals( defaultInterceptor ) ) ) {
			//cfg.setInterceptor has precedence over configuration file
			String interceptorName = preparedProperties.getProperty( HibernatePersistence.INTERCEPTOR );
			try {
				Class interceptor = classForName( interceptorName );
				cfg.setInterceptor( (Interceptor) interceptor.newInstance() );
			}
			catch (ClassNotFoundException e) {
				throw new PersistenceException( "Unable to find interceptor class: " + interceptorName, e );
			}
			catch (IllegalAccessException e) {
				throw new PersistenceException(
						"Unable to access interceptor class: " + interceptorName, e
				);
			}
			catch (InstantiationException e) {
				throw new PersistenceException(
						"Unable to instanciate interceptor class: " + interceptorName, e
				);
			}
			catch (ClassCastException e) {
				throw new PersistenceException(
						"Interceptor class does not implement Interceptor interface: " + interceptorName, e
				);
			}
		}
		if ( preparedProperties.containsKey( HibernatePersistence.NAMING_STRATEGY )
				&& ( cfg.getNamingStrategy() == null
				|| cfg.getNamingStrategy().equals( defaultNamingStrategy ) ) ) {
			//cfg.setNamingStrategy has precedence over configuration file
			String namingStrategyName = preparedProperties.getProperty( HibernatePersistence.NAMING_STRATEGY );
			try {
				Class namingStragegy = classForName( namingStrategyName );
				cfg.setNamingStrategy( (NamingStrategy) namingStragegy.newInstance() );
			}
			catch (ClassNotFoundException e) {
				throw new PersistenceException(
						"Unable to find naming strategy class: " + namingStrategyName, e
				);
			}
			catch (IllegalAccessException e) {
				throw new PersistenceException(
						"Unable to access naming strategy class: " + namingStrategyName, e
				);
			}
			catch (InstantiationException e) {
				throw new PersistenceException(
						"Unable to instanciate naming strategy class: " + namingStrategyName, e
				);
			}
			catch (ClassCastException e) {
				throw new PersistenceException(
						"Naming strategyy class does not implement NmaingStrategy interface: " + namingStrategyName,
						e
				);
			}
		}

		if ( jaccKeys.size() > 0 ) {
			addSecurity( jaccKeys, preparedProperties );
		}

		//initialize listeners
		listenerConfigurator.setProperties( preparedProperties );
		listenerConfigurator.setValidator( true );
		listenerConfigurator.configure();

		//some spec compliance checking
		//TODO centralize that?
		if ( ! "true".equalsIgnoreCase( cfg.getProperty( Environment.AUTOCOMMIT ) ) ) {
			log.warn( Environment.AUTOCOMMIT + " = false break the EJB3 specification" );
		}
		boolean discardOnClose = preparedProperties.getProperty( HibernatePersistence.DISCARD_PC_ON_CLOSE).equals( "true");
		return buildEntityManagerFactory(discardOnClose);
	}

	private void addClassesToSessionFactory(Map workingVars) {
		if ( workingVars.containsKey( HibernatePersistence.CLASS_NAMES ) ) {
			Collection<String> classNames = (Collection<String>) workingVars.get(
					HibernatePersistence.CLASS_NAMES
			);
			addNamedAnnotatedClasses( this, classNames );
		}
		if ( workingVars.containsKey( HibernatePersistence.LOADED_CLASSES ) ) {
			Collection<Class> classes = (Collection<Class>) workingVars.get( HibernatePersistence.LOADED_CLASSES );
			for ( Class clazz : classes ) {
				cfg.addAnnotatedClass( clazz );
			}
		}
		if ( workingVars.containsKey( HibernatePersistence.PACKAGE_NAMES ) ) {
			Collection<String> packages = (Collection<String>) workingVars.get(
					HibernatePersistence.PACKAGE_NAMES
			);
			for ( String pkg : packages ) {
				//FIXME classloader
				cfg.addPackage( pkg );
			}
		}
		if ( workingVars.containsKey( HibernatePersistence.HBXML_FILES ) ) {
			Collection<InputStream> hbmXmlFiles = (Collection<InputStream>) workingVars.get(
					HibernatePersistence.HBXML_FILES
			);
			for ( InputStream is : hbmXmlFiles ) {
				cfg.addInputStream( is );
			}
		}
	}

	private Properties prepareProperties(Properties properties, Map workingVars ) {
		Properties preparedProperties = new Properties();

		//defaults different to Hibernate
		preparedProperties.setProperty( Environment.RELEASE_CONNECTIONS, "auto" );
		//settings that always apply to a compliant EJB3
		preparedProperties.setProperty( Environment.AUTOCOMMIT, "true" );
		preparedProperties.setProperty( Environment.USE_IDENTIFIER_ROLLBACK, "false" );
		preparedProperties.setProperty( Environment.FLUSH_BEFORE_COMPLETION, "false" );
		preparedProperties.setProperty( HibernatePersistence.DISCARD_PC_ON_CLOSE, "false" );

		//override the new defaults with the user defined ones
		if ( properties != null ) preparedProperties.putAll( properties );
		defineTransactionType(
				preparedProperties.getProperty( HibernatePersistence.TRANSACTION_TYPE ),
				(String) workingVars.get( HibernatePersistence.PERSISTENCE_UNIT_NAME )
		);
		boolean hasTxStrategy = StringHelper.isNotEmpty( preparedProperties.getProperty( Environment.TRANSACTION_STRATEGY ) );
		if (! hasTxStrategy && transactionType == PersistenceUnitTransactionType.JTA) {
			preparedProperties.setProperty( Environment.TRANSACTION_STRATEGY, JoinableCMTTransactionFactory.class.getName() );
		}
		else if (! hasTxStrategy && transactionType == PersistenceUnitTransactionType.RESOURCE_LOCAL) {
			preparedProperties.setProperty( Environment.TRANSACTION_STRATEGY, JDBCTransactionFactory.class.getName() );
		}
		else {
			transactionType = PersistenceUnitTransactionType.RESOURCE_LOCAL;
			//new AssertionFailure("Unknown PersisntenceUnitTransactionType: " + transactionType);
		}
		if (hasTxStrategy) {
			log.warn("Overriding " + Environment.TRANSACTION_STRATEGY + " is dangerous, this might break the EJB3 specification implementation");
		}
		if ( preparedProperties.getProperty( Environment.FLUSH_BEFORE_COMPLETION ).equals( "true" ) ) {
			preparedProperties.setProperty( Environment.FLUSH_BEFORE_COMPLETION, "false" );
			log.warn("Defining " + Environment.FLUSH_BEFORE_COMPLETION + "=true ignored in HEM");
		}
		return preparedProperties;
	}

	private Class classForName(
			String className
	) throws ClassNotFoundException {
			return ReflectHelper.classForName( className, this.getClass() );
	}

	private void setCacheStrategy(String propertyKey, Map properties, boolean isClass) {
		String role = propertyKey.substring(
				( isClass ? HibernatePersistence.CLASS_CACHE_PREFIX
						.length() : HibernatePersistence.COLLECTION_CACHE_PREFIX.length() )
						+ 1
		);
		//dot size added
		String value = (String) properties.get( propertyKey );
		StringTokenizer params = new StringTokenizer( value, ";, " );
		if ( !params.hasMoreTokens() ) {
			StringBuilder error = new StringBuilder( "Illegal usage of " );
			error.append(
					isClass ? HibernatePersistence.CLASS_CACHE_PREFIX : HibernatePersistence.COLLECTION_CACHE_PREFIX
			);
			error.append( ": " ).append( propertyKey ).append( " " ).append( value );
			throw new MappingException( error.toString() );
		}
		String usage = params.nextToken();
		String region = null;
		if ( params.hasMoreTokens() ) {
			region = params.nextToken();
		}
		if ( isClass ) {
			boolean lazyProperty = true;
			if ( params.hasMoreTokens() ) {
				lazyProperty = "all".equalsIgnoreCase( params.nextToken() );
			}
			cfg.setCacheConcurrencyStrategy( role, usage, region, lazyProperty );
		}
		else {
			cfg.setCollectionCacheConcurrencyStrategy( role, usage, region );
		}
	}

	private void addSecurity(List<String> keys, Map properties) {
		log.debug( "Adding security" );
		if ( !properties.containsKey( HibernatePersistence.JACC_CONTEXT_ID ) ) {
			throw new MappingException(
					"Entities have been configured for JACC, but "
							+ HibernatePersistence.JACC_CONTEXT_ID
							+ " has not been set"
			);
		}
		String contextId = (String) properties.get( HibernatePersistence.JACC_CONTEXT_ID );
		setProperty( Environment.JACC_CONTEXTID, contextId );

		int roleStart = HibernatePersistence.JACC_PREFIX.length() + 1;

		for ( String key : keys ) {
			JACCConfiguration jaccCfg = new JACCConfiguration( contextId );
			try {
				String role = key.substring( roleStart, key.indexOf( '.', roleStart ) );
				int classStart = roleStart + role.length() + 1;
				String clazz = key.substring( classStart, key.length() );
				String actions = (String) properties.get( key );
				jaccCfg.addPermission( role, clazz, actions );
			}
			catch (IndexOutOfBoundsException e) {
				throw new MappingException( "Illegal usage of " + HibernatePersistence.JACC_PREFIX + ": " + key );
			}
		}
	}

	private void addNamedAnnotatedClasses(
			Ejb3Configuration cfg, Collection<String> classNames
	) {
		for ( String name : classNames ) {
			try {
				Class clazz = classForName( name );
				cfg.addAnnotatedClass( clazz );
			}
			catch (ClassNotFoundException cnfe) {
				Package pkg;
				try {
					pkg = classForName( name + ".package-info" ).getPackage();
				}
				catch (ClassNotFoundException e) {
					pkg = null;
				}
				if ( pkg == null ) {
					throw new IllegalArgumentException( "class or package not found", cnfe );
				}
				else {
					cfg.addPackage( name );
				}
			}
		}
	}


	public Settings buildSettings() throws HibernateException {
		return settingsFactory.buildSettings( cfg.getProperties() );
	}

	public Ejb3Configuration addProperties(Properties props) {
		cfg.addProperties( props );
		return this;
	}

	public Ejb3Configuration addAnnotatedClass(Class persistentClass) throws MappingException {
		cfg.addAnnotatedClass( persistentClass );
		return this;
	}

	public Ejb3Configuration configure(String resource) throws HibernateException {
		cfg.configure( resource );
		return this;
	}

	public Ejb3Configuration addPackage(String packageName) throws MappingException {
		cfg.addPackage( packageName );
		return this;
	}

	public Ejb3Configuration addFile(String xmlFile) throws MappingException {
		cfg.addFile( xmlFile );
		return this;
	}

	public Ejb3Configuration addClass(Class persistentClass) throws MappingException {
		cfg.addClass( persistentClass );
		return this;
	}

	public Ejb3Configuration addFile(File xmlFile) throws MappingException {
		cfg.addFile( xmlFile );
		return this;
	}

	public void buildMappings() {
		cfg.buildMappings();
	}

	public Iterator getClassMappings() {
		return cfg.getClassMappings();
	}

	public EventListeners getEventListeners() {
		return cfg.getEventListeners();
	}

	SessionFactory buildSessionFactory() throws HibernateException {
		return cfg.buildSessionFactory();
	}

	public Iterator getTableMappings() {
		return cfg.getTableMappings();
	}

	public PersistentClass getClassMapping(String persistentClass) {
		return cfg.getClassMapping( persistentClass );
	}

	public org.hibernate.mapping.Collection getCollectionMapping(String role) {
		return cfg.getCollectionMapping( role );
	}

	public void setEntityResolver(EntityResolver entityResolver) {
		cfg.setEntityResolver( entityResolver );
	}

	public Map getNamedQueries() {
		return cfg.getNamedQueries();
	}

	public Interceptor getInterceptor() {
		return cfg.getInterceptor();
	}

	public Properties getProperties() {
		return cfg.getProperties();
	}

	public Ejb3Configuration setInterceptor(Interceptor interceptor) {
		cfg.setInterceptor( interceptor );
		return this;
	}

	public Ejb3Configuration setProperties(Properties properties) {
		cfg.setProperties( properties );
		return this;
	}

	public Map getFilterDefinitions() {
		return cfg.getFilterDefinitions();
	}

	public void addFilterDefinition(FilterDefinition definition) {
		cfg.addFilterDefinition( definition );
	}

	public void addAuxiliaryDatabaseObject(AuxiliaryDatabaseObject object) {
		cfg.addAuxiliaryDatabaseObject( object );
	}

	public NamingStrategy getNamingStrategy() {
		return cfg.getNamingStrategy();
	}

	public Ejb3Configuration setNamingStrategy(NamingStrategy namingStrategy) {
		cfg.setNamingStrategy( namingStrategy );
		return this;
	}

	public void setListeners(String type, String[] listenerClasses) {
		cfg.setListeners( type, listenerClasses );
	}

	public void setListeners(String type, Object[] listeners) {
		cfg.setListeners( type, listeners );
	}

	/**
	 * This API is intended to give a read-only configuration.
	 * It is sueful when working with SchemaExport or any Configuration based
	 * tool.
	 * DO NOT update configuration through it.
	 */
	public AnnotationConfiguration getHibernateConfiguration() {
		//TODO make it really read only (maybe through proxying)
		return cfg;
	}
}
