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
 * $Id: EnterpriseBeans.java 122 2006-03-05 19:54:41Z benoitf $
 * --------------------------------------------------------------------------
 */

package com.bm.ejb3metadata.xml.struct;

import java.util.ArrayList;
import java.util.List;

/**
 * This class defines the implementation of the element enterprise-beans.
 * 
 * @author Florent Benoit
 */
public class EnterpriseBeans {

	/**
	 * Name of this element.
	 */
	public static final String NAME = "enterprise-beans";

	/**
	 * List of &lt;session&gt; elements.
	 */
	private List<Session> sessionList = null;

	/**
	 * Constructor.
	 */
	public EnterpriseBeans() {
		sessionList = new ArrayList<Session>();
	}

	/**
	 * Gets the list of &lt;session&gt; elements.
	 * 
	 * @return list of &lt;session&gt; elements.
	 */
	public List<Session> getSessionList() {
		return sessionList;
	}

	/**
	 * Adds a new &lt;session&gt; element to enterprise beans.
	 * 
	 * @param session
	 *            the &lt;session&gt; element
	 */
	public void addSession(final Session session) {
		sessionList.add(session);
	}

}
