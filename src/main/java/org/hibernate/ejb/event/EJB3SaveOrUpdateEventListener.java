//$Id: EJB3SaveOrUpdateEventListener.java,v 1.1 2006/04/17 12:11:12 daniel_wiese Exp $
package org.hibernate.ejb.event;

import org.hibernate.event.def.DefaultSaveOrUpdateEventListener;
import org.hibernate.event.EventSource;
import org.hibernate.persister.entity.EntityPersister;

/**
 * Overrides the LifeCycle OnSave call to call the PrePersist operation
 * 
 * @author Emmanuel Bernard
 */
public class EJB3SaveOrUpdateEventListener extends DefaultSaveOrUpdateEventListener implements CallbackHandlerConsumer {
	private EntityCallbackHandler callbackHandler;

	public void setCallbackHandler(EntityCallbackHandler callbackHandler) {
		this.callbackHandler = callbackHandler;
	}

	public EJB3SaveOrUpdateEventListener() {
		super();
	};

	public EJB3SaveOrUpdateEventListener(EntityCallbackHandler callbackHandler) {
		super();
		this.callbackHandler = callbackHandler;
	}

	@Override protected boolean invokeSaveLifecycle(Object entity, EntityPersister persister, EventSource source) {
		callbackHandler.preCreate( entity ); //always call the precreate event even if on safe vetoe it
		return super.invokeSaveLifecycle( entity, persister, source );
	}
}
