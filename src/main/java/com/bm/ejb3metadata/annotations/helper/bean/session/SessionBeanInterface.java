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
 * $Id: SessionBeanInterface.java 847 2006-07-12 09:51:27Z benoitf $
 * --------------------------------------------------------------------------
 */

package com.bm.ejb3metadata.annotations.helper.bean.session;

import static com.bm.ejb3metadata.annotations.helper.bean.InheritanceInterfacesHelper.JAVA_LANG_OBJECT;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.repackage.cglib.asm.Opcodes;

import com.bm.ejb3metadata.annotations.JMethod;
import com.bm.ejb3metadata.annotations.impl.JAnnotationResource;
import com.bm.ejb3metadata.annotations.metadata.ClassAnnotationMetadata;
import com.bm.ejb3metadata.annotations.metadata.MethodAnnotationMetadata;

/**
 * This class analyze interfaces of the session bean. If the session bean
 * implements javax.ejb.SessionBean interface, add lifecycle callbacks and add
 * resource injection for setSessionContext method.
 * 
 * @author Florent Benoit
 */
public final class SessionBeanInterface {

	/**
	 * SessionBean interface.
	 */
	private static final String SESSION_BEAN_INTERFACE = "javax/ejb/SessionBean";

	/**
	 * setSessionContext() method.
	 */
	private static final JMethod SETSESSIONCONTEXT_METHOD = new JMethod(Opcodes.ACC_PUBLIC,
			"setSessionContext", "(Ljavax/ejb/SessionContext;)V", null, new String[] {
					"javax/ejb/EJBException", "java/rmi/RemoteException" });

	/**
	 * ejbRemove() method.
	 */
	private static final JMethod EJBREMOVE_METHOD = new JMethod(Opcodes.ACC_PUBLIC, "ejbRemove",
			"()V", null, new String[] { "javax/ejb/EJBException", "java/rmi/RemoteException" });

	/**
	 * ejbActivate() method.
	 */
	private static final JMethod EJBACTIVATE_METHOD = new JMethod(Opcodes.ACC_PUBLIC, "ejbActivate", "()V",
			null, new String[] { "javax/ejb/EJBException", "java/rmi/RemoteException" });

	/**
	 * ejbPassivate() method.
	 */
	private static final JMethod EJBPASSIVATE_METHOD = new JMethod(Opcodes.ACC_PUBLIC, "ejbPassivate",
			"()V", null, new String[] { "javax/ejb/EJBException", "java/rmi/RemoteException" });

	/**
	 * Helper class, no public constructor.
	 */
	private SessionBeanInterface() {
	}

	/**
	 * Try to see if bean implements javax.ejb.SessionBean interface.
	 * 
	 * @param sessionBean
	 *            Session bean to analyze
	 */
	public static void resolve(final ClassAnnotationMetadata sessionBean) {
		// Make a list of interfaces
		List<String> allInterfaces = getAllInterfacesFromClass(sessionBean);

		// if SESSION_BEAN_INTERFACE is contained in the list, add some metadata
		if (allInterfaces.contains(SESSION_BEAN_INTERFACE)) {
			// first add dependency injection for setSessionContext method.
			JAnnotationResource jAnnotationResource = new JAnnotationResource();

			// add resource on setSessionContext method
			MethodAnnotationMetadata setCtxMethod = getMethod(sessionBean,
					SETSESSIONCONTEXT_METHOD, false);
			setCtxMethod.setJAnnotationResource(jAnnotationResource);

			// ejbRemove() method
			MethodAnnotationMetadata ejbRemoveMethod = getMethod(sessionBean, EJBREMOVE_METHOD,
					true);
			ejbRemoveMethod.setPreDestroy(true);
			if (!sessionBean.getPreDestroyMethodsMetadata().contains(ejbRemoveMethod)) {
				sessionBean.addPreDestroyMethodMetadata(ejbRemoveMethod);
			}

			// ejbActivate() method
			MethodAnnotationMetadata ejbActivateMethod = getMethod(sessionBean, EJBACTIVATE_METHOD,
					true);
			ejbRemoveMethod.setPostActivate(true);
			if (!sessionBean.getPostActivateMethodsMetadata().contains(ejbActivateMethod)) {
				sessionBean.addPostActivateMethodMetadata(ejbActivateMethod);
			}

			// ejbPassivate() method
			MethodAnnotationMetadata ejbPassivateMethod = getMethod(sessionBean,
					EJBPASSIVATE_METHOD, true);
			ejbRemoveMethod.setPrePassivate(true);
			if (!sessionBean.getPrePassivateMethodsMetadata().contains(ejbPassivateMethod)) {
				sessionBean.addPrePassivateMethodMetadata(ejbPassivateMethod);
			}

		}

	}

	/**
	 * Gets method metadata on the given class metadata for the given method.
	 * 
	 * @param sessionBean
	 *            the class metadata on which retrieve the method
	 * @param jMethod
	 *            the method to get
	 * @param inherited
	 *            get the correct method in super class, not inherited
	 * @return the method metadata, else exception
	 */
	private static MethodAnnotationMetadata getMethod(final ClassAnnotationMetadata sessionBean,
			final JMethod jMethod,
			final boolean inherited) {
		MethodAnnotationMetadata method = sessionBean.getMethodAnnotationMetadata(jMethod);
		if (method == null) {
			throw new IllegalStateException("Bean '" + sessionBean + "' implements "
					+ SESSION_BEAN_INTERFACE + " but no " + jMethod + " method found in metadata");
		}
		// gets the correct method on the correct level. (not the inherited
		// method) if we don't want the inherited method.
		if (method.isInherited() && !inherited) {
			String superClassName = sessionBean.getSuperName();
			// loop while class is not java.lang.Object
			while (!JAVA_LANG_OBJECT.equals(superClassName)) {
				ClassAnnotationMetadata superMetaData = sessionBean.getEjbJarAnnotationMetadata()
						.getClassAnnotationMetadata(superClassName);
				// If the method is found in the super class and is not
				// inherited, use this one
				if (superMetaData != null) {
					MethodAnnotationMetadata superMethod = superMetaData
							.getMethodAnnotationMetadata(jMethod);
					if (superMethod != null && !superMethod.isInherited()) {
						return superMethod;
					}
					superClassName = superMetaData.getSuperName();
				} else {
					// break the loop
					superClassName = JAVA_LANG_OBJECT;
				}
			}

		}

		return method;
	}

	/**
	 * Gets all interfaces used by a class.
	 * 
	 * @param sessionBean
	 *            the metadata to analyze.
	 * @return the list of interfaces from a given class.
	 */
	public static List<String> getAllInterfacesFromClass(final ClassAnnotationMetadata sessionBean) {
		// build list
		List<String> allInterfaces = new ArrayList<String>();

		// Class to analyze
		String className = sessionBean.getClassName();

		// loop while class is not java.lang.Object
		while (!JAVA_LANG_OBJECT.equals(className)) {
			ClassAnnotationMetadata metaData = sessionBean.getEjbJarAnnotationMetadata()
					.getClassAnnotationMetadata(className);
			// find metadata, all interfaces found
			if (metaData != null) {
				String[] interfaces = metaData.getInterfaces();
				if (interfaces != null) {
					for (String itf : interfaces) {
						allInterfaces.add(itf);
					}
				}
				className = metaData.getSuperName();
			} else {
				// break the loop
				className = JAVA_LANG_OBJECT;
			}
		}
		return allInterfaces;
	}
}
