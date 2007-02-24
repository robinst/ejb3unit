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
 * $Id: SessionBeanValidator.java 9 2006-02-19 18:53:32Z benoitf $
 * --------------------------------------------------------------------------
 */

package com.bm.ejb3metadata.annotations.helper.bean.session.checks;

import com.bm.ejb3metadata.annotations.metadata.ClassAnnotationMetadata;

/**
 * This class ensures that the session bean is well formed before trying to load
 * it.
 * 
 * @author Florent Benoit
 */
public final class SessionBeanValidator {

	/**
	 * Helper class, no public constructor.
	 */
	private SessionBeanValidator() {
	}

	/**
	 * Validate a session bean.
	 * 
	 * @param sessionBean
	 *            Session bean to validate.
	 */
	public static void validate(final ClassAnnotationMetadata sessionBean) {

		// TODO : Complete the list
		/**
		 * Session Bean Class The following are the requirements for the session
		 * bean class:
		 * <ul>
		 * <li>The class must be defined as public, must not be final, and must
		 * not be abstract. The class must be a top level class.</li>
		 * <li>The class must have a public constructor that takes no
		 * parameters. The container uses this constructor to create instances
		 * of the session bean class.</li>
		 * <li>The class must not define the finalize() method.</li>
		 * <li>The class must implement the bean?s business interface(s) or the
		 * methods of the bean?s business interface(s), if any.</li>
		 * </ul>
		 * 
		 * @See 4.6.2 chapter of EJB 3.0
		 */

		InterceptorsValidator.validate(sessionBean);
	}

}
