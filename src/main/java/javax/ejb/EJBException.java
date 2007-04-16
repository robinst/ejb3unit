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
 * $Id: EJBException.java 1100 2006-08-16 13:05:31Z benoitf $
 * --------------------------------------------------------------------------
 */

package javax.ejb;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * Thrown for unexpected exception.
 * @see <a href="http://www.jcp.org/en/jsr/detail?id=220">EJB 3.0 specification</a>
 * @author Florent Benoit
 */
public class EJBException extends RuntimeException {

    /**
     * UID for serialization.
     */
    private static final long serialVersionUID = 796770993296843510L;


    /**
     * Keep the root cause of the exception.
     */
    private Exception causeException = null; // name of this field shouldn't be changed for serialization

    /**
     * Default constructor : builds an exception with an empty message.
     */
    public EJBException() {
        super();
    }

    /**
     * Builds an exception with a given exception.
     * @param causedByException the cause of this exception.
     */
    public EJBException(final Exception causedByException) {
        super(causedByException);
        this.causeException = causedByException;
    }

    /**
     * Builds an exception with a given message.
     * @param message the message of this exception.
     */
    public EJBException(final String message) {
        super(message);
    }

    /**
     * Builds an exception with a given message and given exception.
     * @param message the message of this exception.
     * @param causedByException the cause of this exception.
     */
    public EJBException(final String message, final Exception causedByException) {
        super(message, causedByException);
        this.causeException = causedByException;
    }

    /**
     * @return the cause of this exception.
     */
    public Exception getCausedByException() {
        return causeException;
    }

    /**
     * @return the message of this exception.
     */
    @Override
    public String getMessage() {
        return super.getMessage();
    }

    /**
     * Prints the stack trace on the default stream (System.err).
     */
    @Override
    public void printStackTrace() {
        super.printStackTrace();
    }

    /**
     * Print the stack trace on the given stream.
     * @param printStream the given stream.
     */
    @Override
    public void printStackTrace(final PrintStream printStream) {
        super.printStackTrace(printStream);
    }

    /**
     * Print the stack trace on the given writer.
     * @param printWriter the given writer.
     */
    @Override
    public void printStackTrace(final PrintWriter printWriter) {
        super.printStackTrace(printWriter);
    }

}
