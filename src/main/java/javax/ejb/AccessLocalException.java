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
 * $Id: AccessLocalException.java 1100 2006-08-16 13:05:31Z benoitf $
 * --------------------------------------------------------------------------
 */

package javax.ejb;

/**
 * An AccessLocalException is thrown to indicate that the caller does not have
 * permission to call the method. This exception is thrown to local clients.
 * @see <a href="http://www.jcp.org/en/jsr/detail?id=220">EJB 3.0 specification</a>
 * @author Florent Benoit
 */
public class AccessLocalException extends EJBException {

    /**
     * UID for serialization.
     */
    private static final long serialVersionUID = -4089104928924996726L;

    /**
     * Default constructor : builds an exception with an empty message.
     */
    public AccessLocalException() {
        super();
    }

    /**
     * Build an exception with the given message.
     * @param message the given message to use.
     */
    public AccessLocalException(final String message) {
        super(message);
    }

    /**
     * Build an exception with the given message and given exception.
     * @param message the given message to use.
     * @param causedByException the cause of this exception.
     */
    public AccessLocalException(final String message, final Exception causedByException) {
        super(message, causedByException);
    }

}
