package com.bm.cfg;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import javax.persistence.EntityManagerFactory;

import org.hibernate.ejb.Ejb3Configuration;

/**
 * This class reads and holds the ejb3unit configuration.
 * 
 * @author Daniel Wiese
 */
public final class Ejb3UnitCfg {

	/** Konfiguration key. * */
	public static final String EJB3UNIT_PROPERTIES_NAME = "ejb3unit.properties";

	/** Konfiguration key. * */
	public static final String KEY_AUTOMATIC_SCHEMA_UPDATE = "ejb3unit.schema.update";

	/** Konfiguration key. * */
	public static final String KEY_CONNECTION_DRIVER_CLASS = "ejb3unit.connection.driver_class";

	/** Konfiguration key. * */
	public static final String KEY_CONNECTION_PASSWORD = "ejb3unit.connection.password";

	/** Konfiguration key. * */
	public static final String KEY_CONNECTION_URL = "ejb3unit.connection.url";

	/** Konfiguration key. * */
	public static final String KEY_CONNECTION_USERNAME = "ejb3unit.connection.username";

	/** Konfiguration key. * */
	public static final String KEY_IN_MEMORY_TEST = "ejb3unit.inMemoryTest";

	/** Konfiguration key. * */
	public static final String KEY_SHOW_SQL = "ejb3unit.show_sql";

	/** Konfiguration key. * */
	public static final String KEY_SQL_DIALECT = "ejb3unit.dialect";

	/** Konfiguration key. * */
	public static final String KEY_CACHE_PROVIDER = "ejb3unit.cache_provider";
	
	/** Konfiguration key. * */
	public static final String KEY_USE_SECOND_LEVEL_CACHE = "ejb3unit.use_second_level_cache";
	
	/** Konfiguration key. * */
	public static final String KEY_USE_QUERY_CACHE = "ejb3unit.use_query_cache";

	private static final org.apache.log4j.Logger log = org.apache.log4j.Logger
			.getLogger(Ejb3UnitCfg.class);

	/** Konfiguration key. * */
	private static Ejb3UnitCfg singelton = null;

	private final Properties config;

	private Collection<Class<? extends Object>> currentEntytiesToTest;

	private EntityManagerFactory entityManagerFactory = null;

	private boolean inMemory = true;

	private Ejb3UnitCfg() {
		this.currentEntytiesToTest = new ArrayList<Class<? extends Object>>();
		try {
			final InputStream inStr = Thread.currentThread()
					.getContextClassLoader().getResourceAsStream(
							EJB3UNIT_PROPERTIES_NAME);
			config = new Properties();
			if (inStr != null) {
				config.load(inStr);

			} else {
				// run the system in memory if no config if found
				System.setProperty(KEY_IN_MEMORY_TEST, "true");
			}
		} catch (Exception e) {
			// propagete the exception
			throw new RuntimeException(e);
		}
	}

	/**
	 * Returns the hibernate configuration settings which will be converted form
	 * ejb3configaration settings.
	 * 
	 * @return hibernate entity manager Ejb3Configuration
	 */
	public Ejb3Configuration getEJB3Configuration() {
		Ejb3Configuration cfg = new Ejb3Configuration();
		// tranform the ejb3unit configuration to the hibernate
		// configuration
		final Properties prop = cfg.getProperties();
		if (Boolean
				.valueOf(this.config.getProperty(KEY_IN_MEMORY_TEST, "true"))) {
			// configuration for in memory db
			this.inMemory = true;
			this.setProperty(prop, "hibernate.connection.url",
					"jdbc:hsqldb:mem:ejb3unit");
			this.setProperty(prop, "hibernate.connection.driver_class",
					"org.hsqldb.jdbcDriver");
			this.setProperty(prop, "hibernate.connection.username", "sa");
			this.setProperty(prop, "hibernate.connection.password", "");
			this.setProperty(prop, "hibernate.dialect",
					"org.hibernate.dialect.HSQLDialect");
			this.setProperty(prop, "hibernate.hbm2ddl.auto", "create-drop");
		} else {
			this.inMemory = false;
			this.setProperty(prop, "hibernate.connection.url", this.config
					.getProperty(KEY_CONNECTION_URL));
			this.setProperty(prop, "hibernate.connection.driver_class",
					this.config.getProperty(KEY_CONNECTION_DRIVER_CLASS));
			this.setProperty(prop, "hibernate.connection.username", this.config
					.getProperty(KEY_CONNECTION_USERNAME));
			this.setProperty(prop, "hibernate.connection.password", this.config
					.getProperty(KEY_CONNECTION_PASSWORD));
			this.setProperty(prop, "hibernate.dialect", this.config
								.getProperty(KEY_SQL_DIALECT));
			this.setProperty(prop, "hibernate.hbm2ddl.auto", this.config
					.getProperty(KEY_AUTOMATIC_SCHEMA_UPDATE));
		}
		this.setProperty(prop, "hibernate.cache.provider_class", this.config
				.getProperty(KEY_CACHE_PROVIDER));
		this.setProperty(prop, "hibernate.show_sql", this.config
				.getProperty(KEY_SHOW_SQL));
		this.setProperty(prop, "hibernate.cache.use_second_level_cache", this.config
				.getProperty(KEY_USE_SECOND_LEVEL_CACHE));
		this.setProperty(prop, "hibernate.cache.use_query_cache", this.config
				.getProperty(KEY_USE_QUERY_CACHE));
		// static properties
		this.setProperty(prop, "hibernate.transaction.factory_class",
				"org.hibernate.transaction.JDBCTransactionFactory");
		return cfg;
	}

