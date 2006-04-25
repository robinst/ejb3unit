//$Id: EJB3PersistOnFlushEventListener.java,v 1.1 2006/04/17 12:11:12 daniel_wiese Exp $
package org.hibernate.ejb.event;

import org.hibernate.engine.CascadingAction;

/**
 * @author Emmanuel Bernard
 */
public class EJB3PersistOnFlushEventListener extends EJB3PersistEventListener {
	protected CascadingAction getCascadeAction() {
		return CascadingAction.PERSIST_ON_FLUSH;
	}
}
