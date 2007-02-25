package com.bm.ejb3metadata;

import java.io.File;
import java.net.URL;

import com.bm.ejb3metadata.annotations.analyzer.AnnotationDeploymentAnalyzer;
import com.bm.ejb3metadata.annotations.exceptions.AnalyzerException;
import com.bm.ejb3metadata.annotations.exceptions.ResolverException;
import com.bm.ejb3metadata.annotations.helper.ResolverHelper;
import com.bm.ejb3metadata.annotations.metadata.EjbJarAnnotationMetadata;
import com.bm.ejb3metadata.utils.MetadataUtils;

/**
 * Scann a given archive or folder for annotated classes.
 * 
 * @author Daniel Wiese
 * 
 */
public class AnnonatedClassFinder {

	private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger
			.getLogger(AnnonatedClassFinder.class);

	/**
	 * Annotation deployment analyzer.
	 */
	private AnnotationDeploymentAnalyzer annotationDeploymentAnalyzer = null;

	private boolean isInitailized = false;

	/**
	 * Constructor.
	 */
	public AnnonatedClassFinder() {

	}

	/**
	 * Searches all classes in a given directory (up to top lever and from there
	 * recursive) of in a given jar file.
	 * 
	 * @param firstSearchClue -
	 *            the location of this class will be used as a hint , where to
	 *            start with the search
	 * @return - a EjbJarAnnotationMetadata objetc with list of classes (format:
	 *         com/siemens/MyClass)
	 */
	public EjbJarAnnotationMetadata getResult(Class firstSearchClue) {
		if (!isInitailized) {
			this.init(firstSearchClue);
		}
		return this.annotationDeploymentAnalyzer.getEjbJarAnnotationMetadata();
	}

	private void init(Class firstSearchClue) {
		File rootDirOrArchive = this.getJarFileOrRootDirectory(firstSearchClue);
		this.annotationDeploymentAnalyzer = new AnnotationDeploymentAnalyzer(
				rootDirOrArchive);
		try {
			annotationDeploymentAnalyzer.analyze();
			ResolverHelper.resolve(annotationDeploymentAnalyzer
					.getEjbJarAnnotationMetadata());
		} catch (ResolverException e) {
			logger.error("Error: ", e);
			throw new RuntimeException(e);
		} catch (AnalyzerException e) {
			logger.error("Error: ", e);
			throw new RuntimeException(e);
		}

		annotationDeploymentAnalyzer.getEjbJarAnnotationMetadata()
				.buildInterfaceImplementationMap();
		this.isInitailized = true;
	}

	private File getJarFileOrRootDirectory(Class firstSearchClue) {
		File back = null;
		// Translate the package name into an absolute path
		boolean isLoadInJar = false;
		String name = firstSearchClue.getName().replace('.', '/');
		URL loc = firstSearchClue.getResource("/" + name + ".class");

		File f = new File(loc.getFile());
		// Class file is inside a jar file.
		if (f.getPath().startsWith("file:")) {
			String s = f.getPath();
			int index = s.indexOf('!');
			// It confirm it is a jar file
			if (index != -1) {
				isLoadInJar = true;
			}
		}

		if (!isLoadInJar) {
			File rootDirectory = MetadataUtils.getRootPackageDir(f,
					firstSearchClue.getName());
			back = rootDirectory;
			// generate interface-impl map
		} else {
			String jarName = MetadataUtils.isolateJarName(loc);
			// windows bug with spaces
			jarName = jarName.replaceAll("\\%20", " ");
			back = new File(jarName);
		}

		return back;
	}

}
