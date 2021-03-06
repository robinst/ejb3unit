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
 * $Id: ObjectNotFoundException.java 1100 2006-08-16 13:05:31Z benoitf $
 * --------------------------------------------------------------------------
 */

package javax.ejb;

/**
 * The ObjectNotFoundException exception is thrown by a finder method to
 * indicate that the specified EJB object does not exist.<br>
 * Only the finder methods that are declared to return a single EJB object use
 * this exception. This exception should not be thrown by finder methods that
 * return a collection of EJB objects (they should return an empty collection
 * instead).
 * @see <a href="http://www.jcp.org/en/jsr/detail?id=220">EJB 3.0 specification</a>
 * @author Florent Benoit
 */
public class ObjectNotFoundException extends FinderException {

    /**
     * UID for serialization.
     */
    private static final long serialVersionUID = 4624364141026778L;

    /**
     * Default constructor : builds an exception with an empty message.
     */
    public ObjectNotFoundException() {
        super();
    }

    /**
     * Build an exception with the given message.
     * @param message the given message to use.
     */
    public ObjectNotFoundException(final String message) {
        super(message);
    }

}
