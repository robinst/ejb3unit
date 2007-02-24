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
 * $Id: Session.java 503 2006-05-24 13:44:31Z benoitf $
 * --------------------------------------------------------------------------
 */

package com.bm.ejb3metadata.xml.struct;

import java.util.ArrayList;
import java.util.List;

/**
 * Defines a representation for &lt;session&gt; element.
 * 
 * @author Florent Benoit
 */
public class Session extends AbsBean {

	/**
	 * Name of this element.
	 */
	public static final String NAME = "session";

	/**
	 * business-remote interfaces.
	 */
	private List<String> businessRemoteList = null;

	/**
	 * business-local interfaces.
	 */
	private List<String> businessLocalList = null;

	/**
	 * Type of session bean (stateless/stateful).
	 */
	private String sessionType = null;

	/**
	 * Type of transaction.
	 */
	private String transactionType = null;

	/**
	 * Constructor.
	 */
	public Session() {
		super();
		businessRemoteList = new ArrayList<String>();
		businessLocalList = new ArrayList<String>();

	}

	/**
	 * Gets the business-remote interface list.
	 * 
	 * @return business-remote interface list
	 */
	public List<String> getBusinessRemoteList() {
		return businessRemoteList;
	}

	/**
	 * Gets the business-local interface list.
	 * 
	 * @return business-local interface list
	 */
	public List<String> getBusinessLocalList() {
		return businessLocalList;
	}

	/**
	 * Add the business-remote interface.
	 * 
	 * @param businessRemote
	 *            business-remote interface.
	 */
	public void addBusinessRemote(final String businessRemote) {
		businessRemoteList.add(businessRemote);
	}

	/**
	 * Add the business-local interface.
	 * 
	 * @param businessLocal
	 *            business-remote interface.
	 */
	public void addBusinessLocal(final String businessLocal) {
		businessLocalList.add(businessLocal);
	}

	/**
	 * Gets the session-type.
	 * 
	 * @return the session-type.
	 */
	public String getSessionType() {
		return sessionType;
	}

	/**
	 * Set the session-type.
	 * 
	 * @param sessionType
	 *            the type of session.
	 */
	public void setSessionType(final String sessionType) {
		this.sessionType = sessionType;
	}

	/**
	 * Gets the transaction-type.
	 * 
	 * @return the transaction-type
	 */
	public String getTransactionType() {
		return transactionType;
	}

	/**
	 * Set the transaction-type.
	 * 
	 * @param transactionType
	 *            transaction-type.
	 */
	public void setTransactionType(final String transactionType) {
		this.transactionType = transactionType;
	}

}
