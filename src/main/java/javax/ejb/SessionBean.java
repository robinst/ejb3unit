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
 * $Id: SessionBean.java 1100 2006-08-16 13:05:31Z benoitf $
 * --------------------------------------------------------------------------
 */

package javax.ejb;

import java.rmi.RemoteException;

/**
 * The SessionBean interface is implemented by every session enterprise Bean
 * class. The container uses the SessionBean methods to notify the enterprise
 * Bean instances of the instance's life cycle events.
 * @see <a href="http://www.jcp.org/en/jsr/detail?id=220">EJB 3.0 specification</a>
 * @author Florent Benoit
 */
public interface SessionBean extends EnterpriseBean {

    /**
     * Set the associated session context. The container calls this method after
     * the instance creation. The enterprise Bean instance should store the
     * reference to the context object in an instance variable. This method is
     * called with no transaction context.
     * @param ctx A SessionContext interface for the instance.
     * @throws EJBException Thrown by the method to indicate a failure caused by
     *         a system-level error.
     * @throws RemoteException This exception is defined in the method signature
     *         to provide backward compatibility for applications written for
     *         the EJB 1.0 specification. Enterprise beans written for the EJB
     *         1.1 specification should throw the javax.ejb.EJBException instead
     *         of this exception. Enterprise beans written for the EJB2.0 and
     *         higher specifications must throw the javax.ejb.EJBException
     *         instead of this exception.
     */
    void setSessionContext(SessionContext ctx) throws EJBException, RemoteException;

    /**
     * A container invokes this method before it ends the life of the session
     * object. This happens as a result of a client's invoking a remove
     * operation, or when a container decides to terminate the session object
     * after a timeout. This method is called with no transaction context.
     * @throws EJBException Thrown by the method to indicate a failure caused by
     *         a system-level error.
     * @throws RemoteException This exception is defined in the method signature
     *         to provide backward compatibility for enterprise beans written
     *         for the EJB 1.0 specification. Enterprise beans written for the
     *         EJB 1.1 specification should throw the javax.ejb.EJBException
     *         instead of this exception. Enterprise beans written for the
     *         EJB2.0 and higher specifications must throw the
     *         javax.ejb.EJBException instead of this exception.
     */
    void ejbRemove() throws EJBException, RemoteException;

    /**
     * The activate method is called when the instance is activated from its
     * "passive" state. The instance should acquire any resource that it has
     * released earlier in the ejbPassivate() method. This method is called with
     * no transaction context.
     * @throws EJBException Thrown by the method to indicate a failure caused by
     *         a system-level error.
     * @throws RemoteException This exception is defined in the method signature
     *         to provide backward compatibility for enterprise beans written
     *         for the EJB 1.0 specification. Enterprise beans written for the
     *         EJB 1.1 specification should throw the javax.ejb.EJBException
     *         instead of this exception. Enterprise beans written for the
     *         EJB2.0 and higher specifications must throw the
     *         javax.ejb.EJBException instead of this exception.
     */
    void ejbActivate() throws EJBException, RemoteException;

    /**
     * The passivate method is called before the instance enters the "passive"
     * state. The instance should release any resources that it can re-acquire
     * later in the ejbActivate() method. After the passivate method completes,
     * the instance must be in a state that allows the container to use the Java
     * Serialization protocol to externalize and store away the instance's
     * state. This method is called with no transaction context.
     * @throws EJBException Thrown by the method to indicate a failure caused by
     *         a system-level error.
     * @throws RemoteException This exception is defined in the method signature
     *         to provide backward compatibility for enterprise beans written
     *         for the EJB 1.0 specification. Enterprise beans written for the
     *         EJB 1.1 specification should throw the javax.ejb.EJBException
     *         instead of this exception. Enterprise beans written for the
     *         EJB2.0 and higher specifications must throw the
     *         javax.ejb.EJBException instead of this exception.
     */
    void ejbPassivate() throws EJBException, RemoteException;
}
