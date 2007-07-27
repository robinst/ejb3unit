package com.bm.utils.substitues;

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

import org.apache.commons.lang.NotImplementedException;

import com.bm.jndi.MemoryContextFactory;

/**
 * Represnts a dummy implementation for a session context. Later we can
 * implements more functionality here.
 * 
 * @author Daniel Wiese
 * 
 */
public class FakedSessionContext implements SessionContext {

	private final InitialContext ctx;

	public FakedSessionContext() {
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
		throw new NotImplementedException("Not yet implemented");
	}

	/**
	 * {@inheritDoc}
	 */
	public EJBLocalObject getEJBLocalObject() {
		throw new NotImplementedException("Not yet implemented");
	}

	/**
	 * {@inheritDoc}
	 */
	public EJBObject getEJBObject() {
		throw new NotImplementedException("Not yet implemented");
	}

	/**
	 * {@inheritDoc}
	 */
	public Class getInvokedBusinessInterface() {
		throw new NotImplementedException("Not yet implemented");
	}

	/**
	 * {@inheritDoc}
	 */
	public MessageContext getMessageContext() {
		throw new NotImplementedException("Not yet implemented");
	}

	/**
	 * {@inheritDoc}
	 */
	public Principal getCallerPrincipal() {
		throw new NotImplementedException("Not yet implemented");
	}

	/**
	 * {@inheritDoc}
	 */
	public EJBHome getEJBHome() {
		throw new NotImplementedException("Not yet implemented");
	}

	/**
	 * {@inheritDoc}
	 */
	public EJBLocalHome getEJBLocalHome() {
		throw new NotImplementedException("Not yet implemented");
	}

	/**
	 * {@inheritDoc}
	 */
	public Properties getEnvironment() {
		throw new NotImplementedException("Not yet implemented");
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean getRollbackOnly() {
		throw new NotImplementedException("Not yet implemented");
	}

	/**
	 * {@inheritDoc}
	 */
	public TimerService getTimerService() {
		throw new NotImplementedException(
				"Please use injection for timer: @Resource private TimerService timerService;");
	}

	/**
	 * {@inheritDoc}
	 */
	public UserTransaction getUserTransaction() {
		throw new NotImplementedException("Not yet implemented");
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("deprecation")
	public boolean isCallerInRole(java.security.Identity role) {
		throw new NotImplementedException("Not yet implemented");
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isCallerInRole(String roleName) {
		throw new NotImplementedException("Not yet implemented");
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
		throw new NotImplementedException("Not yet implemented");
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("deprecation")
	public java.security.Identity getCallerIdentity() {
		throw new NotImplementedException("Not yet implemented");
	}

}
