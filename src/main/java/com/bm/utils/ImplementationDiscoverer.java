package com.bm.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * This class discoverimplementations for interfaces. The current implementation
 * takes "is happy" with one possible impelmentation. If a interface has two
 * implementations it´s currently not defined which one if going to be returned
 * 
 * @author Daniel Wiese
 * @since 13.11.2005
 */
public class ImplementationDiscoverer {

    private static final Logger log = Logger.getLogger(ImplementationDiscoverer.class);

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
            throw new IllegalArgumentException("The class (" + toFind + ") is not a Interface");
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
            File rootDirectory = Ejb3Utils.getRootPackageDir(f, toFind.getName());
            findClassesRecursive("", rootDirectory);
            // generate interface-impl map
            this.makeInterfaceImplemantationMap();
        } else {
            findImplementationInJarFiles(loc);
        }
    }

    private void findImplementationInJarFiles(URL locationInJar) {
        String jarName = Ejb3Utils.isolateJarName(locationInJar);
        // windows bug with spaces
        jarName = jarName.replaceAll("\\%20", " ");
        try {
            InputStream input = new FileInputStream(jarName);
            
            List<String> back = Ejb3Utils.scanFileNamesInArchive(input);
            input.close();
            this.findClassesFromList(back);
            // generate interface-impl map
            this.makeInterfaceImplemantationMap();

        } catch (FileNotFoundException e) {
            log.error("File not found", e);
            throw new IllegalArgumentException("Cant find implementation");
        } catch (IOException e) {
            log.error("File not found", e);
            throw new IllegalArgumentException("Cant find implementation");
        }

    }

    private void makeInterfaceImplemantationMap() {

        // find all interfaces
        for (Class aktClass : allClasses) {
            if (!aktClass.isInterface() && !isInnerClass(aktClass)) {
                Class[] interfaces = aktClass.getInterfaces();
                for (Class aktInt : interfaces) {
                    this.interfaceImplentation.put(aktInt, aktClass);
                }
            }
        }
    }

    private boolean isInnerClass(Class aktClass) {
        boolean back = false;
        if (aktClass.isSynthetic()) {
            back = true;
        } else if (aktClass.isAnonymousClass()) {
            back = true;
        } else if (aktClass.getName().indexOf("$") != -1) {
            back = true;
        }
        return back;
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
                    String classname = fileNames[i].substring(0, fileNames[i].length() - 6);
                    try {
                        // Try to find the class on the class path
                        Class c = Class.forName(currentPackageName + "." + classname);
                        this.allClasses.add(c);

                    } catch (ClassNotFoundException cnfex) {
                        log.debug("Can´t load class: " + cnfex);
                    }
                } else if (files[i].isDirectory()) {
                    String newPackageName = "";
                    if (currentPackageName.equals("")) {
                        newPackageName = fileNames[i];
                    } else {
                        newPackageName = currentPackageName + "." + fileNames[i];
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
                classname = classname.replace("\\", ".");
                try {
                    // Try to find the class on the class path
                    Class c = Class.forName(classname);
                    this.allClasses.add(c);

                } catch (ClassNotFoundException cnfex) {
                    log.error("Can´t load class: " + cnfex);
                }
            }
        }
    }
}
