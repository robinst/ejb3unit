package com.bm;

import java.io.IOException;
import java.net.URL;

import junit.framework.TestCase;

public class ClasspathTest extends TestCase {

	private final org.apache.log4j.Logger logger = org.apache.log4j.Logger
			.getLogger(ClasspathTest.class);

	public void testSearchStringString() {
		try {
			URL[] url = Classpath.search("META-INF", "persistence.xml");
			logger
					.info("Found "
							+ url.length
							+ " matching instances of persistence.xml on the classpath");
			assertTrue("Looking for at least one class in the persistence.xml",
					url.length > 0);
			for (URL currentUrl : url) {
				logger.info("Found instances of persistence.xml "
						+ currentUrl.toString());
			}
		} catch (IOException e) {
			fail("Unable to find persistence.xml on the classpath" + e.toString());
		}
	}
}
