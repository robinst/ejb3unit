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
 * $Id: AbsBean.java 127 2006-03-06 17:51:35Z benoitf $
 * --------------------------------------------------------------------------
 */

package com.bm.ejb3metadata.xml.struct;

/**
 * Defines common rules used by Bean (Session, MDB).
 * 
 * @author Florent Benoit
 * 
 */
public class AbsBean {

	/**
	 * ejb-name.
	 */
	private String ejbName = null;

	/**
	 * ejb-class.
	 */
	private String ejbClass = null;

	/**
	 * Constructor.
	 */
	public AbsBean() {
		super();
	}

	/**
	 * Gets the ejb-name.
	 * 
	 * @return the ejb-name
	 */
	public String getEjbName() {
		return ejbName;
	}

	/**
	 * Set the ejb-name.
	 * 
	 * @param ejbName
	 *            ejbName
	 */
	public void setEjbName(final String ejbName) {
		this.ejbName = ejbName;
	}

	/**
	 * Gets the ejb-class.
	 * 
	 * @return the ejb-class
	 */
	public String getEjbClass() {
		return ejbClass;
	}

	/**
	 * Set the ejb-class.
	 * 
	 * @param ejbClass
	 *            ejb-class
	 */
	public void setEjbClass(final String ejbClass) {
		this.ejbClass = ejbClass;
	}

}
