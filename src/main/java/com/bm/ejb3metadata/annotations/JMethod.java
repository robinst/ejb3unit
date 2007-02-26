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
 * $Id: JMethod.java 47 2006-02-28 10:42:29Z benoitf $
 * --------------------------------------------------------------------------
 */

package com.bm.ejb3metadata.annotations;

import java.lang.reflect.Method;
import java.util.Arrays;

import org.ejb3unit.asm.Type;

/**
 * This class defines a Method object. It is not based on reflection but allows
 * to build a JMethod based on java.lang.reflect.Method
 * @author Florent Benoit
 */
public class JMethod {

    /**
     * Name of the method.
     */
    private String name = null;

    /**
     * Access mode (see {@link org.ejb3unit.asm.Opcodes}).
     */
    private int access;

    /**
     * Method's descriptor.
     */
    private String descriptor = null;

    /**
     * Method's signature.
     */
    private String signature;

    /**
     * Exceptions of the method.
     */
    private String[] exceptions;

    /**
     * Constructor.
     * @param access the access mode (see {@link org.ejb3unit.asm.Opcodes})
     * @param name the method's name.
     * @param descriptor the method's descriptor (see
     *        {@link org.ejb3unit.asm.Type Type}).
     * @param signature the method's signature. May be <tt>null</tt> if the
     *        method parameters, return type and exceptions do not use generic
     *        types.
     * @param exceptions the internal names of the method's exception classes
     *        (see
     *        {@link org.ejb3unit.asm.Type#getInternalName() getInternalName}).
     *        May be <tt>null</tt>.
     */
    public JMethod(final int access, final String name, final String descriptor, final String signature,
            final String[] exceptions) {
        this.access = access;
        this.name = name;
        this.descriptor = descriptor;
        this.signature = signature;
        this.exceptions = exceptions;
    }

    /**
     * the access mode (see {@link org.ejb3unit.asm.Opcodes}).
     * @return the access mode (see {@link org.ejb3unit.asm.Opcodes})
     */
    public int getAccess() {
        return access;
    }

    /**
     * Constructor.
     * @param m {@link java.lang.reflect.Method} method.
     */
    public JMethod(final Method m) {
        this.name = m.getName();
        this.descriptor = Type.getMethodDescriptor(m);
        // FIXME: make this ok
        // this.signature = Type.signature;
        // this.exceptions = exceptions;
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     * @param obj object to compare
     * @return true if given object is equals
     */
    @Override
    public boolean equals(final Object obj) {
        if (obj != null && obj instanceof JMethod) {
            JMethod other = (JMethod) obj;

            // same name
            if (!this.name.equals(other.name)) {
                return false;
            }

            // same descriptor
            if ((this.descriptor != null) && (!this.descriptor.equals(other.descriptor))) {
                return false;
            }

            // Don't check signature (which include generics information)
            // For example void method(List<Integer>) and
            //             void method(List<String>)
            // and even if signature is different, they have same erasure
            // This is not allowed, so don't need to check signature information.


            // if all tests succeed, return true
            return true;
        }
        return false;
    }

    /**
     * a hash code value for the object.
     * @return a hash code value for the object.
     */
    @Override
    public int hashCode() {
        return name.hashCode();
    }

    /**
     * method descriptor.
     * @return method descriptor
     */
    public String getDescriptor() {
        return descriptor;
    }

    /**
     * method exceptions.
     * @return method exceptions
     */
    public String[] getExceptions() {
        return exceptions;
    }

    /**
     * method name.
     * @return method name
     */
    public String getName() {
        return name;
    }

    /**
     * method signature.
     * method signature
     * @return method signature
     */
    public String getSignature() {
        return signature;
    }

    /**
     * string representation.
     * @return string representation
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        // classname
        sb.append(this.getClass().getName().substring(this.getClass().getPackage().getName().length() + 1));

        // name
        sb.append("[name=");
        sb.append(name);

        // access
        sb.append(", access=");
        sb.append(access);

        // descriptor
        if (descriptor != null) {
            sb.append(", descriptor=");
            sb.append(descriptor);
        }

        // signature
        if (signature != null) {
            sb.append(", signature=");
            sb.append(signature);
        }

        // exceptions
        if (exceptions != null) {
            sb.append(", exceptions=");
            sb.append(Arrays.asList(exceptions));
        }
        sb.append("]");
        return sb.toString();
    }
}
