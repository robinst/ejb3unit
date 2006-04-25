//$Id: PrimitiveByteArrayBlobType.java,v 1.1 2006/04/17 12:11:06 daniel_wiese Exp $
package org.hibernate.type;

/**
 * Map a byte[] to a Blob
 *
 * @author Emmanuel Bernard
 */
public class PrimitiveByteArrayBlobType extends ByteArrayBlobType {
	public Class getReturnedClass() {
		return byte[].class;
	}

	protected Object wrap(byte[] bytes) {
		return bytes;
	}

	protected byte[] unWrap(Object bytes) {
		return ( byte[] ) bytes;
	}
}
