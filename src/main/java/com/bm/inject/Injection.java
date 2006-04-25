package com.bm.inject;

/**
 * A Dependency Injection factor creates Injections. A injection
 * contaions the object which will be injected and a handle which contains
 * additional information.
 * @author Daniel Wiese
 * @param <H> - the type of the handle object e.g. MOck object
 *
 */
public interface Injection<H> {

	/**
	 * Returns the created object.
	 * @return - the created object
	 */
	Object getCratedObject();
	
	/**
	 * Returns a handle for the previosly crated object. A handle can be any object of
	 * type H. One use case if for example a Mock controll object.
	 * @return - the handle of previosly crated object
	 */
	H getHandle();
	
}
