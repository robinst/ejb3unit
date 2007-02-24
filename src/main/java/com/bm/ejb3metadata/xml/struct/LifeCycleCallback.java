/**
 * EasyBeans
 * Copyright (C) 2006 Bull S.A.S.
 * Contact: easybeans@objectweb.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 * --------------------------------------------------------------------------
 * $Id: LifeCycleCallback.java 503 2006-05-24 13:44:31Z benoitf $
 * --------------------------------------------------------------------------
 */

package com.bm.ejb3metadata.xml.struct;

/**
 * This class defines a life cycle callback that is described in an XML DD. It
 * is used for post-construct, pre-destroy, pre-passivate, etc.
 * 
 * @author Florent Benoit
 */
public class LifeCycleCallback {

	/**
	 * Post construct.
	 */
	public static final String POST_CONSTRUCT = "post-construct";

	/**
	 * Pre Destroy.
	 */
	public static final String PRE_DESTROY = "pre-destroy";

	/**
	 * Class of this callback.
	 */
	private String className = null;

	/**
	 * Method of this callback.
	 */
	private String method = null;

	/**
	 * Gets the class of this callback.
	 * 
	 * @return the class name.
	 */
	public String getClassName() {
		return className;
	}

	/**
	 * Sets the class of the callback.
	 * 
	 * @param className
	 *            the name of the class.
	 */
	public void setClassName(final String className) {
		this.className = className;
	}

	/**
	 * Gets the method of the class for the callback.
	 * 
	 * @return the method name.
	 */
	public String getMethod() {
		return method;
	}

	/**
	 * Sets the method name of the callback's class.
	 * 
	 * @param method
	 *            the method name.
	 */
	public void setMethod(final String method) {
		this.method = method;
	}

}
