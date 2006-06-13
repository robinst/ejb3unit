package com.bm.utils;

import java.security.Identity;
import java.security.Principal;
import java.util.Properties;

import javax.ejb.EJBHome;
import javax.ejb.EJBLocalHome;
import javax.ejb.EJBLocalObject;
import javax.ejb.EJBObject;
import javax.ejb.SessionContext;
import javax.ejb.TimerService;
import javax.transaction.UserTransaction;
import javax.xml.rpc.handler.MessageContext;

/**
 * Represnts a dummy implementation for a session context. Later we can implements more
 * functionality here.
 * @author Daniel Wiese
 *
 */
public class FakedSessionContext implements SessionContext{

	public Object getBusinessObject(Class arg0) throws IllegalStateException {
		return null;
	}

	public EJBLocalObject getEJBLocalObject() throws IllegalStateException {
		return null;
	}

	public EJBObject getEJBObject() throws IllegalStateException {
		return null;
	}

	public Object getInvokedBusinessInterface() throws IllegalStateException {
		return null;
	}

	public MessageContext getMessageContext() throws IllegalStateException {
		return null;
	}

	@SuppressWarnings("deprecation")
	public Identity getCallerIdentity() {
		return null;
	}

	public Principal getCallerPrincipal() {
		return null;
	}

	public EJBHome getEJBHome() {
		return null;
	}

	public EJBLocalHome getEJBLocalHome() {
		return null;
	}

	public Properties getEnvironment() {
		return null;
	}

	public boolean getRollbackOnly() throws IllegalStateException {
		return false;
	}

	public TimerService getTimerService() throws IllegalStateException {
		return null;
	}

	public UserTransaction getUserTransaction() throws IllegalStateException {
		return null;
	}

	@SuppressWarnings("deprecation")
	public boolean isCallerInRole(Identity arg0) {
		return false;
	}

	public boolean isCallerInRole(String arg0) {
		return false;
	}

	public Object lookup(String arg0) {
		return null;
	}

	public void setRollbackOnly() throws IllegalStateException {
		
	}

}
