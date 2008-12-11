package com.bm.testsuite.fixture;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.persistence.PersistenceException;

import junit.framework.TestCase;
import junit.framework.TestFailure;
import junit.framework.TestResult;
import junit.framework.TestSuite;

import org.hibernate.AnnotationException;

import com.bm.datagen.Generator;
import com.bm.datagen.constant.ConstantIntegerGenerator;
import com.bm.ejb3data.bo.AnnotatedFieldsSessionBean;
import com.bm.ejb3data.bo.error.NewsBoDuplicateKey;
import com.bm.ejb3data.bo.error.NewsBoIncorrectQuery;
import com.bm.testsuite.BaseEntityFixture;
import com.bm.testsuite.BaseSessionBeanFixture;
import com.bm.testsuite.EntityInitializationException;
import com.bm.testsuite.PoJoFixture;

public class FixtureMessageTest extends TestCase {

	public void testInitilzationErrorBaseEntityFixture() {
		check(
				InitilzationErrorBaseEntityFixture.class,
				5,
				"Unable to build EntityManagerFactory",
				PersistenceException.class);
	}

	public void testInitilzationErrorPoJoFixture() {
		check(
				InitilzationErrorPoJoFixture.class,
				1,
				"Unable to build EntityManagerFactory",
				PersistenceException.class);
	}

	public void testInitilzationError2PoJoFixture() {
		check(
				InitilzationError2PoJoFixture.class,
				1,
				"No identifier specified for entity: com.bm.ejb3data.bo.error.NewsBoDuplicateKey",
				AnnotationException.class);
	}

	public void testInitilzationErrorBaseSessionBeanFixture() {
		check(
				InitilzationErrorBaseSessionBeanFixture.class,
				1,
				"Unable to build EntityManagerFactory",
				PersistenceException.class);
	}

	@SuppressWarnings("unchecked")
	private void check(Class<?> testClass, int errorCount, String errorMessage,
			Class<?> nestedException) {
		TestSuite suite = new TestSuite(testClass);
		TestResult result = new TestResult();
		suite.run(result);
		assertEquals(0, result.failureCount());
		List<TestFailure> errorList = convertToList(result.errors());
		assertEquals(errorCount, errorList.size());
		String errorBegin = "EntityManager could not be initialized to load the enities [";
		String errorEnde = "]. Cause: " + nestedException.getName() + ": "
				+ errorMessage;
		for (TestFailure testFailure : errorList) {
			assertEquals(EntityInitializationException.class, testFailure
					.thrownException().getClass());
			assertEquals(errorBegin, testFailure.thrownException().getMessage()
					.substring(0, errorBegin.length()));
                        
                        String message = testFailure.thrownException().getMessage();
                        String messageEnd = message.substring(message.indexOf("]. Cause:"));
			assertEquals(errorEnde, messageEnd);
			assertEquals(nestedException, testFailure.thrownException()
					.getCause().getClass());
		}

	}

	private List<TestFailure> convertToList(Enumeration<TestFailure> errorEnum) {

		List<TestFailure> errorList = new ArrayList<TestFailure>();

		while (errorEnum.hasMoreElements()) {
			errorList.add(errorEnum.nextElement());
		}
		return errorList;
	}

	public static class InitilzationErrorBaseEntityFixture extends
			BaseEntityFixture<NewsBoIncorrectQuery> {

		/**
		 * 
		 * Constructor.
		 */
		public InitilzationErrorBaseEntityFixture() {
			super(NewsBoIncorrectQuery.class,
					new Generator<?>[] { new ConstantIntegerGenerator(870737) });
		}

	}

	public static class InitilzationErrorPoJoFixture extends PoJoFixture {

		/**
		 * 
		 * Constructor.
		 */
		public InitilzationErrorPoJoFixture() {
			super(new Class<?>[] { NewsBoIncorrectQuery.class });
		}

	}

	public static class InitilzationError2PoJoFixture extends PoJoFixture {

		/**
		 * 
		 * Constructor.
		 */
		public InitilzationError2PoJoFixture() {
			super(new Class<?>[] { NewsBoDuplicateKey.class });
		}

	}

	public static class InitilzationErrorBaseSessionBeanFixture extends
			BaseSessionBeanFixture<AnnotatedFieldsSessionBean> {

		/**
		 * 
		 * Constructor.
		 */
		public InitilzationErrorBaseSessionBeanFixture() {
			super(AnnotatedFieldsSessionBean.class,
					new Class<?>[] { NewsBoIncorrectQuery.class });
		}

	}
}
