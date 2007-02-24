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
 * $Id: InterfaceAnnotatedHelper.java 266 2006-03-24 12:58:05Z benoitf $
 * --------------------------------------------------------------------------
 */

package com.bm.ejb3metadata.annotations.helper.bean;

import com.bm.ejb3metadata.annotations.exceptions.ResolverException;
import com.bm.ejb3metadata.annotations.impl.JLocal;
import com.bm.ejb3metadata.annotations.impl.JRemote;
import com.bm.ejb3metadata.annotations.metadata.ClassAnnotationMetadata;
import com.bm.ejb3metadata.annotations.metadata.EjbJarAnnotationMetadata;

/**
 * Lookup the annotated interfaces of a class and report it to the class
 * metadata.
 * 
 * @author Florent Benoit
 */
public final class InterfaceAnnotatedHelper {

	/**
	 * Helper class, no public constructor.
	 */
	private InterfaceAnnotatedHelper() {
	}

	/**
	 * Gets interface of a bean and report their types to the bean metadata.
	 * 
	 * @param sessionBean
	 *            Session bean to analyze
	 * @throws ResolverException
	 *             if there is a failure in a resolver
	 */
	public static void resolve(final ClassAnnotationMetadata sessionBean)
			throws ResolverException {
		// root metadata
		EjbJarAnnotationMetadata ejbJarAnnotationMetadata = sessionBean
				.getEjbJarAnnotationMetadata();

		// Local and remote interfaces of the bean.
		JLocal currentLocalInterfaces = sessionBean.getLocalInterfaces();
		JRemote currentRemoteInterfaces = sessionBean.getRemoteInterfaces();

		// Get all interfaces of the bean
		String[] interfaces = sessionBean.getInterfaces();
		for (String itf : interfaces) {
			ClassAnnotationMetadata itfAnnotationMetadata = ejbJarAnnotationMetadata
					.getClassAnnotationMetadata(itf);

			// Interface was analyzed, try to see the type of the interface
			if (itfAnnotationMetadata != null) {
				// Report type of interface in the bean
				JLocal jLocal = itfAnnotationMetadata.getLocalInterfaces();
				if (jLocal != null) {
					if (currentLocalInterfaces == null) {
						currentLocalInterfaces = new JLocal();
						sessionBean.setLocalInterfaces(currentLocalInterfaces);
					}
					String itfName = itfAnnotationMetadata.getClassName();
					if (!currentLocalInterfaces.getInterfaces().contains(
							itfName)) {
						currentLocalInterfaces.addInterface(itfName);
					}
				}

				// Report type of interface in the bean
				JRemote jRemote = itfAnnotationMetadata.getRemoteInterfaces();
				if (jRemote != null) {
					if (currentRemoteInterfaces == null) {
						currentRemoteInterfaces = new JRemote();
						sessionBean
								.setRemoteInterfaces(currentRemoteInterfaces);
					}
					String itfName = itfAnnotationMetadata.getClassName();
					if (!currentRemoteInterfaces.getInterfaces().contains(
							itfName)) {
						currentRemoteInterfaces.addInterface(itfName);
					}
				}
			}
		}
	}

}
