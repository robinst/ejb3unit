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
 * $Id: EntityContext.java 1100 2006-08-16 13:05:31Z benoitf $
 * --------------------------------------------------------------------------
 */

package javax.ejb;

/**
 * The EntityContext interface provides an instance with access to the
 * container-provided runtime context of an entity enterprise Bean instance.
 * @see <a href="http://www.jcp.org/en/jsr/detail?id=220">EJB 3.0 specification</a>
 * @author Florent Benoit
 */
public interface EntityContext extends EJBContext {

    /**
     * Obtain a reference to the EJB local object that is currently associated
     * with the instance. An instance of an entity enterprise Bean can call this
     * method only when the instance is associated with an EJB local object
     * identity, i.e. in the ejbActivate, ejbPassivate, ejbPostCreate,
     * ejbRemove, ejbLoad, ejbStore, and business methods. An instance can use
     * this method, for example, when it wants to pass a reference to itself in
     * a method argument or result.
     * @return The EJB local object currently associated with the instance.
     * @throws IllegalStateException if the instance invokes this method while
     *         the instance is in a state that does not allow the instance to
     *         invoke this method, or if the instance does not have a local
     *         interface.
     */
    EJBLocalObject getEJBLocalObject() throws IllegalStateException;

    /**
     * Obtain a reference to the EJB object that is currently associated with
     * the instance. An instance of an entity enterprise Bean can call this
     * method only when the instance is associated with an EJB object identity,
     * i.e. in the ejbActivate, ejbPassivate, ejbPostCreate, ejbRemove, ejbLoad,
     * ejbStore, and business methods. An instance can use this method, for
     * example, when it wants to pass a reference to itself in a method argument
     * or result.
     * @return The EJB object currently associated with the instance.
     * @throws IllegalStateException Thrown if the instance invokes this method
     *         while the instance is in a state that does not allow the instance
     *         to invoke this method, or if the instance does not have a remote
     *         interface.
     */
    EJBObject getEJBObject() throws IllegalStateException;

    /**
     * Obtain the primary key of the EJB object that is currently associated
     * with this instance. An instance of an entity enterprise Bean can call
     * this method only when the instance is associated with an EJB object
     * identity, i.e. in the ejbActivate, ejbPassivate, ejbPostCreate,
     * ejbRemove, ejbLoad, ejbStore, and business methods. Note: The result of
     * this method is that same as the result of getEJBObject().getPrimaryKey().
     * @return The primary key currently associated with the instance.
     * @throws IllegalStateException Thrown if the instance invokes this method
     *         while the instance is in a state that does not allow the instance
     *         to invoke this method.
     */
    Object getPrimaryKey() throws IllegalStateException;

}
