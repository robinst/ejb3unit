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
 * $Id: MessageDriven.java 1152 2006-10-11 14:07:37Z benoitf $
 * --------------------------------------------------------------------------
 */

package javax.ejb;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Defines a Message Driven Bean when applied on a bean's class.
 * @see <a href="http://www.jcp.org/en/jsr/detail?id=220">EJB 3.0 specification</a>
 * @author Florent Benoit
 * @since EJB 3.0 version.
 */
@Target(TYPE)
@Retention(RUNTIME)
public @interface MessageDriven {
    /**
     * Name of this MDB.
     */
    String name() default "";

    /**
     * Class used as Message Listener Interface, for JMS MDB, it is javax.jms.MessageListener class.
     */
    Class messageListenerInterface() default Object.class;

    /**
     * Properties to configure the activation object (it may include destination, destinationType, etc).
     */
    ActivationConfigProperty[] activationConfig() default {};

    /**
     * Product specific.
     */
    String mappedName() default "";

    /**
     * Description of this MDB.
     */
    String description() default "";
}
