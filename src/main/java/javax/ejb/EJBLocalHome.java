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
 * $Id: EJBLocalHome.java 1100 2006-08-16 13:05:31Z benoitf $
 * --------------------------------------------------------------------------
 */

package javax.ejb;

/**
 * Used by EJB 2.1 for their business local home interface.
 * @see <a href="http://www.jcp.org/en/jsr/detail?id=220">EJB 3.0 specification</a>
 * @author Florent Benoit
 */
public interface EJBLocalHome {

    /**
     * Remove an EJB object identified by its primary key.<br>
     * This method can only be used by local clients of an entity bean. An
     * attempt to call this method on a session bean will result in a
     * RemoveException.
     * @param primaryKey the given primary key
     * @throws RemoveException Thrown if the enterprise Bean or the container
     *         does not allow the client to remove the object.
     * @throws EJBException Thrown when the method failed due to a system-level
     *         failure.
     */
    void remove(Object primaryKey) throws RemoveException, EJBException;

}
