package com.bm.utils;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * This class discoverimplementations for interfaces. The current implementation
 * takes "is happy" with one possible impelmentation. If a interface has two
 * implementations it�s currently not defined which one if going to be returned
 * 
 * @author Daniel Wiese
 * @since 13.11.2005
 */
public class ImplementationDiscoverer {

	private static final Logger log = Logger
			.getLogger(ImplementationDiscoverer.class);

	private final List<Class> allClasses = new ArrayList<Class>();

	private final Map<Class, Class> interfaceImplentation = new HashMap<Class, Class>();

	private boolean isInitailized = false;

	/**
	 * Finds a possible implementation for a given interface.
	 * 
	 * @author Daniel Wiese
	 * @since 13.11.2005
	 * @param toFind -
	 *            the interface
	 * @return the implementation of the interface
	 */
	public Class findImplementation(Class toFind) {
		if (toFind == null) {
			throw new IllegalArgumentException("The argument is null");
		} else if (!toFind.isInterface()) {
			throw new IllegalArgumentException("The class (" + toFind
					+ ") is not a Interface");
		}

		if (!isInitailized) {
			this.init(toFind);
		}

		if (this.interfaceImplentation.containsKey(toFind)) {
			return this.interfaceImplentation.get(toFind);
		} else {
			throw new IllegalArgumentException("The interface (" + toFind
					+ ") has no known implementation");
		}
	}

	private void init(Class toFind) {
		// Translate the package name into an absolute path
		String name = toFind.getName().replace('.', '/');
		URL loc = toFind.getResource("/" + name + ".class");

		File f = new File(loc.getFile());
		// Class file is inside a jar file.
		if (f.getPath().startsWith("file:")) {
			String s = f.getPath();
			int index = s.indexOf('!');
			// It confirm it is a jar file
			if (index != -1) {
				f = new File(s.substring(5).replace('!', File.separatorChar));

				throw new IllegalArgumentException(
						"Class disoverey in Jar files is currently not supported!)");
			}
		}
		File rootDirectory = Ejb3Utils.getRootPackageDir(f, toFind.getName());
		findClassesRecursive("", rootDirectory);
		// generate interface-impl map
		this.makeInterfaceImplemantationMap();
	}

	private void makeInterfaceImplemantationMap() {

		// find all interfaces
		for (Class aktClass : allClasses) {
			if (!aktClass.isInterface()) {
				Class[] interfaces = aktClass.getInterfaces();
				for (Class aktInt : interfaces) {
					this.interfaceImplentation.put(aktInt, aktClass);
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
					try {
						// Try to find the class on the class path
						Class c = Class.forName(currentPackageName + "."
								+ classname);
						this.allClasses.add(c);

					} catch (ClassNotFoundException cnfex) {
						log.debug("Can�t load class: " + cnfex);
					}
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
}
