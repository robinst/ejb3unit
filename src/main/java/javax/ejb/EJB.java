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
 * $Id: EJB.java 1100 2006-08-16 13:05:31Z benoitf $
 * --------------------------------------------------------------------------
 */

package javax.ejb;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Allows to inject a reference to an EJB.
 * @see <a href="http://www.jcp.org/en/jsr/detail?id=220">EJB 3.0 specification</a>
 * @author Florent Benoit
 * @since EJB 3.0 version.
 */
@Target({TYPE, METHOD, FIELD}) @Retention(RUNTIME)
public @interface EJB {
    /**
     * Name in java:comp/env ENC environment to use.
     */
    String name() default "";

    /**
     * Interface of the EJB.
     */
    Class beanInterface() default Object.class;

    /**
     * Name of the EJB as it is declared on the Stateless/Stateful bean.
     */
    String beanName() default "";

    /**
     * The JNDI name to use to search bean in the RMI registry.
     */
    String mappedName() default "";

    /**
     * Description of this link to an EJB.
     */
    String description() default "";
}

