package com.bm.jndi;

import java.util.HashMap;
import java.util.Map;

import javax.naming.Context;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NamingException;

/**
 * An in memory naming context.
 * 
 */
public class MemoryContext extends AbstractContext implements Context {

	private final Map<String, Object> bindings = new HashMap<String, Object>();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void bind(String name, Object obj) throws NamingException {
		bindings.put(name, obj);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object lookup(String name) throws NamingException {
		if (this.bindings.containsKey(name)) {
			return this.bindings.get(name);
		}
		throw new NamingException("Can't find the name (" + name
				+ ") in the JNDI tree Current bindings>(" + bindings + ")");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void rebind(String name, Object obj) throws NamingException {
		bindings.put(name, obj);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void rename(String oldName, String newName) throws NamingException {
		if (this.bindings.containsKey(newName)) {
			throw new NameAlreadyBoundException("The name (" + newName
					+ ") is already bound");
		}
		if (this.bindings.containsKey(oldName)) {
			final Object tmp = this.bindings.remove(oldName);
			this.bindings.put(newName, tmp);
		} else {

			throw new NamingException("Can't find the name (" + oldName
					+ ") in the JNDI tree");
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void unbind(String name) throws NamingException {
		this.bindings.remove(name);
	}

}
