package com.bm.testsuite;

/**
 * Indicates that the entity manager could not be inialized.
 * 
 * @author Fabian Bauschulte
 * 
 */
public class EntityInitializationException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	/**
	 * @param message
	 * @param nested
	 */
	public EntityInitializationException(String message, Throwable nested) {
		super(analyzeMessage(message.trim(), nested), nested);

	}

	private static String analyzeMessage(String message, Throwable nested) {

		if (nested != null) {
			message += " Cause: " + nested.getClass().getName() + ": "
					+ nested.getMessage();
		}

		return message;
	}
}
