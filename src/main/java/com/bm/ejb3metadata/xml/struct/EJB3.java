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
 * $Id: EJB3.java 122 2006-03-05 19:54:41Z benoitf $
 * --------------------------------------------------------------------------
 */

package com.bm.ejb3metadata.xml.struct;

/**
 * This class represents an EJB-Jar deployment descriptor.
 * 
 * @author Florent Benoit
 */
public class EJB3 {

	/**
	 * Enterprise-beans element.
	 */
	private EnterpriseBeans enterpriseBeans = null;

	/**
	 * Gets the enterprise-beans element.
	 * 
	 * @return the enterprise-beans
	 */
	public EnterpriseBeans getEnterpriseBeans() {
		return enterpriseBeans;
	}

	/**
	 * Sets the enterprise-beans element.
	 * 
	 * @param enterpriseBeans
	 *            the element to set.
	 */
	public void setEnterpriseBeans(final EnterpriseBeans enterpriseBeans) {
		this.enterpriseBeans = enterpriseBeans;
	}

}
