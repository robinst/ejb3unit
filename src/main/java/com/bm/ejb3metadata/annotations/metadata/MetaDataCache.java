package com.bm.ejb3metadata.annotations.metadata;

import java.util.Map;

import javax.persistence.EntityManager;

import com.bm.cfg.Ejb3UnitCfg;
import com.bm.creators.DynamicDIModuleCreator;
import com.bm.creators.MockedDIModuleCreator;
import com.bm.ejb3guice.inject.Module;
import com.bm.ejb3metadata.MetadataAnalyzer;

public final class MetaDataCache {

	/**
	 * Contains the meta data information off all jars. GuardedBy MetaDataCache
	 * class lock.
	 */
	private static final EjbJarAnnotationMetadata META_DATA = new EjbJarAnnotationMetadata();

	/**
	 * Constructor.
	 */
	private MetaDataCache() {

	}

	/**
	 * Adds explicit matadate form annotated classes to the ejb3unit framework
	 * @param classes the list of classes
	 */
	public static void addClasses(String... classes) {
		for (String s : classes) {
			EjbJarAnnotationMetadata newJar = MetadataAnalyzer.initialize(s);
			mergeJarIntoCache(newJar);
		}
	}

	/**
	 * Returns the implementation for the class
	 * 
	 * @author Daniel Wiese
	 * @since Jul 19, 2007
	 * @param toInspect
	 *            the interface.
	 * @return the implementation
	 */
	public static String getBeanImplementationForInterface(Class<?> toInspect) {
		getMetaData(toInspect);
		final String name = toInspect.getName();
		String implFound = null;
		// add mappings to the creator
		Map<String, String> interface2implemantation = getInterface2implemantation();
		implFound = interface2implemantation.get(name.replace('.', '/'));

		if (implFound == null) {
			EjbJarAnnotationMetadata newJar = MetadataAnalyzer.initialize(toInspect);
			interface2implemantation = newJar.getInterface2implemantation();
			implFound = interface2implemantation.get(name.replace('.', '/'));
			if (implFound != null) {
				mergeJarIntoCache(newJar);
			} else {
				throw new IllegalArgumentException(
						"Can't find any meta data information for the class ("
								+ toInspect.getName() + ")");
			}
		}

		return implFound;
	}

	/**
	 * Returns the {@link ClassAnnotationMetadata} for the given class name
	 * 
	 * @param className
	 *            The name of the class in the form com.bla.class
	 * @return Metadata if present otherwise NULL
	 */
	public static synchronized ClassAnnotationMetadata getClassAnnotationMetaData(String className) {
		return META_DATA.getClassAnnotationMetadata(className);
	}

	/**
	 * Returns the Module {@link Module} for guice dependency injection with
	 * valid ejb3mapping definition.
	 * 
	 * @param conf
	 *            the configuration
	 * @param manager
	 *            the entity manager instance which should be used for the
	 *            binding
	 * @param toCheck
	 *            the class wich identifyes the module
	 * @return the module creator.
	 */
	public static DynamicDIModuleCreator getDynamicModuleCreator(Ejb3UnitCfg conf,
			EntityManager manager,
			Class toCheck) {
		// ensure the metadata is resolved for this jar (where toCheck is in)
		getMetaData(toCheck);
		final DynamicDIModuleCreator dynamicDIModuleCreator = new DynamicDIModuleCreator(conf,
				manager);
		// add mappings to the creator
		dynamicDIModuleCreator.addInteface2ImplMap(getInterface2implemantation());

		return dynamicDIModuleCreator;
	}

	/**
	 * Returns the metadate mapping for the jar where the class file is in.
	 * 
	 * @author Daniel Wiese
	 * @since Jul 19, 2007
	 * @param toCheck
	 *            the class wich identifyes the module
	 * @return metat data
	 */
	public static ClassAnnotationMetadata getMetaData(Class toCheck) {
		ClassAnnotationMetadata classMeta = getClassAnnotationMetaData(toCheck.getName().replace(
				'.', '/'));

		if (classMeta == null) {
			// still not found
			EjbJarAnnotationMetadata newJar = MetadataAnalyzer.initialize(toCheck);
			classMeta = newJar.getClassAnnotationMetadata(toCheck.getName().replace('.', '/'));
			if (classMeta == null) {
				throw new IllegalArgumentException(
						"Can't find any meta data information for the class (" + toCheck.getName()
								+ ")");
			} else {
				mergeJarIntoCache(newJar);
			}
		}

		return classMeta;
	}

	/**
	 * Returns the Module {@link Module} for guice dependency injection with
	 * valid ejb3mapping definition. Will inject mock controlls.
	 * 
	 * @param toCheck
	 *            the class to check
	 * @return the module creator.
	 */
	public static MockedDIModuleCreator getMockModuleCreator(Class toCheck) {
		// ensure the metadata is resolved for this jar (where toCheck is in)
		getMetaData(toCheck);
		final MockedDIModuleCreator mockedDIModuleCreator = new MockedDIModuleCreator();
		// add mappings to the creator
		mockedDIModuleCreator.addInteface2ImplMap(getInterface2implemantation());
		return mockedDIModuleCreator;
	}

	private static synchronized Map<String, String> getInterface2implemantation() {
		return META_DATA.getInterface2implemantation();
	}

	private static synchronized void mergeJarIntoCache(EjbJarAnnotationMetadata ejbJarAnnotationMetadata) {
		META_DATA.mergeClassAnnotationMetadata(ejbJarAnnotationMetadata);
	}

}
