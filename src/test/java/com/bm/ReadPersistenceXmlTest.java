package com.bm;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

public class ReadPersistenceXmlTest extends TestCase {

	public void testGetClasses() throws Exception {
		Class<?>[] classesLoaded = PersistenceXml.getClasses("ejb3unit");
		assertTrue("Found/Loaded no classes derived from the persistence.xml",
				classesLoaded.length > 0);
		final Set<Class<?>> allLoaded = new HashSet<Class<?>>(Arrays
				.asList(classesLoaded));
		assertTrue(allLoaded.contains(com.bm.ejb3data.bo.NewsBo.class));
		assertTrue(allLoaded.contains(com.bm.ejb3data.bo.FullTimeEmployee1.class));
		assertTrue(allLoaded.contains(com.bm.ejb3data.bo.Choice.class));
		assertTrue(allLoaded.contains(com.bm.ejb3data.bo.Department.class));

	}

}
