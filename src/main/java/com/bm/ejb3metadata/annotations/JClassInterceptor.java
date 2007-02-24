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
 * $Id: JClassInterceptor.java 841 2006-07-12 09:03:06Z benoitf $
 * --------------------------------------------------------------------------
 */

package com.bm.ejb3metadata.annotations;


/**
 * This class defines a Bean interceptor with the name of the class and the
 * method which is the interceptor.
 * @author Florent Benoit
 */
public class JClassInterceptor {

    /**
     * String name of the class (internal name) where is the interceptor.
     */
    private String className = null;

    /**
     * Method with &#64;{@link javax.interceptor.AroundInvoke} annotation.
     */
    private JMethod jMethod = null;

    /**
     * NO ID.
     */
    private static final int NO_ID = -1;

    /**
     * Id of this interceptor (0 = no id).
     */
    private int id = NO_ID;

    /**
     * Constructor.
     * @param className String name of the class (internal name).
     * @param jMethod the method with aroundInvoke annotation.
     * @param id the id of this interceptor
     */
    public JClassInterceptor(final String className, final JMethod jMethod, final int id) {
        this.className = className;
        this.jMethod = jMethod;
        this.id = id;
    }

    /**
     * Constructor.
     * @param className String name of the class (internal name).
     * @param jMethod the method with aroundInvoke annotation.
     */
    public JClassInterceptor(final String className, final JMethod jMethod) {
       this(className, jMethod, NO_ID);
    }

    /**
     * @return class (internal name) where is the interceptor.
     */
    public String getClassName() {
        return className;
    }

    /**
     * @return Method with &#64;{@link javax.interceptor.AroundInvoke} annotation
     */
    public JMethod getJMethod() {
        return jMethod;
    }

    /**
     * Equals method.
     * @param another object to compare.
     * @return true if the objects are the same.
     */
    @Override
    public boolean equals(final Object another) {
        if (!(another instanceof JClassInterceptor)) {
            return false;
        }
        JClassInterceptor anotherItcp = (JClassInterceptor) another;
        if (id == NO_ID) {
            return (anotherItcp.className.equals(className) && anotherItcp.jMethod.equals(jMethod));
        }
        return (anotherItcp.className.equals(className) && anotherItcp.jMethod.equals(jMethod) && anotherItcp.id == id);
    }

    /**
     * @return hashCode of the object.
     */
    @Override
    public int hashCode() {
        return className.hashCode() + jMethod.hashCode();
    }

}
