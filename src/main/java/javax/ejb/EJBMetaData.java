/*
 * The contents of this file are subject to the terms 
 * of the Common Development and Distribution License 
 * (the License).  You may not use this file except in
 * compliance with the License.
 * 
 * You can obtain a copy of the license at 
 * https://glassfish.dev.java.net/public/CDDLv1.0.html or
 * glassfish/bootstrap/legal/CDDLv1.0.txt.
 * See the License for the specific language governing 
 * permissions and limitations under the License.
 * 
 * When distributing Covered Code, include this CDDL 
 * Header Notice in each file and include the License file 
 * at glassfish/bootstrap/legal/CDDLv1.0.txt.  
 * If applicable, add the following below the CDDL Header, 
 * with the fields enclosed by brackets [] replaced by
 * you own identifying information: 
 * "Portions Copyrighted [year] [name of copyright owner]"
 * 
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 */
package javax.ejb;

/**
 * The EJBMetaData interface allows a client to obtain the enterprise Bean's
 * meta-data information.
 * 
 * <p>
 * The meta-data is intended for development tools used for building
 * applications that use deployed enterprise Beans, and for clients using a
 * scripting language to access the enterprise Bean.
 * 
 * <p>
 * Note that the EJBMetaData is not a remote interface. The class that
 * implements this interface (this class is typically generated by container
 * tools) must be serializable, and must be a valid RMI/IDL value type.
 */
public interface EJBMetaData {
	/**
	 * Obtain the remote home interface of the enterprise Bean.
	 * 
	 * @return the remote home interface of the enterprise Bean.
	 */
	EJBHome getEJBHome();

	/**
	 * Obtain the Class object for the enterprise Bean's remote home interface.
	 * 
	 * @return the Class object for the enterprise Bean's remote home interface.
	 */
	Class getHomeInterfaceClass();

	/**
	 * Obtain the Class object for the enterprise Bean's remote interface.
	 * 
	 * @return the Class object for the enterprise Bean's remote interface.
	 */
	Class getRemoteInterfaceClass();

	/**
	 * Obtain the Class object for the enterprise Bean's primary key class.
	 * 
	 * @return the Class object for the enterprise Bean's primary key class.
	 */
	Class getPrimaryKeyClass();

	/**
	 * Test if the enterprise Bean's type is "session".
	 * 
	 * @return True if the type of the enterprise Bean is session bean.
	 */
	boolean isSession();

	/**
	 * Test if the enterprise Bean's type is "stateless session".
	 * 
	 * @return True if the type of the enterprise Bean is stateless session.
	 */
	boolean isStatelessSession();
}
