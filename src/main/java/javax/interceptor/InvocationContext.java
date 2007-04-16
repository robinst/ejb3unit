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
 * $Id: InvocationContext.java 1100 2006-08-16 13:05:31Z benoitf $
 * --------------------------------------------------------------------------
 */

package javax.interceptor;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * Context that is given to all interceptors (business or lifecycle) and that
 * allow to get information on the current invocation.
 * @see <a href="http://www.jcp.org/en/jsr/detail?id=220">EJB 3.0 specification</a>
 * @author Florent Benoit
 * @since EJB 3.0 version.
 */
public interface InvocationContext {

    /**
     * @return the object on which the intercepted method is called.
     */
    Object getTarget();

    /**
     * @return the method that is intercepted (null for lifecycle interceptors).
     */
    Method getMethod();

    /**
     * @return the parameters of the intercepted method (if any)
     */
    Object[] getParameters();

    /**
     * Sets the parameters of the method that is intercepted.
     * @param params the array of parameters.
     */
    void setParameters(Object[] params);

    /**
     * @return a Map that is shared by all interceptors for a given method.
     */
    Map<String, Object> getContextData();

    /**
     * Call the next interceptor in the chain (and at the end it is the intercepted method that is called).
     * @return the result of the invocation on the intercepted method.
     * @throws Exception if method invocation fails.
     */
    Object proceed() throws Exception;


}
