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
 * $Id: EJBHome.java 1100 2006-08-16 13:05:31Z benoitf $
 * --------------------------------------------------------------------------
 */

package javax.ejb;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Used by EJB 2.1 for their business remote home interface.
 * @see <a href="http://www.jcp.org/en/jsr/detail?id=220">EJB 3.0 specification</a>
 * @author Florent Benoit
 */
public interface EJBHome extends Remote {

    /**
     * Remove an EJB object identified by its handle.
     * @param handle the given handle
     * @throws RemoteException Thrown when the method failed due to a
     *         system-level failure.
     * @throws RemoveException Thrown if the enterprise Bean or the container
     *         does not allow the client to remove the object.
     */
    void remove(Handle handle) throws RemoteException, RemoveException;

    /**
     * Remove an EJB object identified by its primary key.
     * @param primaryKey the given primary key;
     * @throws RemoteException Thrown when the method failed due to a
     *         system-level failure.
     * @throws RemoveException Thrown if the enterprise Bean or the container
     *         does not allow the client to remove the object.
     */
    void remove(Object primaryKey) throws RemoteException, RemoveException;

    /**
     * Obtain the EJBMetaData interface for the enterprise Bean. The EJBMetaData
     * interface allows the client to obtain information about the enterprise
     * Bean.<br>
     * The information obtainable via the EJBMetaData interface is intended to
     * be used by tools.
     * @return The enterprise Bean's EJBMetaData interface.
     * @throws RemoteException Thrown when the method failed due to a
     *         system-level failure.
     */
    EJBMetaData getEJBMetaData() throws RemoteException;

    /**
     * Obtain a handle for the remote home object. The handle can be used at
     * later time to re-obtain a reference to the remote home object, possibly
     * in a different Java Virtual Machine.
     * @return A handle for the remote home object.
     * @throws RemoteException Thrown when the method failed due to a
     *         system-level failure.
     */
    HomeHandle getHomeHandle() throws RemoteException;

}
