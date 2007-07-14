package com.bm.ejb3metadata;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import org.ejb3unit.asm.ClassReader;

import com.bm.cfg.Ejb3UnitCfg;
import com.bm.ejb3guice.inject.Module;
import com.bm.ejb3metadata.annotations.analyzer.ScanClassVisitor;
import com.bm.ejb3metadata.annotations.exceptions.ResolverException;
import com.bm.ejb3metadata.annotations.helper.ResolverHelper;
import com.bm.ejb3metadata.annotations.metadata.ClassAnnotationMetadata;
import com.bm.ejb3metadata.annotations.metadata.EjbJarAnnotationMetadata;
import com.bm.ejb3metadata.finder.ClassFinder;

/**
 * Analyse the matadate for ejb 3.
 * 
 * @author Daniel Wiese
 */
public final class MetadataAnalyzer {

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
	 * Returns the Module with all bindings for the jar/project where toInspect is in.
	 * @param toInspect the hint which ja/project should be scanned
	 * @param configuration the configuration
	 * @param manager the entity manager instance which should be used for the binding
	 * @return the configuration the current ejb3unit configuration
	 */
	public static synchronized Module getGuiceBindingModule(Class<?> toInspect, Ejb3UnitCfg configuration, EntityManager manager){
		 EjbJarAnnotationMetadata metadata = initialize(toInspect);
		 return metadata.getDynamicModuleCreator(configuration, manager);
	}

	
	public static synchronized EjbJarAnnotationMetadata initialize(
			Class<?> toInspect) {
		EjbJarAnnotationMetadata toReturn = null;
		final ClassFinder finder = new ClassFinder();
		final List<String> classes = finder.getListOfClasses(toInspect);
		try {
			toReturn = MetadataAnalyzer.analyze(Thread.currentThread()
					.getContextClassLoader(), classes, null);
		} catch (ResolverException e) {
			throw new RuntimeException("Class (" + toInspect.getName()
					+ ") can´t be resolved");
		}
		return toReturn;
	}

	/**
	 * Allow to analyze a given set of classes.
	 * 
	 * @param loader
	 *            the classloader that will be used to load the classes.
	 * @param classesToAnalyze
	 *            the set of classes to analyze.
	 * @param map
	 *            a map allowing to give some objects to the enhancer.
	 * @return die metadaten
	 * @throws ResolverException
	 *             in error case
	 */
	private static EjbJarAnnotationMetadata analyze(final ClassLoader loader,
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
