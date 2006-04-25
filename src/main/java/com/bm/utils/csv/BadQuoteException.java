package com.bm.utils.csv;

/**
 * Signals that binary data was encountered and continuing with a text operation
 * would likely corrupt the data.
 * 
 * @author Daniel Wiese
 * @since 17.04.2006
 */
public class BadQuoteException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructs an IOException with null as its error detail message.
	 * 
	 * @since ostermillerutils 1.02.16
	 */
	public BadQuoteException() {
		super();
	}

	/**
	 * Constructs an exception with the specified detail message. The error
	 * message string s can later be retrieved by the Throwable.getMessage()
	 * method of class java.lang.Throwable.
	 * 
	 * @param s
	 *            the detail message.
	 * 
	 * @since ostermillerutils 1.02.16
	 */
	public BadQuoteException(String s) {
		super(s);
	}
}
