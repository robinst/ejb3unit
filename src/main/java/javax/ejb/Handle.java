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
 * $Id: Handle.java 1100 2006-08-16 13:05:31Z benoitf $
 * --------------------------------------------------------------------------
 */

package javax.ejb;

import java.io.Serializable;
import java.rmi.RemoteException;

/**
 * The Handle interface is implemented by all EJB object handles. A handle is an
 * abstraction of a network reference to an EJB object. A handle is intended to
 * be used as a "robust" persistent reference to an EJB object.
 * @see <a href="http://www.jcp.org/en/jsr/detail?id=220">EJB 3.0 specification</a>
 * @author Florent Benoit
 */
public interface Handle extends Serializable {

    /**
     * Obtain the EJB object reference represented by this handle.
     * @return the EJB object reference represented by this handle.
     * @throws RemoteException The EJB object could not be obtained because of a
     *         system-level failure.
     */
    EJBObject getEJBObject() throws RemoteException;

}
