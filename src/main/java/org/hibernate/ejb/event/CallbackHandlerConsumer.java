//$Id: CallbackHandlerConsumer.java,v 1.1 2006/04/17 12:11:12 daniel_wiese Exp $
package org.hibernate.ejb.event;

/**
 * @author Emmanuel Bernard
 */
public interface CallbackHandlerConsumer {
	void setCallbackHandler(EntityCallbackHandler callbackHandler);
}
