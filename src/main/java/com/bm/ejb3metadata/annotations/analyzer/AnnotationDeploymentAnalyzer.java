package com.bm.ejb3metadata.annotations.analyzer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import org.ejb3unit.asm.ClassReader;

import com.bm.ejb3metadata.annotations.exceptions.AnalyzerException;
import com.bm.ejb3metadata.annotations.metadata.EjbJarAnnotationMetadata;

/**
 * This class finds the annotated class and fill metadata class.
 * 
 * @author Daniel Wiese
 */
public class AnnotationDeploymentAnalyzer {

	private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger
			.getLogger(AnnotationDeploymentAnalyzer.class);

	/**
	 * Archive which will be analyzed.
	 */
	private File archive = null;

	/**
	 * Metadata representing the parsed ejb-jar file.
	 */
	private EjbJarAnnotationMetadata ejbJarAnnotationMetadata = null;

	/**
	 * ASM visitor used to visit classes.
	 */
	private ScanClassVisitor scanVisitor = null;

	/**
	 * Constructor.<br>
	 * Archive which will be used when analyzing.
	 * 
	 * @param archive
	 *            the archive to analyze.
	 */
	public AnnotationDeploymentAnalyzer(final File archive) {
		this.archive = archive;
		this.ejbJarAnnotationMetadata = new EjbJarAnnotationMetadata();
		scanVisitor = new ScanClassVisitor(ejbJarAnnotationMetadata);
	}

	/**
	 * Analyzes the archive and fill metadata struct.
	 * 
	 * @throws AnalyzerException
	 *             if analyze of archive fails
	 */
	public void analyze() throws AnalyzerException {

		// two cases :
		// - archive is a jar file
		// - archive is an exploded directory

		if (archive.isFile()) {
			scanJarArchive();
		} else {
			// exploded mode
			// need recursion on all directories of the archive
			scanDirectory(archive);
		}
	}

	/**
	 * Scan all classes from the archive which is a jar file.
	 * 
	 * @throws AnalyzerException
	 *             if scan is aborted.
	 */
	private void scanJarArchive() throws AnalyzerException {
		JarFile jarFile;
		try {
			jarFile = new JarFile(archive);
		} catch (IOException ioe) {
			throw new AnalyzerException("Cannot build jar file on archive '"
					+ archive + "'.", ioe);
		}
		Enumeration<? extends ZipEntry> en = jarFile.entries();
		while (en.hasMoreElements()) {
			ZipEntry e = en.nextElement();
			String name = e.getName();
			// iterate through the jar file
			if (name.toLowerCase().endsWith(".class")) {
				try {
					new ClassReader(jarFile.getInputStream(e)).accept(
							scanVisitor, ClassReader.SKIP_CODE);
				} catch (Exception ioe) {
					throw new AnalyzerException(
							"Error while analyzing file entry '" + name
									+ "' in jar file '" + archive + "'", ioe);
				}

			}
		}
		try {
			jarFile.close();
		} catch (IOException ioe) {
			logger.warn("Error while trying to close the file '" + jarFile
					+ "'", ioe);
		}

	}

	/**
	 * Scan (recursively) all classes from the given directory. It starts with
	 * the archive root directory first.
	 * 
	 * @param directory
	 *            the directory in which proceed all classes.
	 * @throws AnalyzerException
	 *             if analyzer fails to scan classes.
	 */
	private void scanDirectory(final File directory) throws AnalyzerException {
		File[] files = directory.listFiles();
		for (File f : files) {
			// scan .class file
			if (f.isFile() && f.getName().toLowerCase().endsWith(".class")) {
				InputStream is = null;
				try {
					is = new FileInputStream(f);
				} catch (FileNotFoundException e) {
					throw new AnalyzerException(
							"Cannot read input stream from '"
									+ f.getAbsolutePath() + "'.", e);
				}
				try {
					new ClassReader(is).accept(scanVisitor,
							ClassReader.SKIP_CODE);
				} catch (Exception e) {
					throw new AnalyzerException(
							"Cannot launch class visitor on the file '"
									+ f.getAbsolutePath() + "'.", e);
				}
				try {
					is.close();
				} catch (IOException e) {
					throw new AnalyzerException(
							"Cannot close input stream on the file '"
									+ f.getAbsolutePath() + "'.", e);
				}
			} else {
				// loop on childs
				if (f.isDirectory()) {
					scanDirectory(f);
				}
			}
		}
	}

	/**
	 * struct (metadata) filled by this analyzer.
	 * 
	 * @return struct (metadata) filled by this analyzer
	 */
	public EjbJarAnnotationMetadata getEjbJarAnnotationMetadata() {
		return ejbJarAnnotationMetadata;
	}

}
