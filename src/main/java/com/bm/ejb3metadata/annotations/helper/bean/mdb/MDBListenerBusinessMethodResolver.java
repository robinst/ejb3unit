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
 * $Id: MDBListenerBusinessMethodResolver.java 854 2006-07-12 12:57:45Z benoitf $
 * --------------------------------------------------------------------------
 */

package com.bm.ejb3metadata.annotations.helper.bean.mdb;

import org.hibernate.repackage.cglib.asm.Opcodes;

import com.bm.ejb3metadata.annotations.JMethod;
import com.bm.ejb3metadata.annotations.metadata.ClassAnnotationMetadata;
import com.bm.ejb3metadata.annotations.metadata.MethodAnnotationMetadata;

/**
 * This class finds the listener method of the MDB and mark it as a business
 * method so that it will be intercepted.
 * @author Florent Benoit
 */
public final class MDBListenerBusinessMethodResolver {

    /**
     * onMessage method.
     */
    private static final JMethod ONMESSAGE_METHOD = new JMethod(Opcodes.ACC_PUBLIC, "onMessage", "(Ljavax/jms/Message;)V", null, null);

    /**
     * Helper class, no public constructor.
     */
    private MDBListenerBusinessMethodResolver() {
    }

    /**
     * Mark listener method of the interface as business method.
     * @param sessionBean Session bean to analyze
     */
    public static void resolve(final ClassAnnotationMetadata sessionBean) {
        //TODO: use of another interface than JMS

        // Set business method for onMessage Method (JMS)
        MethodAnnotationMetadata onMessageMethod = sessionBean.getMethodAnnotationMetadata(ONMESSAGE_METHOD);
        if (onMessageMethod != null) {
            onMessageMethod.setBusinessMethod(true);
        }

    }
}
