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
 * $Id: Resource.java 1100 2006-08-16 13:05:31Z benoitf $
 * --------------------------------------------------------------------------
 */

package javax.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Used to declare a link to a resource (like a Bean's context, a DataSource, etc).
 * @see <a href="http://www.jcp.org/en/jsr/detail?id=220">EJB 3.0 specification</a>
 * @author Florent Benoit
 * @since EJB 3.0 version.
 */
// seems strange, documented annotation is not used  here ?
@Target({TYPE, METHOD, FIELD})
@Retention(RUNTIME)
public @interface Resource {
    /**
     * Type of the authentication.
     */
   public enum AuthenticationType {
       /**
        * Container.
        */
       CONTAINER,

       /**
        * Application.
        */
       APPLICATION
   }

   /**
    * Name of the resource. (in java:comp ENC environment)
    */
   String name() default "";

   /**
    * Type of the resource.
    */
   Class type() default Object.class;

   /**
    * The authentication type, with container as default.
    */
   AuthenticationType authenticationType() default AuthenticationType.CONTAINER;

   /**
    * Share this resource between components ?
    */
   boolean shareable() default true;

   /**
    * JNDI name to use for the resource. (name found in the RMI registry)
    */
   String mappedName() default "";

   /**
    * A description for this resource.
    */
   String description() default "";
}

