package com.bm.cfg;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
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
	public static final String KEY_CONNECTION_URL = "ejb3unit.connection.url";

	/** Konfiguration key. * */
	public static final String KEY_CONNECTION_DRIVER_CLASS = "ejb3unit.connection.driver_class";

	/** Konfiguration key. * */
	public static final String KEY_CONNECTION_USERNAME = "ejb3unit.connection.username";

	/** Konfiguration key. * */
	public static final String KEY_CONNECTION_PASSWORD = "ejb3unit.connection.password";

	/** Konfiguration key. * */
	public static final String KEY_SQL_DIALECT = "ejb3unit.dialect";

	/** Konfiguration key. * */
	public static final String KEY_SHOW_SQL = "ejb3unit.show_sql";

	/** Konfiguration key. * */
	public static final String KEY_AUTOMATIC_SHEMA_UPDATE = "ejb3unit.shema.update";

	public static final String KEY_IN_MEMORY_TEST = "ejb3unit.inMemoryTest";

	/** Konfiguration key. * */
	private static Ejb3UnitCfg singelton = null;

	private final Properties config;

	private EntityManagerFactory entityManagerFactory = null;

	private Collection<Class<? extends Object>> currentEntytiesToTest;

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
	 * Checks if the configuration was initialized.
	 * 
	 * @return - a singelton instance
	 */
	public static boolean isInitialized() {
		return (singelton != null);
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
			this.setProperty(prop, "dialect",
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
			this.setProperty(prop, "dialect", this.config
					.getProperty(KEY_SQL_DIALECT));
			this.setProperty(prop, "hibernate.show_sql", this.config
					.getProperty(KEY_SHOW_SQL));
			this.setProperty(prop, "hibernate.hbm2ddl.auto", this.config
					.getProperty(KEY_AUTOMATIC_SHEMA_UPDATE));
		}
		this.setProperty(prop, "hibernate.show_sql", this.config
				.getProperty(KEY_SHOW_SQL));
		// static properties
		this.setProperty(prop, "hibernate.transaction.factory_class",
				"org.hibernate.transaction.JDBCTransactionFactory");
		return cfg;

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
	 * Returns the inMemory.
	 * 
	 * @return Returns the inMemory.
	 */
	public boolean isInMemory() {
		return inMemory;
	}

}
