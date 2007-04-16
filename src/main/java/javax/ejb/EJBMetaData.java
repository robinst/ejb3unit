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
 * $Id: EJBMetaData.java 1100 2006-08-16 13:05:31Z benoitf $
 * --------------------------------------------------------------------------
 */

package javax.ejb;

/**
 * Metadata of EJB provided to the client.
 * @see <a href="http://www.jcp.org/en/jsr/detail?id=220">EJB 3.0 specification</a>
 * @author Florent Benoit
 */
public interface EJBMetaData {

    /**
     * Obtain the remote home interface of the enterprise Bean.
     * @return the remote home interface of the enterprise Bean.
     */
    EJBHome getEJBHome();

    /**
     * Obtain the Class object for the enterprise Bean's remote home interface.
     * @return the Class object for the enterprise Bean's remote home interface.
     */
    Class getHomeInterfaceClass();

    /**
     * Obtain the Class object for the enterprise Bean's remote interface.
     * @return the Class object for the enterprise Bean's remote interface.
     */
    Class getRemoteInterfaceClass();

    /**
     * Obtain the Class object for the enterprise Bean's primary key class.
     * @return the Class object for the enterprise Bean's primary key class.
     */
    Class getPrimaryKeyClass();

    /**
     * Test if the enterprise Bean's type is "session".
     * @return True if the type of the enterprise Bean is session bean.
     */
    boolean isSession();

    /**
     * Test if the enterprise Bean's type is "stateless session".
     * @return True if the type of the enterprise Bean is stateless session.
     */
    boolean isStatelessSession();
}
