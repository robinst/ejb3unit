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
 * $Id: SessionBusinessInterfaceFinder.java 873 2006-07-16 18:26:10Z benoitf $
 * --------------------------------------------------------------------------
 */

package com.bm.ejb3metadata.annotations.helper.bean.session;

import com.bm.ejb3metadata.annotations.impl.JLocal;
import com.bm.ejb3metadata.annotations.impl.JRemote;
import com.bm.ejb3metadata.annotations.metadata.ClassAnnotationMetadata;

/**
 * This class finds the business interface if there are no business interfaces
 * specified.
 * 
 * @author Florent Benoit
 */
public final class SessionBusinessInterfaceFinder {

	/**
	 * Helper class, no public constructor.
	 */
	private SessionBusinessInterfaceFinder() {
	}

	/**
	 * Finds business interface in a session bean.
	 * 
	 * @see <a href="http://www.jcp.org/en/jsr/detail?id=220">EJB 3.0 Spec
	 *      ?4.6.6</a>
	 * @param sessionBean
	 *            Session bean to analyze
	 */
	public static void resolve(final ClassAnnotationMetadata sessionBean) {

		JLocal jLocal = sessionBean.getLocalInterfaces();
		JRemote jRemote = sessionBean.getRemoteInterfaces();

		// No business interface or empty annotation (@Remote or @Local)
		if ((jLocal == null && jRemote == null)
				|| (jLocal == null && jRemote != null && jRemote
						.getInterfaces().isEmpty())) {

			// The following interfaces are excluded when determining whether
			// the bean class has
			// more than one interface: java.io.Serializable;
			// java.io.Externalizable;
			// any of the interfaces defined by the javax.ejb package.
			String[] interfaces = sessionBean.getInterfaces();
			int numberItfFound = 0;
			String itfFound = null;
			for (String itf : interfaces) {
				if (!itf.equals(java.io.Serializable.class.getName().replace(
						".", "/"))
						&& !itf.equals(java.io.Externalizable.class.getName()
								.replace(".", "/"))
						&& !itf.startsWith("javax/ejb")) {
					itfFound = itf;
					numberItfFound++;
				}
			}

			if (numberItfFound == 0) {
				throw new IllegalStateException(
						"No business interface found on class '"
								+ sessionBean.getClassName() + "'.");
			}

			if (numberItfFound > 1) {
				throw new IllegalStateException("More than 1 itf on class '"
						+ sessionBean.getClassName() + "'.");
			}

			// If bean class implements a single interface, that interface is
			// assumed to be the business
			// interface of the bean. This business interface will be a local
			// interface unless the
			// interface is designated as a remote business interface by use of
			// the Remote annotation
			// on the bean class or interface or by means of the deployment
			// descriptor.

			// Build a local interface if no @Remote annotation, else add
			// interface in the existing object
			if (jRemote == null) {
				JLocal addedJLocal = new JLocal();
				addedJLocal.addInterface(itfFound);
				sessionBean.setLocalInterfaces(addedJLocal);
			} else {
				jRemote.addInterface(itfFound);
				sessionBean.setRemoteInterfaces(jRemote);
			}

		}
	}
}
