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
 * $Id: InheritanceInterfacesHelper.java 268 2006-03-24 15:09:48Z benoitf $
 * --------------------------------------------------------------------------
 */

package com.bm.ejb3metadata.annotations.helper.bean;

import java.util.ArrayList;
import java.util.List;

import com.bm.ejb3metadata.annotations.exceptions.ResolverException;
import com.bm.ejb3metadata.annotations.impl.JLocal;
import com.bm.ejb3metadata.annotations.impl.JRemote;
import com.bm.ejb3metadata.annotations.metadata.ClassAnnotationMetadata;
import com.bm.ejb3metadata.annotations.metadata.EjbJarAnnotationMetadata;

/**
 * Analyze classes and if there are super classes, set all super interfaces to
 * the current class.
 * 
 * @author Florent Benoit
 */
public final class InheritanceInterfacesHelper {

	/**
	 * Defines java.lang.Object class.
	 */
	public static final String JAVA_LANG_OBJECT = "java/lang/Object";

	/**
	 * Helper class, no public constructor.
	 */
	private InheritanceInterfacesHelper() {

	}

	/**
	 * Found all method meta data of the super class and adds them to the bean's
	 * class. Delegates to loop method.
	 * 
	 * @param classAnnotationMetadata
	 *            bean' class to analyze.
	 * @throws ResolverException
	 *             if the super class in not in the given ejb-jar
	 */
	public static void resolve(
			final ClassAnnotationMetadata classAnnotationMetadata)
			throws ResolverException {
		loop(classAnnotationMetadata, classAnnotationMetadata);

	}

	/**
	 * Found all method meta data of the super class and adds them to the bean's
	 * class.
	 * 
	 * @param beanClassAnnotationMetadata
	 *            class where to report interfaces
	 * @param visitingClassAnnotationMetadata
	 *            class to analyze
	 * @throws ResolverException
	 *             if the super class in not in the given ejb-jar
	 */
	public static void loop(
			final ClassAnnotationMetadata beanClassAnnotationMetadata,
			final ClassAnnotationMetadata visitingClassAnnotationMetadata)
			throws ResolverException {
		String superClass = visitingClassAnnotationMetadata.getSuperName();
		if (superClass != null) {
			// try to see if it was analyzed (and not java.lang.Object)
			if (!superClass.equals(JAVA_LANG_OBJECT)) {
				EjbJarAnnotationMetadata ejbJarAnnotationMetadata = beanClassAnnotationMetadata
						.getEjbJarAnnotationMetadata();
				ClassAnnotationMetadata superMetadata = ejbJarAnnotationMetadata
						.getClassAnnotationMetadata(superClass);

				if (superMetadata == null) {
					throw new IllegalStateException("No super class named '"
							+ superClass
							+ "' was analyzed. But it is referenced from '"
							+ visitingClassAnnotationMetadata.getClassName()
							+ "'.");
				}

				// Add in a new list the existing interfaces
				List<String> newInterfacesLst = new ArrayList<String>();
				String[] currentInterfaces = beanClassAnnotationMetadata
						.getInterfaces();
				if (currentInterfaces != null) {
					for (String itf : currentInterfaces) {
						newInterfacesLst.add(itf);
					}
				}

				// Add the interfaces of the super class only if there aren't
				// yet present
				String[] superInterfaces = superMetadata.getInterfaces();
				if (superInterfaces != null) {
					for (String itf : superInterfaces) {
						if (!newInterfacesLst.contains(itf)) {
							newInterfacesLst.add(itf);
						}
					}
				}

				// Set the updated list.
				beanClassAnnotationMetadata.setInterfaces(newInterfacesLst
						.toArray(new String[newInterfacesLst.size()]));

				// The local and remote interfaces need to be reported from the
				// superclass to the current class.
				// Start with the local interfaces.
				JLocal currentLocalInterfaces = beanClassAnnotationMetadata
						.getLocalInterfaces();
				JLocal superLocalInterfaces = superMetadata
						.getLocalInterfaces();
				if (superLocalInterfaces != null) {
					if (currentLocalInterfaces == null) {
						currentLocalInterfaces = new JLocal();
						beanClassAnnotationMetadata
								.setLocalInterfaces(currentLocalInterfaces);
					}
					for (String itf : superLocalInterfaces.getInterfaces()) {
						if (!currentLocalInterfaces.getInterfaces().contains(
								itf)) {
							currentLocalInterfaces.addInterface(itf);
						}
					}
				}

				// And then, with the remote interfaces
				JRemote currentRemoteInterfaces = beanClassAnnotationMetadata
						.getRemoteInterfaces();
				JRemote superRemoteInterfaces = superMetadata
						.getRemoteInterfaces();
				if (superRemoteInterfaces != null) {
					if (currentRemoteInterfaces == null) {
						currentRemoteInterfaces = new JRemote();
						beanClassAnnotationMetadata
								.setRemoteInterfaces(currentRemoteInterfaces);
					}
					for (String itf : superRemoteInterfaces.getInterfaces()) {
						if (!currentRemoteInterfaces.getInterfaces().contains(
								itf)) {
							currentRemoteInterfaces.addInterface(itf);
						}
					}
				}

				// Loop again until java.lang.Object super class is found
				if (!superMetadata.getClassName().equals(JAVA_LANG_OBJECT)) {
					loop(beanClassAnnotationMetadata, superMetadata);
				}
			}
		}
	}
}