	/**
	 * Returns the entityManagerFactory.
	 * 
	 * @return Returns the entityManagerFactory.
	 */
	public EntityManagerFactory getEntityManagerFactory() {
		// lazy initialization
		if (this.entityManagerFactory == null) {
			final Ejb3Configuration cfg = this.getEJB3Configuration();
			// add anotated entity beans (to test)
			if (this.currentEntytiesToTest != null) {
				for (Class<? extends Object> entityToTest : this.currentEntytiesToTest) {
					cfg.addAnnotatedClass(entityToTest);
				}
			}
			// fix later for now it´s working
			this.entityManagerFactory = cfg.createEntityManagerFactory();
		}

		return this.entityManagerFactory;
	}

	/**
	 * Retruns a value of a config key.
	 * 
	 * @author Daniel Wiese
	 * @since 08.11.2005
	 * @param key -
	 *            the key
	 * @return - a value of an config key
	 */
	public String getValue(String key) {
		return this.config.getProperty(key);
	}

	/**
	 * Returns the inMemory.
	 * 
	 * @return Returns the inMemory.
	 */
	public boolean isInMemory() {
		return inMemory;
	}

	/**
	 * This helper method will ignore null properties (keys or values)
	 * 
	 * @param prop -
	 *            the propertiy file
	 * @param key -
	 *            the key
	 * @param value -
	 *            the value
	 */
	private void setProperty(Properties prop, String key, String value) {
		if (key != null && value != null) {
			prop.setProperty(key, value);
		}
	}

	/**
	 * Add entities to test to the configuration.
	 * 
	 * @param entytiesToTest -
	 *            the used entity beans
	 */
	public static synchronized void addEntytiesToTest(
			Collection<Class<? extends Object>> entytiesToTest) {
		// init only if the entits for the test change or not initialized at all
		if (singelton == null) {
			singelton = new Ejb3UnitCfg();
		}

		singelton.currentEntytiesToTest.addAll(entytiesToTest);
		// reset the factory (if already created) because we have new beans
		singelton.entityManagerFactory = null;
	}

	/**
	 * Creates / returns a singelton instance of the configuration.
	 * 
	 * @return - a singelton instance
	 */
	public static synchronized Ejb3UnitCfg getConfiguration() {
		if (singelton == null) {
			singelton = new Ejb3UnitCfg();
		}

		return singelton;
	}

	/**
	 * Liefert die properties der Jndi rules.
	 * 
	 * @author Daniel Wiese
	 * @since 06.07.2006
	 * @param key -
	 *            der key
	 * @return - die sell rules
	 */
	public static List<JndiProperty> getJndiBindings() {
		return getNestedProperty("ejb3unit_jndi", JndiProperty.class);
	}

	/**
	 * Checks if the configuration was initialized.
	 * 
	 * @return - a singelton instance
	 */
	public static boolean isInitialized() {
		return (singelton != null);
	}

	/**
	 * Liest ein NestedProperty Objekte ein.
	 * 
	 * @author Daniel Wiese
	 * @since 04.12.2005
	 * @param key -
	 *            ConfigKeys - die keys um im Property-File einen Wert
	 *            auszulesen
	 * @param toRead -
	 *            die klasse die eingelesen werden soll
	 * @return - eine Liste mit gelesenen NestedProperty Objekten.
	 */
	private static <T extends NestedProperty> List<T> getNestedProperty(
			String key, Class<T> toRead) {
		try {
			final List<T> back = new ArrayList<T>();
			boolean continueRead = true;
			int counter = 0;
			StringBuilder sb = null;
			while (continueRead) {
				T currentInstance = toRead.newInstance();

				counter++;
				sb = new StringBuilder();
				sb.append(key).append(".").append(counter);

				for (String currentInnerValue : currentInstance.innerValues()) {
					StringBuilder innerValue = new StringBuilder(sb);
					innerValue.append(".").append(currentInnerValue);
					final String value = getConfiguration().getValue(
							innerValue.toString());
					if (value != null) {
						currentInstance.setValue(currentInnerValue, value);
					} else {
						continueRead = false;
						break;
					}
				}

				if (continueRead) {
					back.add(currentInstance);
				}
			}

			return back;
		} catch (InstantiationException e) {
			log.error("Can´t instantiate class: " + toRead);
			throw new IllegalArgumentException("Can´t instantiate class: "
					+ toRead);
		} catch (IllegalAccessException e) {
			log.error("Can´t instantiate class: " + toRead);
			throw new IllegalArgumentException("Can´t instantiate class: "
					+ toRead);
		}
	}

}
