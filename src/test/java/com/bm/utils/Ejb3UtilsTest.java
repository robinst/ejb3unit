package com.bm.utils;

import java.util.Collection;
import java.util.List;

import junit.framework.TestCase;

/**
 * The Ejb3UtilsTest.java is responsible for testing the java ejb3utils
 */

public class Ejb3UtilsTest extends TestCase {
    
    public void testHasSuperclassOrInterface() {
        assertTrue(Ejb3Utils.hasSuperclassOrInterface(List.class, Collection.class));
        assertTrue(Ejb3Utils.hasSuperclassOrInterface(Object.class, Object.class));
        assertFalse(Ejb3Utils.hasSuperclassOrInterface(String.class, Collection.class));
        assertTrue(Ejb3Utils.hasSuperclassOrInterface(String.class, Object.class));
    }

}

