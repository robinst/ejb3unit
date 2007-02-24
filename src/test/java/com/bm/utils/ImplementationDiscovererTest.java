package com.bm.utils;

import junit.framework.TestCase;

import org.apache.commons.collections.Bag;

import com.bm.datagen.Generator;

/**
 * Test.
 * 
 * @author Daniel Wiese
 * @since 12.11.2005
 */
public class ImplementationDiscovererTest extends TestCase {

	private static final org.apache.log4j.Logger log = org.apache.log4j.Logger
			.getLogger(ImplementationDiscovererTest.class);

	/**
	 * Testmethod.
	 * 
	 * @author Daniel Wiese
	 * @since 13.11.2005
	 */
	public void testFind() {

		ImplementationDiscoverer dc = new ImplementationDiscoverer();
		log.debug("Implementaion of Generator: "
				+ dc.findImplementation(Generator.class));
	}

	/**
	 * Testmethod.
	 * 
	 * @author Daniel Wiese
	 * @since 13.11.2005
	 */
	public void testFindInJar() {

		ImplementationDiscoverer dc = new ImplementationDiscoverer();
		log.debug("Implementaion of BeanMap: "
				+ dc.findImplementation(Bag.class));
	}

}
