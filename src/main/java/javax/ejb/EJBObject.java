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
 * $Id: EJBObject.java 1100 2006-08-16 13:05:31Z benoitf $
 * --------------------------------------------------------------------------
 */

package javax.ejb;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Used by EJB 2.1 for their business remote interface.
 * @see <a href="http://www.jcp.org/en/jsr/detail?id=220">EJB 3.0 specification</a>
 * @author Florent Benoit
 */
public interface EJBObject extends Remote {

    /**
     * Obtain the enterprise Bean's remote home interface. The remote home
     * interface defines the enterprise Bean's create, finder, remove, and home
     * business methods.
     * @return A reference to the enterprise Bean's home interface.
     * @throws RemoteException Thrown when the method failed due to a
     *         system-level failure.
     */
    EJBHome getEJBHome() throws RemoteException;

    /**
     * Obtain the primary key of the EJB object.<br>
     * This method can be called on an entity bean. An attempt to invoke this
     * method on a session bean will result in RemoteException.
     * @return The EJB object's primary key.
     * @throws RemoteException Thrown when the method failed due to a
     *         system-level failure or when invoked on a session bean.
     */
    Object getPrimaryKey() throws RemoteException;

    /**
     * Remove the EJB object.
     * @throws RemoteException Thrown when the method failed due to a
     *         system-level failure.
     * @throws RemoveException The enterprise Bean or the container does not
     *         allow destruction of the object.
     */
    void remove() throws RemoteException, RemoveException;

    /**
     * Obtain a handle for the EJB object. The handle can be used at later time
     * to re-obtain a reference to the EJB object, possibly in a different Java
     * Virtual Machine.
     * @return A handle for the EJB object.
     * @throws RemoteException Thrown when the method failed due to a
     *         system-level failure.
     */
    Handle getHandle() throws RemoteException;

    /**
     * Test if a given EJB object is identical to the invoked EJB object.
     * @param obj An object to test for identity with the invoked object.
     * @return True if the given EJB object is identical to the invoked object,
     *         false otherwise.
     * @throws RemoteException Thrown when the method failed due to a
     *         system-level failure.
     */
    boolean isIdentical(EJBObject obj) throws RemoteException;

}
