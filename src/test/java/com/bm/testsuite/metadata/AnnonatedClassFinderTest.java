package com.bm.testsuite.metadata;

import junit.framework.TestCase;

import com.bm.ejb3metadata.annotations.metadata.ClassAnnotationMetadata;
import com.bm.ejb3metadata.annotations.metadata.EjbJarAnnotationMetadata;
import com.bm.ejb3metadata.annotations.metadata.FieldAnnotationMetadata;
import com.bm.ejb3metadata.finder.AnnonatedClassFinder;

/**
 * Junit test.
 * 
 * @author Daniel Wiese
 * 
 */
public class AnnonatedClassFinderTest extends TestCase {

	/**
	 * Testmethod.
	 * 
	 * @author Daniel Wiese
	 * @since 24.02.2007
	 */
	public void testFindAllMetadata() {
		final AnnonatedClassFinder clazzFinder = new AnnonatedClassFinder();
		EjbJarAnnotationMetadata metaData = clazzFinder.getResult(this
				.getClass());
		ClassAnnotationMetadata clzzMeta = metaData
				.getClassAnnotationMetadata("com/bm/testsuite/metadata/TestServiceBean");
		String impl = metaData
				.getBeanImplementationForInterface(IBusinessInterface.class);
		assertEquals(impl, "com/bm/testsuite/metadata/TestStatlessSessionBean");
		assertEquals(clzzMeta.getLocalInterfaces().getInterfaces().get(0),
				"com/bm/testsuite/metadata/IBusinessInterface3");
		assertEquals(clzzMeta.isService(), true);
		assertEquals(clzzMeta.getFieldAnnotationMetadataCollection().size(), 1);
		FieldAnnotationMetadata dependencyInj = clzzMeta
				.getFieldAnnotationMetadataCollection().iterator().next();
		assertEquals(dependencyInj.getJField().getName(), "reference");
		assertEquals(dependencyInj.getJField().getDescriptor(),
				"Lcom/bm/testsuite/metadata/IBusinessInterface2;");

	}

}
