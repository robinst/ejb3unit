package com.bm.ejb3metadata;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.ejb3unit.asm.ClassReader;

import com.bm.ejb3metadata.annotations.analyzer.ScanClassVisitor;
import com.bm.ejb3metadata.annotations.exceptions.ResolverException;
import com.bm.ejb3metadata.annotations.helper.ResolverHelper;
import com.bm.ejb3metadata.annotations.metadata.ClassAnnotationMetadata;
import com.bm.ejb3metadata.annotations.metadata.EjbJarAnnotationMetadata;

/**
 * Analyse the matadate for ejb 3.
 * 
 * @author Daniel Wiese
 */
public final class MetadataAnalyzer {

	private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger
			.getLogger(MetadataAnalyzer.class);

	/**
	 * Defines java.lang.Object class.
	 */
	public static final String JAVA_LANG_OBJECT = "java/lang/Object";

	/**
	 * Creates an new enhancer.
	 * 
	 * @param loader
	 *            classloader where to define enhanced classes.
	 * @param ejbJarAnnotationMetadata
	 *            object with references to the metadata.
	 * @param map
	 *            a map allowing to give some objects to the enhancer.
	 */
	private MetadataAnalyzer() {

	}

	/**
	 * Allow to analyze a given set of classes.
	 * 
	 * @param loader
	 *            the classloader that will be used to load the classes.
	 * @param classesToAnalyze
	 *            the set of classes to analyze
	 * @param map
	 *            a map allowing to give some objects to the enhancer.
	 * @return die metadaten
	 * @throws ResolverException
	 *             in error case
	 */
	public static EjbJarAnnotationMetadata analyze(final ClassLoader loader,
			final List<String> classesToAnalyze, final Map<String, Object> map)
			throws ResolverException {
		EjbJarAnnotationMetadata ejbJarAnnotationMetadata = new EjbJarAnnotationMetadata();
		ScanClassVisitor scanVisitor = new ScanClassVisitor(
				ejbJarAnnotationMetadata);
		// logger.info("ClassLoader used = (" + loader + ")");
		for (String clazz : classesToAnalyze) {
			read(clazz, loader, scanVisitor, ejbJarAnnotationMetadata);
		}

		ResolverHelper.resolve(ejbJarAnnotationMetadata);
		ejbJarAnnotationMetadata.buildInterfaceImplementationMap();

		return ejbJarAnnotationMetadata;

	}

	/**
	 * Visits the given class and all available parent classes.
	 * 
	 * @param className
	 *            the class to visit.
	 * @param loader
	 *            the classloader to use.
	 * @param scanVisitor
	 *            the ASM visitor.
	 * @param ejbJarAnnotationMetadata
	 *            the structure containing class metadata
	 */
	private static void read(final String className, final ClassLoader loader,
			final ScanClassVisitor scanVisitor,
			final EjbJarAnnotationMetadata ejbJarAnnotationMetadata) {
		String readingClass = className;
		if (!className.toLowerCase().endsWith(".class")) {
			readingClass = className + ".class";
		}

		InputStream is = loader.getResourceAsStream(readingClass);

		try {
			new ClassReader(is).accept(scanVisitor, ClassReader.SKIP_CODE);
		} catch (IOException e) {
			throw new RuntimeException("Cannot read the given class '"
					+ className + "'.", e);
		}

		// get the class parsed
		ClassAnnotationMetadata classMetadata = ejbJarAnnotationMetadata
				.getClassAnnotationMetadata(className);
		String superClassName = classMetadata.getSuperName();
		if (!superClassName.equals(JAVA_LANG_OBJECT)) {
			read(superClassName, loader, scanVisitor, ejbJarAnnotationMetadata);
		}
		try {
			is.close();
		} catch (IOException e) {
			throw new RuntimeException(
					"Cannot close the input stream for class '" + className
							+ "'.", e);
		}

	}

}
