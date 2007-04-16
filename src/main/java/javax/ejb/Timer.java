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
 * $Id: Timer.java 1100 2006-08-16 13:05:31Z benoitf $
 * --------------------------------------------------------------------------
 */

package javax.ejb;

import java.io.Serializable;
import java.util.Date;

/**
 * The Timer interface contains information about a timer that was created
 * through the EJB Timer Service.
 * @see <a href="http://www.jcp.org/en/jsr/detail?id=220">EJB 3.0 specification</a>
 * @author Florent Benoit
 */
public interface Timer {

    /**
     * Cause the timer and all its associated expiration notifications to be
     * cancelled.
     * @throws IllegalStateException If this method is invoked while the
     *         instance is in a state that does not allow access to this method.
     * @throws NoSuchObjectLocalException If invoked on a timer that has expired
     *         or has been cancelled.
     * @throws EJBException If this method could not complete due to a
     *         system-level failure.
     */
    void cancel() throws IllegalStateException, NoSuchObjectLocalException, EJBException;

    /**
     * Get the number of milliseconds that will elapse before the next scheduled
     * timer expiration.
     * @return the number of milliseconds that will elapse before the next
     *         scheduled timer expiration.
     * @throws IllegalStateException If this method is invoked while the
     *         instance is in a state that does not allow access to this method.
     * @throws NoSuchObjectLocalException If invoked on a timer that has expired
     *         or has been cancelled.
     * @throws EJBException If this method could not complete due to a
     *         system-level failure.
     */
    long getTimeRemaining() throws IllegalStateException, NoSuchObjectLocalException, EJBException;

    /**
     * Get the point in time at which the next timer expiration is scheduled to
     * occur.
     * @return the point in time at which the next timer expiration is scheduled
     *         to occur.
     * @throws IllegalStateException If this method is invoked while the
     *         instance is in a state that does not allow access to this method.
     * @throws NoSuchObjectLocalException If invoked on a timer that has expired
     *         or has been cancelled.
     * @throws EJBException If this method could not complete due to a
     *         system-level failure.
     */
    Date getNextTimeout() throws IllegalStateException, NoSuchObjectLocalException, EJBException;

    /**
     * Get the information associated with the timer at the time of creation.
     * @return The Serializable object that was passed in at timer creation, or
     *         null if the info argument passed in at timer creation was null.
     * @throws IllegalStateException If this method is invoked while the
     *         instance is in a state that does not allow access to this method.
     * @throws NoSuchObjectLocalException If invoked on a timer that has expired
     *         or has been cancelled.
     * @throws EJBException If this method could not complete due to a
     *         system-level failure.
     */
    Serializable getInfo() throws IllegalStateException, NoSuchObjectLocalException, EJBException;

    /**
     * Get a serializable handle to the timer. This handle can be used at a
     * later time to re-obtain the timer reference.
     * @return a serializable handle to the timer.
     * @throws IllegalStateException If this method is invoked while the
     *         instance is in a state that does not allow access to this method.
     * @throws NoSuchObjectLocalException If invoked on a timer that has expired
     *         or has been cancelled.
     * @throws EJBException If this method could not complete due to a
     *         system-level failure.
     */
    TimerHandle getHandle() throws IllegalStateException, NoSuchObjectLocalException, EJBException;

}
