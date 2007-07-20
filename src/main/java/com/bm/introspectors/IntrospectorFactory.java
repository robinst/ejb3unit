package com.bm.introspectors;

public final class IntrospectorFactory {

	/**
	 * Constructor.
	 */
	private IntrospectorFactory() {
	}

	/**
	 * returns the right introspector.
	 * 
	 * @author Daniel Wiese
	 * @since Jul 19, 2007
	 * @param forClass
	 *            the right one for the class
	 * @return the introspector for the class.
	 */
	@SuppressWarnings("unchecked")
	public static AbstractIntrospector<?> createIntrospector(Class forClass) {
		if (SessionBeanIntrospector.accept(forClass)) {
			return new SessionBeanIntrospector<Object>(forClass);
		} else if (MDBIntrospector.accept(forClass)) {
			return new MDBIntrospector<Object>(forClass);
		} else if (JbossServiceIntrospector.accept(forClass)) {
			return new JbossServiceIntrospector<Object>(forClass);
		}

		throw new IllegalArgumentException("Introspector for class ("
				+ forClass + ") not found");
	}
}
