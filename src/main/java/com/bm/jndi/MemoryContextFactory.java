package com.bm.jndi;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;

/**
 * The initial context factory for the <code>MemoryContext</code> JNDI naming
 * provider. 
 */
public class MemoryContextFactory implements InitialContextFactory {
	private static final org.apache.log4j.Logger log = org.apache.log4j.Logger
			.getLogger(MemoryContextFactory.class);
	
	private static Context instance;

	/**
	 * Default constructor.
	 */
	public MemoryContextFactory() {
	}


	public Context getInitialContext(Hashtable environment)
			throws NamingException {
		return getContext(environment);
	}

	private synchronized Context getContext(Hashtable environment)
			throws NamingException {
		if (instance == null) {
			log.info("Creating EJB3Unit initial JNDI context");
			instance = new MemoryContext();
		}

		return instance;
	}

}
