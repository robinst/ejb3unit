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
 * $Id: EJBLocalObject.java 1100 2006-08-16 13:05:31Z benoitf $
 * --------------------------------------------------------------------------
 */

package javax.ejb;

/**
 * Used by EJB 2.1 for their business local interface.
 * @see <a href="http://www.jcp.org/en/jsr/detail?id=220">EJB 3.0 specification</a>
 * @author Florent Benoit
 */
public interface EJBLocalObject {

    /**
     * Obtain the enterprise Bean's local home interface. The local home
     * interface defines the enterprise Bean's create, finder, remove, and home
     * business methods that are available to local clients.
     * @return A reference to the enterprise Bean's local home interface.
     * @throws EJBException Thrown when the method failed due to a system-level
     *         failure.
     */
    EJBLocalHome getEJBLocalHome() throws EJBException;

    /**
     * Obtain the primary key of the EJB local object.<br>
     * This method can be called on an entity bean. An attempt to invoke this
     * method on a session Bean will result in an EJBException.
     * @return The EJB local object's primary key.
     * @throws EJBException Thrown when the method failed due to a system-level
     *         failure or when invoked on a session bean.
     */
    Object getPrimaryKey() throws EJBException;

    /**
     * Remove the EJB local object.
     * @throws RemoveException The enterprise Bean or the container does not
     *         allow destruction of the object.
     * @throws EJBException Thrown when the method failed due to a system-level
     *         failure.
     */
    void remove() throws RemoveException, EJBException;

    /**
     * Test if a given EJB local object is identical to the invoked EJB local
     * object.
     * @param obj An object to test for identity with the invoked object.
     * @return True if the given EJB local object is identical to the invoked
     *         object, false otherwise.
     * @throws EJBException Thrown when the method failed due to a system-level
     *         failure.
     */
    boolean isIdentical(EJBLocalObject obj) throws EJBException;

}
