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
 * $Id: InterceptorType.java 403 2006-04-24 16:39:53Z benoitf $
 * --------------------------------------------------------------------------
 */

package com.bm.ejb3metadata.annotations;

/**
 * This class defines the type available for life cycle events.
 * 
 * @author Florent Benoit
 */
public enum InterceptorType {
	/**
	 * List of the types.
	 */
	AROUND_INVOKE, POST_CONSTRUCT, PRE_DESTROY, POST_ACTIVATE, PRE_PASSIVATE, DEP_INJECT;

}
