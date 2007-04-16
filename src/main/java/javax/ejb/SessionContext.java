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
 * $Id: SessionContext.java 1100 2006-08-16 13:05:31Z benoitf $
 * --------------------------------------------------------------------------
 */

package javax.ejb;

import javax.xml.rpc.handler.MessageContext;

/**
 * Context provided by Session Bean.
 * @see <a href="http://www.jcp.org/en/jsr/detail?id=220">EJB 3.0 specification</a>
 * @author Florent Benoit
 */
public interface SessionContext extends EJBContext {

    /**
     * Obtain a reference to the EJB local object that is associated with the
     * instance. An instance of a session enterprise Bean can call this method
     * at anytime between the ejbCreate() and ejbRemove() methods, including
     * from within the ejbCreate() and ejbRemove() methods. An instance can use
     * this method, for example, when it wants to pass a reference to itself in
     * a method argument or result.
     * @return The EJB local object currently associated with the instance.
     * @throws IllegalStateException - Thrown if the instance invokes this
     *         method while the instance is in a state that does not allow the
     *         instance to invoke this method, or if the instance does not have
     *         a local interface.
     */
    EJBLocalObject getEJBLocalObject() throws IllegalStateException;

    /**
     * Obtain a reference to the EJB object that is currently associated with
     * the instance. An instance of a session enterprise Bean can call this
     * method at anytime between the ejbCreate() and ejbRemove() methods,
     * including from within the ejbCreate() and ejbRemove() methods. An
     * instance can use this method, for example, when it wants to pass a
     * reference to itself in a method argument or result.
     * @return The EJB object currently associated with the instance.
     * @throws IllegalStateException - Thrown if the instance invokes this
     *         method while the instance is in a state that does not allow the
     *         instance to invoke this method, or if the instance does not have
     *         a remote interface.
     */
    EJBObject getEJBObject() throws IllegalStateException;

    /**
     * Obtain a reference to the JAX-RPC MessageContext. An instance of a
     * stateless session bean can call this method from any business method
     * invoked through its web service endpoint interface.
     * @return The MessageContext for this web service invocation.
     * @throws IllegalStateException - Thrown if this method is invoked while
     *         the instance is in a state that does not allow access to this
     *         method.
     */
    MessageContext getMessageContext() throws IllegalStateException;

    /**
     * Obtain an object that can be used to invoke the current bean through the
     * given business interface.
     * @param <T> the interface of the bean
     * @param businessInterface One of the local business interfaces or remote
     *        business interfaces for this session bean.
     * @return The business object corresponding to the given business
     *         interface.
     * @throws IllegalStateException - Thrown if this method is invoked with an
     *         invalid business interface for the current bean.
     * @since EJB 3.0 version.
     */
    <T> T getBusinessObject(Class<T> businessInterface) throws IllegalStateException;

    /**
     * Obtain the business interface through which the current business method
     * invocation was made.
     * @return the business interface through which the current business method
     *         invocation was made.
     * @throws IllegalStateException - Thrown if this method is called and the
     *         bean has not been invoked through a business interface.
     * @since EJB 3.0 version.
     */
    Class getInvokedBusinessInterface() throws IllegalStateException;

}
