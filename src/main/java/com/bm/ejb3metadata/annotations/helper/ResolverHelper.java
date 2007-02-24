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
 * $Id: ResolverHelper.java 854 2006-07-12 12:57:45Z benoitf $
 * --------------------------------------------------------------------------
 */

package com.bm.ejb3metadata.annotations.helper;

import com.bm.ejb3metadata.annotations.exceptions.ResolverException;
import com.bm.ejb3metadata.annotations.helper.bean.BusinessMethodResolver;
import com.bm.ejb3metadata.annotations.helper.bean.InheritanceInterfacesHelper;
import com.bm.ejb3metadata.annotations.helper.bean.InheritanceMethodResolver;
import com.bm.ejb3metadata.annotations.helper.bean.InterfaceAnnotatedHelper;
import com.bm.ejb3metadata.annotations.helper.bean.SessionBeanHelper;
import com.bm.ejb3metadata.annotations.helper.bean.mdb.MDBListenerBusinessMethodResolver;
import com.bm.ejb3metadata.annotations.metadata.ClassAnnotationMetadata;
import com.bm.ejb3metadata.annotations.metadata.EjbJarAnnotationMetadata;

/**
 * This class handle some steps that need to be done after the meta-data
 * generation.
 * 
 * @author Florent Benoit
 */
public final class ResolverHelper {

	/**
	 * Helper class, no public constructor.
	 */
	private ResolverHelper() {

	}

	/**
	 * The helper will analyze datas of a given EjbJarAnnotationMetadata object.
	 * 
	 * @param ejbJarAnnotationMetadata
	 *            object to analyze
	 * @throws ResolverException
	 *             if one of resolver fails
	 */
	public static void resolve(
			final EjbJarAnnotationMetadata ejbJarAnnotationMetadata)
			throws ResolverException {

		// Found each bean class
		for (ClassAnnotationMetadata classMetaData : ejbJarAnnotationMetadata
				.getClassAnnotationMetadataCollection()) {

			if (classMetaData.isBean()) {
				// Inheritance on interfaces
				InheritanceInterfacesHelper.resolve(classMetaData);
				InterfaceAnnotatedHelper.resolve(classMetaData);
				InheritanceMethodResolver.resolve(classMetaData);
				// Find business method
				if (classMetaData.isSession()) {
					BusinessMethodResolver.resolve(classMetaData);
				} else if (classMetaData.isMdb()) {
					MDBListenerBusinessMethodResolver.resolve(classMetaData);
				}

				// Transaction
				// TransactionResolver.resolve(classMetaData);

				// Interceptors
				// InterceptorsClassResolver.resolve(classMetaData);

			}

			// for each bean, call sub helper
			if (classMetaData.isSession()) {
				SessionBeanHelper.resolve(classMetaData);
			}
		}
	}
}
