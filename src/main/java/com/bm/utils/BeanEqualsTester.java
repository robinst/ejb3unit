package com.bm.utils;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import junit.framework.Assert;

import org.apache.log4j.Logger;

import com.bm.introspectors.Introspector;
import com.bm.introspectors.Property;

/**
 * Helper method to test the equality of beans.
 * 
 * @author Daniel Wiese
 * 
 */
public final class BeanEqualsTester extends Assert {

	private static final Logger log = Logger.getLogger(BeanEqualsTester.class);

	private BeanEqualsTester() {
		// intenionally left emty
	}

	/**
	 * Test the collection for the size (if not equal a runntime excetion is.
	 * thrown)
	 * 
	 * @param <T> -
	 *            the type of the bean
	 * 
	 * @param original -
	 *            the original collsecion ob beans
	 * @param readed -
	 *            the readed collection from the DB
	 */
	public static <T> void testEqualsOnSize(List<T> original, List<T> readed) {

		if (original != null && readed != null) {
			if (original.size() != readed.size()) {
				throw new RuntimeException(
						"Bean-Read-test: Not all/too many beans reded from the DB");
			}
		} else {
			throw new RuntimeException(
					"Bean-Read-test: One of the collections is null");
		}

	}

	/**
	 * Test the collection for equality on persistent fields (if not equal a
	 * runntime excetion is thrown).
	 * 
	 * @param <T> -
	 *            the type of the bean
	 * 
	 * @param original -
	 *            the original collsecion ob beans
	 * @param readed -
	 *            the readed collection from the DB
	 * @param intro -
	 *            the introspector
	 */
	public static <T> void testEqualsOnPersistentFields(List<T> original,
			List<T> readed, Introspector<T> intro) {

		testEqualsOnSize(original, readed);

		for (int i = 0; i < original.size(); i++) {
			T origB = original.get(i);
			T readB = readed.get(i);

			for (Property akt : intro.getPersitentFields()) {
				try {
					Object valueOrig = intro.getField(origB, akt);
					Object valueRead = intro.getField(readB, akt);
					if (valueOrig == null && valueRead == null) {
						// both are null >ok
						log.debug("Both values are null> field:"
								+ akt.getName());
					} else {
						if (valueOrig == null) {
							fail("The DB value is not null");
						} else if (valueRead == null) {
							fail("The DB value is null");
						} else if (valueOrig instanceof Collection
								&& valueRead instanceof Collection) {
							// test collections independend from the order
							testColletionsForEqual(
									(Collection<? extends Object>) valueOrig,
									(Collection<? extends Object>) valueRead);

						} else if (!valueOrig.equals(valueRead)) {
							// usual equals failed
							if (valueOrig instanceof String) {
								printErrorForUneqalStrings((String) valueOrig,
										(String) valueRead);
							}
							fail("The values of the field (" + akt.getName()
									+ ") are not equal");
						}
					}
				} catch (IllegalAccessException e) {
					throw new RuntimeException(
							"Can´t read the value of a field");
				}

			}
		}

	}

	/**
	 * Assert that the two collections are the same irrespective of order.
	 * 
	 * @param firstColl
	 *            The first collection
	 * @param secondColl
	 *            The second collection
	 * @param <T> -
	 *            the type of the collection
	 */
	public static <T> void testColletionsForEqual(
			Collection<? extends T> firstColl,
			Collection<? extends T> secondColl) {
		if (firstColl == null || secondColl == null) {
			assertNull("Second collection is null, first not", firstColl);
			assertNull("First collection is null, second not", secondColl);
		} else if (firstColl.size() == secondColl.size()) {
			final Collection<T> copyOfSecond = new LinkedList<T>(secondColl);
			// check if all elements of a are in b and vice versa.
			final Iterator iterator = firstColl.iterator();
			while (iterator.hasNext()) {
				final Object object = iterator.next();
				if (!copyOfSecond.contains(object)) {
					fail("Object ("
							+ object
							+ ") is missing in second collection (second parameter)");
				}

				copyOfSecond.remove(object);
			}

			if (!copyOfSecond.isEmpty()) {
				fail("Second collection has elements that aren't in the first collection: "
						+ copyOfSecond);
			}
		} else {
			fail("The size of the collections is not equal, fist size ("
					+ firstColl.size() + "), second size (" + secondColl.size()
					+ ")");
		}
	}

	/**
	 * Test the collection for equality by calling the equals method (if not
	 * equal a runntime excetion is thrown).
	 * 
	 * 
	 * 
	 * The preconditions are:
	 * We expect that the list do not contain duplicate elements - every element should 
	 * represent a different row in the database!<br>
	 * Additional preconditions are:<br>
	 * - 2 Entitybeans representing different database-rows are NEVER equal. <br>
	 * - Every bean instance representing the same DB row is always equal. <br> 
	 * - 2 equal beans MUST have the same hash code.<br> 
	 * - N bans (e.g 30) should have different hash codes.<br>
	 * 
	 * @param <T> -
	 *            the type of the bean
	 * 
	 * @param original -
	 *            the original collection ob beans
	 * @param readed -
	 *            the readed collection from the DB
	 */
	public static <T> void testEqualsImplementationForEntityBeans(List<T> original,
			List<T> readed) {

		int firstHashCode = 0;
		boolean differentHashCode = true;
		// make the hashcode test only with minimum 30 elements
		if (original.size() > 30) {
			firstHashCode = original.get(0).hashCode();
			differentHashCode = false;
		}

		for (int i = 0; i < original.size(); i++) {
			T origB = original.get(i);
			T readB = readed.get(i);

			// thest the equals method
			if (!(origB.equals(readB) && readB.equals(origB))) {
				log.error("The implementation of the equals Method is wrong");
				log.error("Two Entity-Beans (" + origB.getClass().getName()
						+ ") representing the same row should be equal");
				fail("The implementation of the eqals method is wrong");
			}
			// test the hash code
			if (origB.hashCode() != readB.hashCode()) {
				log
						.error("The implementation of the hash-code Method is wrong");
				log
						.error("Two Entity-Beans ("
								+ origB.getClass().getName()
								+ ") representing the same row should have the same hash-code");
				fail("The implementation of the hash-code method is wrong");

			}

			// to avoid n-square complexity compare the n bean with the n+1 bean
			if (i + 1 < original.size()) {
				T nextOrigB = original.get(i + 1);
				T nextReadB = readed.get(i + 1);

				if ((origB.equals(nextReadB) || readB.equals(nextOrigB))) {
					log
							.error("The implementation of the equals Method is wrong");
					log
							.error("Two Entity-Beans ("
									+ origB.getClass().getName()
									+ ") representing the different row should NEVER be equal");
					fail("The implementation of the eqals method is wrong");
				}
			}

			if (!differentHashCode && origB.hashCode() != firstHashCode) {
				// different hash code found
				differentHashCode = true;
			}
		}

		// throw a exception if all hash codes are the same
		if (!differentHashCode) {
			fail("All tested beans have the same hash code!");
		}

	}

	private static void printErrorForUneqalStrings(String origin, String other) {
		log.error("The readed string from the database "
				+ "is not Eqal to the origin");
		log.error("Origin : " + origin);
		log.error("From DB: " + other);
		log.error("-----------------------------------");
		log.error("Origin Length : " + origin.length());
		log.error("From DB Length: " + other.length());

	}

}
