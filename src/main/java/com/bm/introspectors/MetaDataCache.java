package com.bm.introspectors;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import com.bm.cfg.Ejb3UnitCfg;
import com.bm.creators.DynamicDIModuleCreator;
import com.bm.creators.MockedDIModuleCreator;
import com.bm.ejb3guice.inject.Module;
import com.bm.ejb3metadata.MetadataAnalyzer;
import com.bm.ejb3metadata.annotations.metadata.ClassAnnotationMetadata;
import com.bm.ejb3metadata.annotations.metadata.EjbJarAnnotationMetadata;

public final class MetaDataCache {

	/**
	 * Contains the meta data information off all jars.
	 */
	private static final List<EjbJarAnnotationMetadata> ALL_JARS = new ArrayList<EjbJarAnnotationMetadata>();

	/**
	 * Constructor.
	 */
	private MetaDataCache() {

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
			EntityManager manager, Class toCheck) {
		// ensure the metadata is resolved for this jar (where toCheck is in)
		getMetaData(toCheck);
		final DynamicDIModuleCreator dynamicDIModuleCreator = new DynamicDIModuleCreator(
				conf, manager);
		// add mappings to the creator
		for (EjbJarAnnotationMetadata currentJar : ALL_JARS) {
			dynamicDIModuleCreator.addInteface2ImplMap(currentJar
					.getInterface2implemantation());
		}
		return dynamicDIModuleCreator;
	}
	
	/**
	 * Returns the Module {@link Module} for guice dependency injection with
	 * valid ejb3mapping definition. Will inject mock controlls.
	 * 
	 * @param conf
	 *            the configuration
	 * @return the module creator.
	 */
	public static MockedDIModuleCreator getMockModuleCreator(Class toCheck) {
		// ensure the metadata is resolved for this jar (where toCheck is in)
		getMetaData(toCheck);
		final MockedDIModuleCreator mockedDIModuleCreator = new MockedDIModuleCreator();
		// add mappings to the creator
		for (EjbJarAnnotationMetadata currentJar : ALL_JARS) {
			mockedDIModuleCreator.addInteface2ImplMap(currentJar
					.getInterface2implemantation());
		}
		return mockedDIModuleCreator;
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
		for (EjbJarAnnotationMetadata currentJar : ALL_JARS) {
			Map<String, String> interface2implemantation = currentJar
					.getInterface2implemantation();
			implFound = interface2implemantation.get(name.replace('.', '/'));
		}

		if (implFound == null) {
			EjbJarAnnotationMetadata newJar = MetadataAnalyzer
					.initialize(toInspect);
			Map<String, String> interface2implemantation = newJar
					.getInterface2implemantation();
			implFound = interface2implemantation.get(name.replace('.', '/'));
			if (implFound != null) {
				ALL_JARS.add(newJar);
			} else {
				throw new IllegalArgumentException(
						"Can't find any meta data information for the class ("
								+ toInspect.getName() + ")");
			}
		}

		return implFound;
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
		ClassAnnotationMetadata classMeta = null;
		for (EjbJarAnnotationMetadata currentJar : ALL_JARS) {
			classMeta = currentJar.getClassAnnotationMetadata(toCheck.getName()
					.replace('.', '/'));
			if (classMeta != null) {
				// found
				break;
			}
		}

		if (classMeta == null) {
			// still not found
			EjbJarAnnotationMetadata newJar = MetadataAnalyzer
					.initialize(toCheck);
			classMeta = newJar.getClassAnnotationMetadata(toCheck.getName()
					.replace('.', '/'));
			if (classMeta == null) {
				throw new IllegalArgumentException(
						"Can't find any meta data information for the class ("
								+ toCheck.getName() + ")");
			} else {
				ALL_JARS.add(newJar);
			}
		}

		return classMeta;
	}

}
