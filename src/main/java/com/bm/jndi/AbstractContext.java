package com.bm.jndi;

import java.util.Hashtable;

import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NameClassPair;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

/**
 * Implements all Interface methods with unimplemented exception.
 * @author Daniel
 *
 */
public class AbstractContext implements Context {

	/**
	 * {@inheritDoc}
	 */
	public Object addToEnvironment(String arg0, Object arg1) throws NamingException {
		throw new IllegalArgumentException("This JNDI operation is not implemented by the JNDI provider");
	}

	/**
	 * {@inheritDoc}
	 */
	public void bind(Name arg0, Object arg1) throws NamingException {
		throw new IllegalArgumentException("This JNDI operation is not implemented by the JNDI provider");
	}

	/**
	 * {@inheritDoc}
	 */
	public void bind(String arg0, Object arg1) throws NamingException {
		throw new IllegalArgumentException("This JNDI operation is not implemented by the JNDI provider");
	}

	/**
	 * {@inheritDoc}
	 */
	public void close() throws NamingException {
		throw new IllegalArgumentException("This JNDI operation is not implemented by the JNDI provider");
	}

	/**
	 * {@inheritDoc}
	 */
	public Name composeName(Name arg0, Name arg1) throws NamingException {
		throw new IllegalArgumentException("This JNDI operation is not implemented by the JNDI provider");
	}

	/**
	 * {@inheritDoc}
	 */
	public String composeName(String arg0, String arg1) throws NamingException {
		throw new IllegalArgumentException("This JNDI operation is not implemented by the JNDI provider");
	}

	/**
	 * {@inheritDoc}
	 */
	public Context createSubcontext(Name arg0) throws NamingException {
		throw new IllegalArgumentException("This JNDI operation is not implemented by the JNDI provider");
	}

	/**
	 * {@inheritDoc}
	 */
	public Context createSubcontext(String arg0) throws NamingException {
		throw new IllegalArgumentException("This JNDI operation is not implemented by the JNDI provider");
	}

	/**
	 * {@inheritDoc}
	 */
	public void destroySubcontext(Name arg0) throws NamingException {
		throw new IllegalArgumentException("This JNDI operation is not implemented by the JNDI provider");
	}

	/**
	 * {@inheritDoc}
	 */
	public void destroySubcontext(String arg0) throws NamingException {
		throw new IllegalArgumentException("This JNDI operation is not implemented by the JNDI provider");
	}

	/**
	 * {@inheritDoc}
	 */
	public Hashtable<?, ?> getEnvironment() throws NamingException {
		throw new IllegalArgumentException("This JNDI operation is not implemented by the JNDI provider");
	}

	/**
	 * {@inheritDoc}
	 */
	public String getNameInNamespace() throws NamingException {
		throw new IllegalArgumentException("This JNDI operation is not implemented by the JNDI provider");
	}

	/**
	 * {@inheritDoc}
	 */
	public NameParser getNameParser(Name arg0) throws NamingException {
		throw new IllegalArgumentException("This JNDI operation is not implemented by the JNDI provider");
	}

	/**
	 * {@inheritDoc}
	 */
	public NameParser getNameParser(String arg0) throws NamingException {
		throw new IllegalArgumentException("This JNDI operation is not implemented by the JNDI provider");
	}

	/**
	 * {@inheritDoc}
	 */
	public NamingEnumeration<NameClassPair> list(Name arg0) throws NamingException {
		throw new IllegalArgumentException("This JNDI operation is not implemented by the JNDI provider");
	}

	/**
	 * {@inheritDoc}
	 */
	public NamingEnumeration<NameClassPair> list(String arg0) throws NamingException {
		throw new IllegalArgumentException("This JNDI operation is not implemented by the JNDI provider");
	}

	/**
	 * {@inheritDoc}
	 */
	public NamingEnumeration<Binding> listBindings(Name arg0) throws NamingException {
		throw new IllegalArgumentException("This JNDI operation is not implemented by the JNDI provider");
	}

	/**
	 * {@inheritDoc}
	 */
	public NamingEnumeration<Binding> listBindings(String arg0) throws NamingException {
		throw new IllegalArgumentException("This JNDI operation is not implemented by the JNDI provider");
	}

	/**
	 * {@inheritDoc}
	 */
	public Object lookup(Name arg0) throws NamingException {
		throw new IllegalArgumentException("This JNDI operation is not implemented by the JNDI provider");
	}

	/**
	 * {@inheritDoc}
	 */
	public Object lookup(String arg0) throws NamingException {
		throw new IllegalArgumentException("This JNDI operation is not implemented by the JNDI provider");
	}

	/**
	 * {@inheritDoc}
	 */
	public Object lookupLink(Name arg0) throws NamingException {
		throw new IllegalArgumentException("This JNDI operation is not implemented by the JNDI provider");
	}

	/**
	 * {@inheritDoc}
	 */
	public Object lookupLink(String arg0) throws NamingException {
		throw new IllegalArgumentException("This JNDI operation is not implemented by the JNDI provider");
	}

	/**
	 * {@inheritDoc}
	 */
	public void rebind(Name arg0, Object arg1) throws NamingException {
		throw new IllegalArgumentException("This JNDI operation is not implemented by the JNDI provider");
	}

	/**
	 * {@inheritDoc}
	 */
	public void rebind(String arg0, Object arg1) throws NamingException {
		throw new IllegalArgumentException("This JNDI operation is not implemented by the JNDI provider");
	}

	/**
	 * {@inheritDoc}
	 */
	public Object removeFromEnvironment(String arg0) throws NamingException {
		throw new IllegalArgumentException("This JNDI operation is not implemented by the JNDI provider");
	}

	/**
	 * {@inheritDoc}
	 */
	public void rename(Name arg0, Name arg1) throws NamingException {
		throw new IllegalArgumentException("This JNDI operation is not implemented by the JNDI provider");
	}

	/**
	 * {@inheritDoc}
	 */
	public void rename(String arg0, String arg1) throws NamingException {
		throw new IllegalArgumentException("This JNDI operation is not implemented by the JNDI provider");
	}

	/**
	 * {@inheritDoc}
	 */
	public void unbind(Name arg0) throws NamingException {
		throw new IllegalArgumentException("This JNDI operation is not implemented by the JNDI provider");
	}

	/**
	 * {@inheritDoc}
	 */
	public void unbind(String arg0) throws NamingException {
		throw new IllegalArgumentException("This JNDI operation is not implemented by the JNDI provider");
	}

}
