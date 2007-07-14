package com.bm.testsuite.metadata;

import junit.framework.TestCase;

import com.bm.ejb3metadata.MetadataAnalyzer;
import com.bm.ejb3metadata.annotations.exceptions.ResolverException;
import com.bm.ejb3metadata.annotations.metadata.ClassAnnotationMetadata;
import com.bm.ejb3metadata.annotations.metadata.EjbJarAnnotationMetadata;
import com.bm.ejb3metadata.annotations.metadata.FieldAnnotationMetadata;

/**
 * Testclass.
 * 
 * @author Daniel Wiese
 * @since 24.02.2007
 */
public class MetadataAnalyzerTest extends TestCase {

	/**
	 * Testmethod.
	 * 
	 * @throws ResolverException
	 */
	public void testAnalyzeMataData() throws ResolverException {
		// classes.add("com/siemens/ct/ejb3metadata/TestStatlessSessionBean");
		// classes.add("com/siemens/ct/ejb3metadata/TestStetlessSessionBean2");
		EjbJarAnnotationMetadata metadata = MetadataAnalyzer.initialize(TestStatlessSessionBean.class);
		ClassAnnotationMetadata clzzMeta = metadata
				.getClassAnnotationMetadata("com/bm/testsuite/metadata/TestServiceBean");
		String impl = metadata
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
