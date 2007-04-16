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
 * $Id: TransactionRolledbackLocalException.java 1100 2006-08-16 13:05:31Z benoitf $
 * --------------------------------------------------------------------------
 */

package javax.ejb;

/**
 * This exception indicates that the transaction associated with processing of
 * the request has been rolled back, or marked to roll back. Thus the requested
 * operation either could not be performed or was not performed because further
 * computation on behalf of the transaction would be fruitless
 * @see <a href="http://www.jcp.org/en/jsr/detail?id=220">EJB 3.0 specification</a>
 * @author Florent Benoit
 */
public class TransactionRolledbackLocalException extends EJBException {

    /**
     * UID for serialization.
     */
    private static final long serialVersionUID = 2897658132751784821L;

    /**
     * Default constructor : builds an exception with an empty message.
     */
    public TransactionRolledbackLocalException() {
        super();
    }

    /**
     * Build an exception with the given message.
     * @param message the given message to use.
     */
    public TransactionRolledbackLocalException(final String message) {
        super(message);
    }

    /**
     * Builds an exception with a given message and given exception.
     * @param message the message of this exception.
     * @param causedByException the cause of this exception.
     */
    public TransactionRolledbackLocalException(final String message, final Exception causedByException) {
        super(message, causedByException);
    }
}
