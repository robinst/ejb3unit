//$Id: PrimitiveCharacterArrayClobType.java,v 1.1 2006/04/17 12:11:06 daniel_wiese Exp $
package org.hibernate.type;


/**
 * Map a char[] to a Clob
 *
 * @author Emmanuel Bernard
 */
public class PrimitiveCharacterArrayClobType extends CharacterArrayClobType {
	public Class returnedClass() {
		return char[].class;
	}
}
