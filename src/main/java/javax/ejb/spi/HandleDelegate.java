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
 * $Id: HandleDelegate.java 1100 2006-08-16 13:05:31Z benoitf $
 * --------------------------------------------------------------------------
 */

package javax.ejb.spi;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.ejb.EJBHome;
import javax.ejb.EJBObject;

/**
 * Used to get used by portable implementations of javax.ejb.Handle and
 * javax.ejb.HomeHandle.
 * @see <a href="http://www.jcp.org/en/jsr/detail?id=220">EJB 3.0 specification</a>
 * @author Florent Benoit
 */
public interface HandleDelegate {

    /**
     * Serialize the EJBObject reference corresponding to a Handle. This method
     * is called from the writeObject method of portable Handle implementation
     * classes. The ostream object is the same object that was passed in to the
     * Handle class's writeObject.
     * @param ejbObject The EJBObject reference to be serialized.
     * @param objectOutputStream The output stream.
     * @throws IOException The EJBObject could not be serialized because of a
     *         system-level failure.
     */
    void writeEJBObject(final EJBObject ejbObject, final ObjectOutputStream objectOutputStream) throws IOException;

    /**
     * Deserialize the EJBObject reference corresponding to a Handle.
     * readEJBObject is called from the readObject method of portable Handle
     * implementation classes. The istream object is the same object that was
     * passed in to the Handle class's readObject. When readEJBObject is called,
     * istream must point to the location in the stream at which the EJBObject
     * reference can be read. The container must ensure that the EJBObject
     * reference is capable of performing invocations immediately after
     * deserialization.
     * @param objectInputStream The input stream.
     * @return The deserialized EJBObject reference.
     * @throws IOException The EJBObject could not be deserialized because of a
     *         system-level failure.
     * @throws ClassNotFoundException The EJBObject could not be deserialized
     *         because some class could not be found.
     */
    EJBObject readEJBObject(ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException;

    /**
     * Serialize the EJBHome reference corresponding to a HomeHandle. This
     * method is called from the writeObject method of portable HomeHandle
     * implementation classes. The ostream object is the same object that was
     * passed in to the Handle class's writeObject.
     * @param ejbHome The EJBHome reference to be serialized.
     * @param objectOutputStream The output stream.
     * @throws IOException The EJBObject could not be serialized because of a
     *         system-level failure.
     */
    void writeEJBHome(EJBHome ejbHome, ObjectOutputStream objectOutputStream) throws IOException;

    /**
     * Deserialize the EJBHome reference corresponding to a HomeHandle.
     * readEJBHome is called from the readObject method of portable HomeHandle
     * implementation classes. The istream object is the same object that was
     * passed in to the HomeHandle class's readObject. When readEJBHome is
     * called, istream must point to the location in the stream at which the
     * EJBHome reference can be read. The container must ensure that the EJBHome
     * reference is capable of performing invocations immediately after
     * deserialization.
     * @param objectInputStream The input stream.
     * @return The deserialized EJBHome reference.
     * @throws IOException The EJBHome could not be deserialized because of a
     *         system-level failure.
     * @throws ClassNotFoundException The EJBHome could not be deserialized
     *         because some class could not be found.
     */
    EJBHome readEJBHome(ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException;

}
