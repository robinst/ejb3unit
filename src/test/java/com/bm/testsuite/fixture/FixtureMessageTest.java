package com.bm.testsuite.fixture;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import junit.framework.TestCase;
import junit.framework.TestFailure;
import junit.framework.TestResult;
import junit.framework.TestSuite;

import com.bm.datagen.Generator;
import com.bm.datagen.constant.ConstantIntegerGenerator;
import com.bm.ejb3data.bo.AnnotatedFieldsSessionBean;
import com.bm.ejb3data.bo.NewsBoIncorrect;
import com.bm.testsuite.BaseEntityFixture;
import com.bm.testsuite.BaseSessionBeanFixture;
import com.bm.testsuite.EntityInitializationException;
import com.bm.testsuite.PoJoFixture;

public class FixtureMessageTest extends TestCase {

	public void testInitilzationErrorBaseEntityFixture() {
		check(InitilzationErrorBaseEntityFixture.class, 5);
	}

	public void testInitilzationErrorPoJoFixture() {
		check(InitilzationErrorPoJoFixture.class, 1);
	}
	public void testInitilzationErrorBaseSessionBeanFixture() {
		check(InitilzationErrorBaseSessionBeanFixture.class, 1);
	}
	
	
	@SuppressWarnings("unchecked")
	private void check(Class<?> clazz, int methodCount) {
		TestSuite suite = new TestSuite(clazz);
		TestResult result = new TestResult();
		suite.run(result);
		Enumeration<TestFailure> errorEnum = result.errors();
		List<TestFailure> errorList = new ArrayList<TestFailure>();

		while (errorEnum.hasMoreElements()) {
			errorList.add(errorEnum.nextElement());
		}

		assertEquals(methodCount, errorList.size());
		for (TestFailure testFailure : errorList) {
			assertEquals(EntityInitializationException.class, testFailure
					.thrownException().getClass());
		}
	}

	public static class InitilzationErrorBaseEntityFixture extends
			BaseEntityFixture<NewsBoIncorrect> {

		/**
		 * 
		 * Constructor.
		 */
		public InitilzationErrorBaseEntityFixture() {
			super(NewsBoIncorrect.class,
					new Generator<?>[] { new ConstantIntegerGenerator(870737) });
		}

	}

	public static class InitilzationErrorPoJoFixture extends PoJoFixture {

		/**
		 * 
		 * Constructor.
		 */
		public InitilzationErrorPoJoFixture() {
			super(new Class<?>[] { NewsBoIncorrect.class });
		}

	}
	
	public static class InitilzationErrorBaseSessionBeanFixture extends BaseSessionBeanFixture<AnnotatedFieldsSessionBean> {

		/**
		 * 
		 * Constructor.
		 */
		public InitilzationErrorBaseSessionBeanFixture() {
			super(AnnotatedFieldsSessionBean.class,new Class<?>[] { NewsBoIncorrect.class });
		}

	}
}
