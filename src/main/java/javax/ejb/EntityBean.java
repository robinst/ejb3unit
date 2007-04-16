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
 * $Id: EntityBean.java 1100 2006-08-16 13:05:31Z benoitf $
 * --------------------------------------------------------------------------
 */

package javax.ejb;

import java.rmi.RemoteException;

/**
 * Each EJB 2.1 Entity bean needs to implement this interface.
 * @see <a href="http://www.jcp.org/en/jsr/detail?id=220">EJB 3.0 specification</a>
 * @author Florent Benoit
 */
public interface EntityBean extends EnterpriseBean {

    /**
     * Set the associated entity context. The container invokes this method on
     * an instance after the instance has been created. This method is called in
     * an unspecified transaction context.
     * @param ctx An EntityContext interface for the instance. The instance
     *        should store the reference to the context in an instance variable.
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
    void setEntityContext(EntityContext ctx) throws EJBException, RemoteException;

    /**
     * Unset the associated entity context. The container calls this method
     * before removing the instance. This is the last method that the container
     * invokes on the instance. The Java garbage collector will eventually
     * invoke the finalize() method on the instance. This method is called in an
     * unspecified transaction context.
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
    void unsetEntityContext() throws EJBException, RemoteException;

    /**
     * A container invokes this method before it removes the EJB object that is
     * currently associated with the instance. This method is invoked when a
     * client invokes a remove operation on the enterprise Bean's home interface
     * or the EJB object's remote interface. This method transitions the
     * instance from the ready state to the pool of available instances. This
     * method is called in the transaction context of the remove operation.
     * @throws RemoveException The enterprise Bean does not allow destruction of
     *         the object.
     * @throws EJBException The enterprise Bean does not allow destruction of
     *         the object.
     * @throws RemoteException This exception is defined in the method signature
     *         to provide backward compatibility for enterprise beans written
     *         for the EJB 1.0 specification. Enterprise beans written for the
     *         EJB 1.1 specification should throw the javax.ejb.EJBException
     *         instead of this exception. Enterprise beans written for the
     *         EJB2.0 and higher specifications must throw the
     *         javax.ejb.EJBException instead of this exception.
     */
    void ejbRemove() throws RemoveException, EJBException, RemoteException;

    /**
     * A container invokes this method when the instance is taken out of the
     * pool of available instances to become associated with a specific EJB
     * object. This method transitions the instance to the ready state. This
     * method executes in an unspecified transaction context.
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
     * A container invokes this method on an instance before the instance
     * becomes disassociated with a specific EJB object. After this method
     * completes, the container will place the instance into the pool of
     * available instances. This method executes in an unspecified transaction
     * context.
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

    /**
     * A container invokes this method to instruct the instance to synchronize
     * its state by loading it state from the underlying database. This method
     * always executes in the transaction context determined by the value of the
     * transaction attribute in the deployment descriptor.
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
    void ejbLoad() throws EJBException, RemoteException;

    /**
     * A container invokes this method to instruct the instance to synchronize
     * its state by storing it to the underlying database. This method always
     * executes in the transaction context determined by the value of the
     * transaction attribute in the deployment descriptor.
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
    void ejbStore() throws EJBException, RemoteException;

}
