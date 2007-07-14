package com.bm.ejb3metadata.finder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import com.bm.ejb3metadata.utils.MetadataUtils;

/**
 * This class discover implementations for interfaces. The current implementation
 * takes "is happy" with one possible impelmentation. If a interface has two
 * implementations it´s currently not defined which one if going to be returned
 * 
 * @author Daniel Wiese
 * @since 13.11.2005
 */
public class ClassFinder {

	private static final Logger log = Logger.getLogger(ClassFinder.class);

	private final Set<String> allClasses = new HashSet<String>();

	private boolean isInitailized = false;

	/**
	 * Searches all classer in a given directory (up to top lever and from there
	 * recursive) of in a given jar file.
	 * 
	 * @param firstSearchClue -
	 *            the location of this class will be used as a hint , where to
	 *            start with the search
	 * @return - a list of classes (format: com/siemens/MyClass)
	 */
	public List<String> getListOfClasses(Class firstSearchClue) {
		if (!isInitailized) {
			this.init(firstSearchClue);
		}
		return new ArrayList<String>(allClasses);
	}

	private void init(Class toFind) {
		// Translate the package name into an absolute path
		boolean isLoadInJar = false;
		String name = toFind.getName().replace('.', '/');
		URL loc = toFind.getResource("/" + name + ".class");

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
			File rootDirectory = MetadataUtils.getRootPackageDir(f, toFind
					.getName());
			findClassesRecursive("", rootDirectory);
			// generate interface-impl map
		} else {
			findImplementationInJarFiles(loc);
		}
	}

	private void findImplementationInJarFiles(URL locationInJar) {
		String jarName = MetadataUtils.isolateJarName(locationInJar);
		// windows bug with spaces
		jarName = jarName.replaceAll("\\%20", " ");
		try {
			InputStream input = new FileInputStream(jarName);

			List<String> back = MetadataUtils.scanFileNamesInArchive(input);
			input.close();
			this.findClassesFromList(back);
			// generate interface-impl map

		} catch (FileNotFoundException e) {
			log.error("File not found", e);
			throw new IllegalArgumentException("Cant find implementation");
		} catch (IOException e) {
			log.error("File not found", e);
			throw new IllegalArgumentException("Cant find implementation");
		}

	}

	/**
	 * Iterate over a given root directory and search class files
	 * 
	 * @author Daniel Wiese
	 * @since 13.11.2005
	 * @param currentPackageName -
	 *            the package we are starting with
	 * @param directory .
	 *            the current directory
	 */
	private void findClassesRecursive(String currentPackageName, File directory) {

		if (directory.exists()) {
			// Get the list of the files contained in the package
			File[] files = directory.listFiles();
			String[] fileNames = directory.list();
			for (int i = 0; i < files.length; i++) {

				// we are only interested in .class files
				if (fileNames[i].endsWith(".class")) {
					// removes the .class extension
					String classname = fileNames[i].substring(0, fileNames[i]
							.length() - 6);

					// Try to find the class on the class path
					final String toAdd = currentPackageName + "." + classname;
					this.allClasses.add(toAdd.replace('.', '/'));

				} else if (files[i].isDirectory()) {
					String newPackageName = "";
					if (currentPackageName.equals("")) {
						newPackageName = fileNames[i];
					} else {
						newPackageName = currentPackageName + "."
								+ fileNames[i];
					}
					findClassesRecursive(newPackageName, files[i]);
				}
			}
		}
	}

	/**
	 * Iterate over a given root directory and search class files
	 * 
	 * @author Daniel Wiese
	 * @since 13.11.2005
	 * @param currentPackageName -
	 *            the package we are starting with
	 * @param directory .
	 *            the current directory
	 */
	private void findClassesFromList(List<String> files) {

		for (String file : files) {
			if (file.endsWith(".class")) {
				// removes the .class extension
				String classname = file.substring(0, file.length() - 6);
				classname = classname.replace("\\", "/");
				this.allClasses.add(classname);
			}
		}
	}
}
