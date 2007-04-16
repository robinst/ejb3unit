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
 * $Id: SessionSynchronization.java 1100 2006-08-16 13:05:31Z benoitf $
 * --------------------------------------------------------------------------
 */

package javax.ejb;

import java.rmi.RemoteException;

/**
 * The SessionSynchronization interface allows a session Bean instance to be
 * notified by its container of transaction boundaries. An session Bean class is
 * not required to implement this interface. A session Bean class should
 * implement this interface only if it wishes to synchronize its state with the
 * transactions.
 * @see <a href="http://www.jcp.org/en/jsr/detail?id=220">EJB 3.0 specification</a>
 * @author Florent Benoit
 */
public interface SessionSynchronization {

    /**
     * The afterBegin method notifies a session Bean instance that a new
     * transaction has started, and that the subsequent business methods on the
     * instance will be invoked in the context of the transaction. The instance
     * can use this method, for example, to read data from a database and cache
     * the data in the instance fields. This method executes in the proper
     * transaction context.
     * @throws EJBException Thrown by the method to indicate a failure caused by
     *         a system-level error.
     * @throws RemoteException This exception is defined in the method signature
     *         to provide backward compatibility for enterprise beans written
     *         for the EJB 1.0 specification. Enterprise beans written for the
     *         EJB 1.1 and higher specifications should throw the
     *         javax.ejb.EJBException instead of this exception. Enterprise
     *         beans written for the EJB 2.0 and higher specifications must not
     *         throw the java.rmi.RemoteException.
     */
    void afterBegin() throws EJBException, RemoteException;

    /**
     * The beforeCompletion method notifies a session Bean instance that a
     * transaction is about to be committed. The instance can use this method,
     * for example, to write any cached data to a database. This method executes
     * in the proper transaction context. Note: The instance may still cause the
     * container to rollback the transaction by invoking the setRollbackOnly()
     * method on the instance context, or by throwing an exception.
     * @throws EJBException Thrown by the method to indicate a failure caused by
     *         a system-level error.
     * @throws RemoteException This exception is defined in the method signature
     *         to provide backward compatibility for enterprise beans written
     *         for the EJB 1.0 specification. Enterprise beans written for the
     *         EJB 1.1 and higher specification should throw the
     *         javax.ejb.EJBException instead of this exception. Enterprise
     *         beans written for the EJB 2.0 and higher specifications must not
     *         throw the java.rmi.RemoteException.
     */
    void beforeCompletion() throws EJBException, RemoteException;

    /**
     * The afterCompletion method notifies a session Bean instance that a
     * transaction commit protocol has completed, and tells the instance whether
     * the transaction has been committed or rolled back. This method executes
     * with no transaction context. This method executes with no transaction
     * context.
     * @param committed True if the transaction has been committed, false if is
     *        has been rolled back.
     * @throws EJBException Thrown by the method to indicate a failure caused by
     *         a system-level error.
     * @throws RemoteException This exception is defined in the method signature
     *         to provide backward compatibility for enterprise beans written
     *         for the EJB 1.0 specification. Enterprise beans written for the
     *         EJB 1.1 and higher specification should throw the
     *         javax.ejb.EJBException instead of this exception. Enterprise
     *         beans written for the EJB 2.0 and higher specifications must not
     *         throw the java.rmi.RemoteException.
     */
    void afterCompletion(final boolean committed) throws EJBException, RemoteException;

}
