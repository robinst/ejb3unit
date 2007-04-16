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
 * $Id: EJBContext.java 1100 2006-08-16 13:05:31Z benoitf $
 * --------------------------------------------------------------------------
 */

package javax.ejb;

import java.security.Identity;
import java.security.Principal;
import java.util.Properties;

import javax.transaction.UserTransaction;

/**
 * Allows to gets some info on the bean.
 * @see <a href="http://www.jcp.org/en/jsr/detail?id=220">EJB 3.0 specification</a>
 * @author Florent Benoit
 */
public interface EJBContext {

    /**
     * @return the Home(remote) interface of the bean.
     * throws IllegalStateException if home is not retrieved
     */
    EJBHome getEJBHome();

    /**
     * @return the local home interface of the bean.
     * throws IllegalStateException if local home is not retrieved
     */
    EJBLocalHome getEJBLocalHome();

    /**
     * @return deprecated
     */
    @Deprecated
    Properties getEnvironment();

    /**
     * @return deprecated
     */
    @Deprecated
    Identity getCallerIdentity();

    /**
     * @return the caller principal object.
     */
    Principal getCallerPrincipal();

    /**
     * @param role deprecated
     * @return deprecated
     */
    @Deprecated
    boolean isCallerInRole(final Identity role);

    /**
     * Check if the given role is in the roles of the current caller's
     * principal.
     * @param roleName the role to check.
     * @return true if it is included, else false.
     */
    boolean isCallerInRole(final String roleName);

    /**
     * Gets the current transaction.
     * @return the transaction object
     * @throws IllegalStateException in case of Container Managed Transaction.
     */
    UserTransaction getUserTransaction() throws IllegalStateException;

    /**
     * Sets the current transaction in rollback only mode.
     * @throws IllegalStateException in CMT case
     */
    void setRollbackOnly() throws IllegalStateException;

    /**
     * Check if current transaction is marked as rollback only.
     * @return true if current tx is in rollback mode.
     * @throws IllegalStateException if used with CMT.
     */
    boolean getRollbackOnly() throws IllegalStateException;

    /**
     * Gets the timer service.
     * @return an instance of the timer service.
     * @throws IllegalStateException if used within a SFSB.
     */
    TimerService getTimerService() throws IllegalStateException;

    /**
     * Search the given name in java:comp/env ENC environment.
     * @param name the name to search
     * @return null if not found, else an instance of the object found.
     * @since EJB 3.0 version.
     */
    Object lookup(final String name);
}
