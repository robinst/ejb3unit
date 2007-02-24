package com.bm.introspectors;

/**
 * Introspector for jboss service.
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
		super(toInspect, true);
		// TODO Check if is really a Jboss service: Annotation @Service

		// analyse the fields
		this.processAccessTypeField(toInspect);

	}

	/**
	 * Returns true is this intorspector accept this class.
	 * 
	 * @param toCheck -
	 *            to check
	 * @return true id the introspector will accept this class
	 */
	public static boolean accept(@SuppressWarnings("unused")
	Class toCheck) {
		// TODO Check if is really a Jboss service: Annotation @Service
		return true;
	}

}
