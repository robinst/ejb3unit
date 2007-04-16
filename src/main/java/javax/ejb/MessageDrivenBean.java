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
 * $Id: MessageDrivenBean.java 1100 2006-08-16 13:05:31Z benoitf $
 * --------------------------------------------------------------------------
 */

package javax.ejb;


/**
 * The MessageDrivenBean interface is implemented by every message-driven
 * enterprise Bean class. The container uses the MessageDrivenBean methods to
 * notify the enterprise Bean instances of the instance's life cycle events.
 * @see <a href="http://www.jcp.org/en/jsr/detail?id=220">EJB 3.0 specification</a>
 * @author Florent Benoit
 */
public interface MessageDrivenBean extends EnterpriseBean {

    /**
     * Set the associated message-driven context. The container calls this
     * method after the instance creation. The enterprise Bean instance should
     * store the reference to the context object in an instance variable. This
     * method is called with no transaction context.
     * @param ctx A MessageDrivenContext interface for the instance.
     * @throws EJBException Thrown by the method to indicate a failure caused by
     *         a system-level error.
     */
    void setMessageDrivenContext(MessageDrivenContext ctx) throws EJBException;

    /**
     * A container invokes this method before it ends the life of the
     * message-driven object. This happens when a container decides to terminate
     * the message-driven object. This method is called with no transaction
     * context.
     * @throws EJBException Thrown by the method to indicate a failure caused by
     *         a system-level error.
     */
    void ejbRemove() throws EJBException;

}
