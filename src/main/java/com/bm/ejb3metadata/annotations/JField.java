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
 * $Id: JField.java 47 2006-02-28 10:42:29Z benoitf $
 * --------------------------------------------------------------------------
 */

package com.bm.ejb3metadata.annotations;

/**
 * This class defines a Field object. It is not based on reflection.
 * @author Florent Benoit
 */
public class JField {

    /**
     * Name of the field.
     */
    private String name = null;

    /**
     * Access mode (see {@link org.ejb3unit.asm.Opcodes}).
     */
    private int access;

    /**
     * Field's descriptor.
     */
    private String descriptor = null;

    /**
     * Field's signature.
     */
    private String signature;

    /**
     * Value of the field.
     */
    private Object value;

    /**
     * Constructor. *
     * @param access the field's access flags (see
     *        {@link org.ejb3unit.asm.Opcodes}). This parameter also indicates
     *        if the field is synthetic and/or deprecated.
     * @param name the field's name.
     * @param descriptor the field's descriptor (see
     *        {@link org.ejb3unit.asm.Type}).
     * @param signature the field's signature. May be <tt>null</tt> if the
     *        field's type does not use generic types.
     * @param value the field's initial value. This parameter, which may be
     *        <tt>null</tt> if the field does not have an initial value, must
     *        be an {@link Integer}, a {@link Float}, a {@link Long}, a
     *        {@link Double} or a {@link String} (for <tt>int</tt>,
     *        <tt>float</tt>, <tt>long</tt> or <tt>String</tt> fields
     *        respectively). <i>This parameter is only used for static fields</i>.
     *        Its value is ignored for non static fields, which must be
     *        initialized through bytecode instructions in constructors or
     *        methods.
     */
    public JField(final int access, final String name, final String descriptor, final String signature,
            final Object value) {
        this.access = access;
        this.name = name;
        this.descriptor = descriptor;
        this.signature = signature;
        this.value = value;
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     * @param obj object to compare
     * @return true if given object is equals
     */
    @Override
    public boolean equals(final Object obj) {
        if (obj != null && obj instanceof JField) {
            JField other = (JField) obj;

            // same name
            if (!this.name.equals(other.name)) {
                return false;
            }

            // same descriptor
            if ((this.descriptor != null) && (!this.descriptor.equals(other.descriptor))) {
                return false;
            }

            // same signature
            if ((this.signature != null) && (!this.signature.equals(other.signature))) {
                return false;
            }

            // if all tests succeed, return true
            return true;
        }
        return false;
    }

    /**
     * @return a hash code value for the object.
     */
    @Override
    public int hashCode() {
        return name.hashCode();
    }

    /**
     * @return field's descriptor.
     */
    public String getDescriptor() {
        return descriptor;
    }

    /**
     * @return field's value.
     */
    public Object getValue() {
        return value;
    }

    /**
     * @return method name
     */
    public String getName() {
        return name;
    }

    /**
     * @return method signature
     */
    public String getSignature() {
        return signature;
    }

    /**
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
        if (value != null) {
            sb.append(", value=");
            sb.append(value);
        }
        sb.append("]");
        return sb.toString();
    }

    /**
     * @return the field's access flags
     */
    public int getAccess() {
        return access;
    }
}
