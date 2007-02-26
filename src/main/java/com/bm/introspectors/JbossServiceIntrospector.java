package com.bm.introspectors;

/**
 * Marker class :Introspector for jboss service.
 * 
 * @author Daniel Wiese
 * @param <T> -
 *            teh type of the service
 * @since 12.11.2005
 */
public class JbossServiceIntrospector<T> extends SessionBeanIntrospector<T> {

	/**
	 * Constructor.
	 * 
	 * @param toInspect -
	 *            the sesion bean to inspect
	 */
	public JbossServiceIntrospector(Class<? extends T> toInspect) {
		super(toInspect);
	}

}
