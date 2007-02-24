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
 * $Id: BusinessMethodResolver.java 451 2006-05-12 16:39:43Z benoitf $
 * --------------------------------------------------------------------------
 */

package com.bm.ejb3metadata.annotations.helper.bean;

import com.bm.ejb3metadata.annotations.JMethod;
import com.bm.ejb3metadata.annotations.metadata.ClassAnnotationMetadata;
import com.bm.ejb3metadata.annotations.metadata.MethodAnnotationMetadata;

/**
 * This class resolves the business method for bean class by looking at the
 * interfaces.
 * @author Florent Benoit
 */
public final class BusinessMethodResolver {
	
	private final static org.apache.log4j.Logger logger = org.apache.log4j.Logger
			.getLogger(BusinessMethodResolver.class);

    /**
     * Name of the method used in constructor.
     * This has to be ignored as this is never a business interface
     */
    private static final String CLASS_INIT = "<clinit>";

    /**
     * Ignore constructor "method" of interfaces.
     * Not a business interface.
     */
    private static final String CONST_INIT = "<init>";

    /**
     * Helper class, no public constructor.
     */
    private BusinessMethodResolver() {

    }

    /**
     * Found all business methods of a bean.<br>
     * A business method is a method from one of the local or remote interfaces.
     * @param classAnnotationMetadata class to analyze
     */
    public static void resolve(final ClassAnnotationMetadata classAnnotationMetadata) {
        loop(classAnnotationMetadata, classAnnotationMetadata);
    }

    /**
     * While there are super interfaces, loop on them.
     * @param beanclassAnnotationMetadata the class that is analyzed
     * @param visitingclassAnnotationMetadata classes from where we get
     *        interfaces
     */
    private static void loop(final ClassAnnotationMetadata beanclassAnnotationMetadata,
            final ClassAnnotationMetadata visitingclassAnnotationMetadata) {
        // first, need to analyze all methods of interfaces used by this class
        // then, set them as business method

        for (String itf : visitingclassAnnotationMetadata.getInterfaces()) {
            if (itf.startsWith("javax/ejb/") || itf.startsWith("java/io/Serializable")
                    || itf.startsWith("java/io/Externalizable")) {
                continue;
            }

            // get meta data of the interface
            ClassAnnotationMetadata itfMetadata = visitingclassAnnotationMetadata.getEjbJarAnnotationMetadata()
                    .getClassAnnotationMetadata(itf);

            if (itfMetadata == null) {
                logger.warn("No class was found for interface (" +itf+")");
                continue;
            }

            // for each method of the interface, set the business method to true
            // in bean
            for (MethodAnnotationMetadata methodData : itfMetadata.getMethodAnnotationMetadataCollection()) {
                JMethod itfMethod = methodData.getJMethod();

                // Ignore class init method
                if (itfMethod.getName().equals(CLASS_INIT) || itfMethod.getName().equals(CONST_INIT)) {
                    continue;
                }

                // take the method from the bean class
                MethodAnnotationMetadata beanMethod = beanclassAnnotationMetadata.getMethodAnnotationMetadata(itfMethod);
                if (beanMethod == null) {
                    // TODO: I18n
                    throw new IllegalStateException("No method was found for method " + itfMethod + " in class "
                            + beanclassAnnotationMetadata.getClassName());
                }
                beanMethod.setBusinessMethod(true);
            }

            // loop again
            if (itfMetadata.getInterfaces() != null) {
                loop(beanclassAnnotationMetadata, itfMetadata);
            }
        }
    }

}
