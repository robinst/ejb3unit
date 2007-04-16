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
 * $Id: ConcurrentAccessException.java 1100 2006-08-16 13:05:31Z benoitf $
 * --------------------------------------------------------------------------
 */

package javax.ejb;

/**
 * A ConcurrentAccessException indicates that the client has attempted an
 * invocation on a stateful session bean while another invocation is in
 * progress.
 * @see <a href="http://www.jcp.org/en/jsr/detail?id=220">EJB 3.0 specification</a>
 * @author Florent Benoit
 */
public class ConcurrentAccessException extends EJBException {

    /**
     * UID for serialization.
     */
    private static final long serialVersionUID = -8980855319866981477L;

    /**
     * Default constructor : builds an exception with an empty message.
     */
    public ConcurrentAccessException() {
        super();
    }

    /**
     * Build an exception with the given message.
     * @param message the given message to use.
     */
    public ConcurrentAccessException(final String message) {
        super(message);
    }

    /**
     * Build an exception with the given message and given exception.
     * @param message the given message to use.
     * @param causedByException the cause of this exception.
     */
    public ConcurrentAccessException(final String message, final Exception causedByException) {
        super(message, causedByException);
    }

}
