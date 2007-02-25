package javax.xml.rpc.handler;

import java.util.Iterator;

public interface MessageContext {
	boolean containsProperty(String name);

	Object getProperty(String name);

	Iterator getPropertyNames();

	void removeProperty(String name);

	void setProperty(String name, Object value);
}
