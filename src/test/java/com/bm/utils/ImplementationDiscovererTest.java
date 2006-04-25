package com.bm.utils;

import junit.framework.TestCase;

import com.bm.datagen.Generator;

/**
 * Test.
 * 
 * @author Daniel Wiese
 * @since 12.11.2005
 */
public class ImplementationDiscovererTest extends TestCase {

	/**
	 * Testmethod.
	 * 
	 * @author Daniel Wiese
	 * @since 13.11.2005
	 */
	public void testFind() {

		ImplementationDiscoverer dc = new ImplementationDiscoverer();
		System.out.println("Implementaion of Generator: "
				+ dc.findImplementation(Generator.class));
	}

}
