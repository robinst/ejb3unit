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
 * $Id: TimerService.java 1311 2007-02-13 17:33:45Z benoitf $
 * --------------------------------------------------------------------------
 */

package javax.ejb;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;

/**
 * The TimerService interface provides enterprise bean components with access to
 * the container-provided Timer Service. The EJB Timer Service allows entity
 * beans, stateless session beans, and message-driven beans to be registered for
 * timer callback events at a specified time, after a specified elapsed time, or
 * after a specified interval.
 * @see <a href="http://www.jcp.org/en/jsr/detail?id=220">EJB 3.0 specification</a>
 * @author Florent Benoit
 */
public interface TimerService {

    /**
     * Create a single-action timer that expires after a specified duration.
     * @param duration The number of milliseconds that must elapse before the
     *        timer expires.
     * @param info Application information to be delivered along with the timer
     *        expiration notification. This can be null.
     * @return The newly created Timer.
     * @throws IllegalArgumentException If duration is negative
     * @throws IllegalStateException If this method is invoked while the
     *         instance is in a state that does not allow access to this method.
     * @throws EJBException If this method fails due to a system-level failure.
     */
    Timer createTimer(final long duration, final Serializable info) throws IllegalArgumentException, IllegalStateException,
            EJBException;

    /**
     * Create an interval timer whose first expiration occurs after a specified
     * duration, and whose subsequent expirations occur after a specified
     * interval.
     * @param initialDuration The number of milliseconds that must elapse before
     *        the first timer expiration notification.
     * @param intervalDuration The number of milliseconds that must elapse
     *        between timer expiration notifications. Expiration notifications
     *        are scheduled relative to the time of the first expiration. If
     *        expiration is delayed(e.g. due to the interleaving of other method
     *        calls on the bean) two or more expiration notifications may occur
     *        in close succession to "catch up".
     * @param info Application information to be delivered along with the timer
     *        expiration. This can be null.
     * @return The newly created Timer.
     * @throws IllegalArgumentException If initialDuration is negative, or
     *         intervalDuration is negative.
     * @throws IllegalStateException If this method is invoked while the
     *         instance is in a state that does not allow access to this method.
     * @throws EJBException If this method could not complete due to a
     *         system-level failure.
     */
    Timer createTimer(final long initialDuration, final long intervalDuration, final Serializable info)
            throws IllegalArgumentException, IllegalStateException, EJBException;

    /**
     * Create a single-action timer that expires at a given point in time.
     * @param expiration The point in time at which the timer must expire.
     * @param info Application information to be delivered along with the timer
     *        expiration notification. This can be null.
     * @return The newly created Timer.
     * @throws IllegalArgumentException If expiration is null, or
     *         expiration.getTime() is negative.
     * @throws IllegalStateException If this method is invoked while the
     *         instance is in a state that does not allow access to this method.
     * @throws EJBException If this method could not complete due to a
     *         system-level failure.
     */
    Timer createTimer(final Date expiration, final Serializable info) throws IllegalArgumentException, IllegalStateException,
            EJBException;

    /**
     * Create an interval timer whose first expiration occurs at a given point
     * in time and whose subsequent expirations occur after a specified
     * interval.
     * @param initialExpiration The point in time at which the first timer
     *        expiration must occur.
     * @param intervalDuration The number of milliseconds that must elapse
     *        between timer expiration notifications. Expiration notifications
     *        are scheduled relative to the time of the first expiration. If
     *        expiration is delayed(e.g. due to the interleaving of other method
     *        calls on the bean) two or more expiration notifications may occur
     *        in close succession to "catch up".
     * @param info Application information to be delivered along with the timer
     *        expiration. This can be null.
     * @return The newly created Timer.
     * @throws IllegalArgumentException If initialExpiration is null, or
     *         initialExpiration.getTime() is negative, or intervalDuration is
     *         negative.
     * @throws IllegalStateException If this method is invoked while the
     *         instance is in a state that does not allow access to this method.
     * @throws EJBException If this method could not complete due to a
     *         system-level failure.
     */
    Timer createTimer(Date initialExpiration, long intervalDuration, Serializable info) throws IllegalArgumentException,
            IllegalStateException, EJBException;

    /**
     * Get all the active timers associated with this bean.
     * @return A collection of javax.ejb.Timer objects.
     * @throws IllegalStateException If this method is invoked while the
     *         instance is in a state that does not allow access to this method.
     * @throws EJBException If this method could not complete due to a
     *         system-level failure.
     */
    Collection getTimers() throws IllegalStateException, EJBException;

}
