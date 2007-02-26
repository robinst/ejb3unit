package com.bm.ejb3metadata;

import com.bm.ejb3metadata.annotations.metadata.EjbJarAnnotationMetadata;

/**
 * The cache will hold in as an central place the current configuration. This
 * means all meta data informations will be avaiable here.
 * 
 * @author Daniel Wiese
 * 
 */
public final class MetaDataCache {

	private static final AnnonatedClassFinder classFinder = new AnnonatedClassFinder();

	/**
	 * Private Constructor - avoids instanniation
	 * 
	 */
	private MetaDataCache() {

	}
	
	/**
	 * Returns the meta data information of the current application.
	 * @param hint - one class of the current appication (used as hint for scanning)
	 * @return - EjbJarAnnotationMetadata
	 */
	public static synchronized EjbJarAnnotationMetadata getMeataData(Class hint){
		return classFinder.getResult(hint);
	}

}
