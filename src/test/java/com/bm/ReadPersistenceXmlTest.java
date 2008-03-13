package com.bm;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import org.apache.log4j.Logger;

public class ReadPersistenceXmlTest extends TestCase {

	private static final Logger logger = Logger.getLogger(ReadPersistenceXmlTest.class);

	public void testGetClasses() throws IOException {
		try {
			Class<?>[] classesLoaded = PersistenceXml.getClasses();
			assertTrue("Found/Loaded no classes derived from the persistence.xml",
					classesLoaded.length > 0);
			final Set<Class<?>> allLoaded = new HashSet<Class<?>>(Arrays.asList(classesLoaded));
			assertTrue(allLoaded.contains(com.bm.ejb3data.bo.NewsBo.class));
			assertTrue(allLoaded.contains(com.bm.ejb3data.bo.FullTimeEmployee1.class));
			assertTrue(allLoaded.contains(com.bm.ejb3data.bo.Choice.class));
			assertTrue(allLoaded.contains(com.bm.ejb3data.bo.Department.class));
		} catch (Exception e) {
			logger.error("Oh no, something bad has happened reading the persistence.xml", e);
		}
	}

}
