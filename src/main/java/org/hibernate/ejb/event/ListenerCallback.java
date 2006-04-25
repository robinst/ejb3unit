/*
 * JBoss, the OpenSource EJB server
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.hibernate.ejb.event;

import java.lang.reflect.Method;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import org.hibernate.util.ReflectHelper;

/**
 * @author <a href="mailto:kabir.khan@jboss.org">Kabir Khan</a>
 * @version $Revision: 1.1 $
 */
public class ListenerCallback extends Callback {
	protected transient Object listener;

	public ListenerCallback(Method callbackMethod, Object listener) {
		super( callbackMethod );
		this.listener = listener;
	}

	public void invoke(Object bean) {
		try {
			callbackMethod.invoke( listener, new Object[]{bean} );
		}
		catch (Exception e) {
			throw new RuntimeException( e );
		}
	}

	private void writeObject(ObjectOutputStream oos) throws IOException {
		oos.defaultWriteObject();
		oos.writeObject( listener.getClass().getName() );
	}

	private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException  {
		ois.defaultReadObject();
		String listenerClass = (String) ois.readObject();
		try {
			listener = ReflectHelper.classForName( listenerClass, this.getClass() ).newInstance();
		}
		catch (InstantiationException e) {
			throw new ClassNotFoundException( "Unable to load class:" + listenerClass, e);
		}
		catch (IllegalAccessException e) {
			throw new ClassNotFoundException( "Unable to load class:" + listenerClass, e);
		}
	}
}
