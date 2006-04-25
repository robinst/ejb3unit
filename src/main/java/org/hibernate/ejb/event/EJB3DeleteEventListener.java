//$Id: EJB3DeleteEventListener.java,v 1.1 2006/04/17 12:11:12 daniel_wiese Exp $
package org.hibernate.ejb.event;

import org.hibernate.event.def.DefaultDeleteEventListener;
import org.hibernate.event.EventSource;
import org.hibernate.persister.entity.EntityPersister;

/**
 * Overrides the LifeCycle OnSave call to call the PreRemove operation
 *
 * @author Emmanuel Bernard
 */
public class EJB3DeleteEventListener extends DefaultDeleteEventListener implements CallbackHandlerConsumer {
	private EntityCallbackHandler callbackHandler;

	public void setCallbackHandler(EntityCallbackHandler callbackHandler) {
		this.callbackHandler = callbackHandler;
	}

	public EJB3DeleteEventListener() {
		super();
	};

	public EJB3DeleteEventListener(EntityCallbackHandler callbackHandler) {
		this();
		this.callbackHandler = callbackHandler;
	}

	@Override protected boolean invokeDeleteLifecycle(EventSource session, Object entity, EntityPersister persister) {
		callbackHandler.preRemove( entity );
		return super.invokeDeleteLifecycle( session, entity, persister );
	}

}
