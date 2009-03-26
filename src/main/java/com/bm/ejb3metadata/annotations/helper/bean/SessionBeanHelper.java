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
 * $Id: SessionBeanHelper.java 843 2006-07-12 09:05:58Z benoitf $
 * --------------------------------------------------------------------------
 */

package com.bm.ejb3metadata.annotations.helper.bean;

import com.bm.ejb3metadata.annotations.exceptions.ResolverException;
import com.bm.ejb3metadata.annotations.helper.bean.session.SessionBeanInterface;
import com.bm.ejb3metadata.annotations.helper.bean.session.SessionBusinessInterfaceFinder;
import com.bm.ejb3metadata.annotations.helper.bean.session.checks.SessionBeanValidator;
import com.bm.ejb3metadata.annotations.metadata.ClassAnnotationMetadata;

/**
 * Helper class which manages only Session bean class.
 * 
 * @author Florent Benoit
 */
public final class SessionBeanHelper {

	/**
	 * Validation.
	 */
	private static boolean validating = false;

	/**
	 * Helper class, no public constructor.
	 */
	private SessionBeanHelper() {
	}

	/**
	 * Apply all helper.
	 * 
	 * @param sessionBean
	 *            Session bean to analyze
	 * @throws ResolverException
	 *             if there is a failure in a resolver
	 */
	public static void resolve(final ClassAnnotationMetadata sessionBean)
			throws ResolverException {
		// call helpers

		// Search session bean that implements javax.ejb.SessionBean and add
		// metadata on it
		SessionBeanInterface.resolve(sessionBean);

		// Find annotated interfaces
		SessionBusinessInterfaceFinder.resolve(sessionBean);

		if (validating) {
			SessionBeanValidator.validate(sessionBean);
		}
	}
}
