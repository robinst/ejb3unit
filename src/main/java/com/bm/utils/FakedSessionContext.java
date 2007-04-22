package com.bm.utils;

import java.security.Principal;
import java.util.Hashtable;
import java.util.Properties;

import javax.ejb.EJBHome;
import javax.ejb.EJBLocalHome;
import javax.ejb.EJBLocalObject;
import javax.ejb.EJBObject;
import javax.ejb.SessionContext;
import javax.ejb.TimerService;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.transaction.UserTransaction;
import javax.xml.rpc.handler.MessageContext;

import com.bm.jndi.MemoryContextFactory;

/**
 * Represnts a dummy implementation for a session context. Later we can implements more
 * functionality here.
 * @author Daniel Wiese
 *
 */
public class FakedSessionContext implements SessionContext {
	
	private final InitialContext ctx;
	
	public FakedSessionContext(){
		Hashtable<String, String> env = new Hashtable<String, String>();
		env.put(Context.INITIAL_CONTEXT_FACTORY, MemoryContextFactory.class
				.getName());
		try {
			ctx = new InitialContext(env);
		} catch (NamingException e) {
			throw new RuntimeException("Can't setup JNDI context for testing");
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public <T> T getBusinessObject(Class<T> businessInterface) {
		return null;
	}
	

	/**
	 * {@inheritDoc}
	 */
	public EJBLocalObject getEJBLocalObject() {
		return null;
	}


	/**
	 * {@inheritDoc}
	 */
	public EJBObject getEJBObject()  {
		return null;
	}


	/**
	 * {@inheritDoc}
	 */	
	public Class getInvokedBusinessInterface()  {
		return null;
	}


	/**
	 * {@inheritDoc}
	 */
	public MessageContext getMessageContext() {
		return null;
	}



	/**
	 * {@inheritDoc}
	 */
	public Principal getCallerPrincipal() {
		return null;
	}


	/**
	 * {@inheritDoc}
	 */
	public EJBHome getEJBHome() {
		return null;
	}


	/**
	 * {@inheritDoc}
	 */
	public EJBLocalHome getEJBLocalHome() {
		return null;
	}


	/**
	 * {@inheritDoc}
	 */
	public Properties getEnvironment() {
		return null;
	}


	/**
	 * {@inheritDoc}
	 */
	public boolean getRollbackOnly()  {
		return false;
	}


	/**
	 * {@inheritDoc}
	 */
	public TimerService getTimerService()  {
		return null;
	}


	/**
	 * {@inheritDoc}
	 */
	public UserTransaction getUserTransaction()  {
		return null;
	}


	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("deprecation")
	public boolean isCallerInRole(java.security.Identity role) {
		return false;
	}


	/**
	 * {@inheritDoc}
	 */
	public boolean isCallerInRole(String roleName) {
		return false;
	}


	/**
	 * {@inheritDoc}
	 */
	public Object lookup(String name) {
		try {
			return ctx.lookup(name);
		} catch (NamingException e) {
			throw new RuntimeException(e);
		}
	}


	/**
	 * {@inheritDoc}
	 */
	public void setRollbackOnly() {
		
	}


	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("deprecation")
	public java.security.Identity getCallerIdentity() {
		return null;
	}


}
