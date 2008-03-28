package com.bm.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.jar.JarInputStream;
import java.util.zip.ZipEntry;

import javax.ejb.Local;
import javax.ejb.Remote;

import com.bm.datagen.Generator;
import com.bm.datagen.annotations.GeneratorType;
import com.bm.introspectors.Property;

/**
 * Util class for ejb3unit.
 * 
 * @author Daniel Wiese
 */
public final class Ejb3Utils {

	private static final org.apache.log4j.Logger log = org.apache.log4j.Logger
			.getLogger(Ejb3Utils.class);

	/**
	 * This field is used to OSTYPE_WINDOWS.
	 */
	public static final int OSTYPE_WINDOWS = 1;

	/**
	 * This field is used to OSTYPE_WINNT.
	 */
	public static final int OSTYPE_WINNT = 2;

	/**
	 * This field is used to OSTYPE_WINCE.
	 */
	public static final int OSTYPE_WINCE = 3;

	/**
	 * This field is used to OSTYPE_LINUX.
	 */
	public static final int OSTYPE_LINUX = 4;

	/**
	 * This field is used to OSTYPE_MAC.
	 */
	public static final int OSTYPE_MAC = 5;

	/**
	 * This field is used to OSTYPE_SOLARIS.
	 */
	public static final int OSTYPE_SOLARIS = 6;

	/**
	 * This field is used to OSTYPE_SUN.
	 */
	public static final int OSTYPE_SUN = 7;

	/**
	 * This field is used to OSTYPE_NETWARE.
	 */
	public static final int OSTYPE_NETWARE = 8;

	/**
	 * This field is used to OSTYPE_OS2.
	 */
	public static final int OSTYPE_OS2 = 9;

	/**
	 * This field is used to OSTYPE_UNKNOWN.
	 */
	public static final int OSTYPE_UNKNOWN = 10;

	/** default type * */
	private static int type = OSTYPE_UNKNOWN;

	private Ejb3Utils() {
		// intentionally left blank
	}

	/**
	 * Isolates a jar file when a file was found inside a jar.
	 * 
	 * @param fileInJar -
	 *            the path to the file inside the jar file
	 * @return - the name of the jar file
	 */
	public static String isolateJarName(URL fileInJar) {
		String urlSt = getDecodedFilename(fileInJar);
		urlSt = urlSt.substring("file:/".length(), urlSt.indexOf("!"));
		// under linux, solaris we need an absolute path
		int os = getOs();
		if (os == OSTYPE_LINUX || os == OSTYPE_SOLARIS || os == OSTYPE_SUN || os == OSTYPE_MAC) {
			urlSt = "/" + urlSt;
		}
		return urlSt;
	}

	/**
	 * Decodes an encoded file name in url
	 * 
	 * @param url
	 *            the file name
	 * @return the decoded version
	 */
	public static String getDecodedFilename(URL url) {
		String decoded = null;
		try {
			decoded = URLDecoder.decode(url.getFile(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new IllegalArgumentException(e);
		}
		return decoded;
	}

	/**
	 * Creates a new instance of a bean, even if it's protected constructor.
	 * (chapter 2.1 page 17), "The entity class must have a no-arg constructor.
	 * The entity class may have other constructors as well. The no-arg
	 * constructor must be public or protected."
	 * 
	 * @author Daniel Wiese
	 * @since 28.03.2007
	 * @param <T>
	 * @param forClass
	 *            for which class
	 * @return the instrance
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getNewInstance(Class<T> forClass) {
		Constructor<T>[] parameterlessConstructors = (Constructor<T>[]) forClass
				.getDeclaredConstructors();
		if (parameterlessConstructors == null) {
			throw new RuntimeException("The session/entity bean (" + forClass.getName()
					+ ") has no public/protected parameterles constructor");
		}

		for (Constructor<T> current : parameterlessConstructors) {
			final Class[] params = current.getParameterTypes();
			if (params == null || params.length == 0) {
				current.setAccessible(true);
				T back = null;
				try {
					back = current.newInstance((Object[]) null);
				} catch (InstantiationException e) {
					log.error("Can´t create the session/entity bean", e);
					throw new RuntimeException("Can´t create the session/entity bean", e);

				} catch (IllegalArgumentException e) {
					log.error("Can´t create the entity bean", e);
					throw new RuntimeException("Can´t create the session/entity bean", e);
				} catch (InvocationTargetException e) {
					log.error("Can´t create the entity bean", e);
					throw new RuntimeException("Can´t create the session/entity bean", e);
				} catch (SecurityException e) {
					throw new RuntimeException(
							"Insufficient access rights to create the session/entity bean ("
									+ forClass.getName() + ")");
				} catch (IllegalAccessException e) {
					throw new RuntimeException(
							"Insufficient access rights to create the session/entity bean ("
									+ forClass.getName() + ")");
				}
				return back;
			}
		}

		throw new RuntimeException("The session/entity bean (" + forClass.getName()
				+ ") has no public/protected parameterles constructor");
	}

	/**
	 * Determines the OS.
	 * 
	 * @return an integer identifying the OS (one of the OSTYPE constants)
	 */
	public static int getOs() {
		if (type == OSTYPE_UNKNOWN) {

			final String osname = System.getProperty("os.name").toLowerCase();
			log.info("OS was found to be: " + osname);
			if (osname.indexOf("windows") != -1) {
				if (osname.indexOf("nt") != -1 || osname.indexOf("2000") != -1
						|| osname.indexOf("xp") != -1) {
					type = OSTYPE_WINNT;
				} else if (osname.indexOf("ce") != -1) {
					type = OSTYPE_WINCE;
				} else {
					type = OSTYPE_WINDOWS;
				}
			} else if (osname.indexOf("linux") != -1 || osname.indexOf("bsd") != -1) {
				type = OSTYPE_LINUX;
			} else if (osname.indexOf("mac os") != -1 || osname.indexOf("macos") != -1
						// SoyLatte JDK returns "darwin" on Mac OSX 
						|| osname.indexOf("darwin") != -1) {
				type = OSTYPE_MAC;
			} else if (osname.indexOf("solaris") != -1) {
				type = OSTYPE_SOLARIS; // could also be old freebsd version
			} else if (osname.indexOf("sun") != -1) {
				type = OSTYPE_SUN;
			} else if (osname.indexOf("netware") != -1) {
				type = OSTYPE_NETWARE;
			} else if (osname.indexOf("os/2") != -1) {
				type = OSTYPE_OS2;
			} else {
				type = OSTYPE_UNKNOWN;
			}
		}

		return type;
	}

	/**
	 * Dump the contents of a JarArchive to the specified destination.
	 * 
	 * @param in -
	 *            the jar archive as input stream
	 * @param dest -
	 *            the destination (to extract the content)
	 * @return - a list with all extracted files
	 * @throws IOException -
	 *             in an error case
	 */
	public static List<File> unjar(InputStream in, File dest) throws IOException {

		final List<File> back = new ArrayList<File>();
		if (!dest.exists()) {
			dest.mkdirs();
		}

		if (!dest.isDirectory()) {
			throw new IOException("Destination must be a directory.");
		}

		JarInputStream jin = new JarInputStream(in);
		final byte[] buffer = new byte[1024];
		ZipEntry entry = jin.getNextEntry();
		while (entry != null) {

			String fileName = entry.getName();
			if (fileName.charAt(fileName.length() - 1) == '/') {
				fileName = fileName.substring(0, fileName.length() - 1);
			}

			if (fileName.charAt(0) == '/') {
				fileName = fileName.substring(1);
			}

			if (File.separatorChar != '/') {
				fileName = fileName.replace('/', File.separatorChar);
			}

			final File file = new File(dest, fileName);
			if (entry.isDirectory()) {
				// make sure the directory exists
				file.mkdirs();
				jin.closeEntry();
			} else {
				// make sure the directory exists
				final File parent = file.getParentFile();
				if (parent != null && !parent.exists()) {
					parent.mkdirs();
				}

				// dump the file

				final OutputStream out = new FileOutputStream(file);
				int len = 0;
				while ((len = jin.read(buffer, 0, buffer.length)) != -1) {
					out.write(buffer, 0, len);
				}

				out.flush();
				out.close();
				jin.closeEntry();
				back.add(file);

			}

			entry = jin.getNextEntry();
		}
		jin.close();
		return back;
	}

	/**
	 * Extract the first entry in the jar file to an output stream.
	 * 
	 * @param in -
	 *            the jar archive as input stream
	 * @throws IOException -
	 *             in an error case
	 */
	public static InputStream unjar(InputStream in) throws IOException {

		JarInputStream jin = new JarInputStream(in);
		final byte[] buffer = new byte[2048];
		ZipEntry entry = jin.getNextEntry();
		InputStream toReturn = null;
		while (entry != null) {
			if (!entry.isDirectory()) {
				// make sure the directory exists

				// dump the file

				final ByteArrayOutputStream out = new ByteArrayOutputStream();
				int len = 0;
				while ((len = jin.read(buffer, 0, buffer.length)) != -1) {
					out.write(buffer, 0, len);
				}

				out.flush();
				out.close();
				jin.closeEntry();
				toReturn = new ByteArrayInputStream(out.toByteArray());
				break;
			}

			entry = jin.getNextEntry();
		}
		jin.close();

		return toReturn;
	}

	/**
	 * Scan for files in jar file.
	 * 
	 * @param in -
	 *            the jar archive as input stream
	 * 
	 * @return - a list with all extracted files
	 * @throws IOException -
	 *             in an error case
	 */
	public static List<String> scanFileNamesInArchive(InputStream in) throws IOException {

		final List<String> back = new ArrayList<String>();

		JarInputStream jin = new JarInputStream(in);
		ZipEntry entry = jin.getNextEntry();
		while (entry != null) {

			String fileName = entry.getName();
			if (fileName.charAt(fileName.length() - 1) == '/') {
				fileName = fileName.substring(0, fileName.length() - 1);
			}

			if (fileName.charAt(0) == '/') {
				fileName = fileName.substring(1);
			}

			if (File.separatorChar != '/') {
				fileName = fileName.replace('/', File.separatorChar);
			}

			if (!entry.isDirectory()) {
				back.add(fileName);
			}

			entry = jin.getNextEntry();
		}
		jin.close();
		return back;
	}

	/**
	 * Returns all business (local, remote) interfaces of the class.
	 * 
	 * @author Daniel Wiese
	 * @since 05.02.2006
	 * @param toAnalyse -
	 *            the session bean /service to analyse
	 * @return - the interfaces
	 */
	public static List<Class<?>> getLocalRemoteInterfaces(Class<?> toAnalyse) {
		final List<Class<?>> back = new ArrayList<Class<?>>();
		if (toAnalyse != null) {
			Class<?>[] interfaces = toAnalyse.getInterfaces();
			if (interfaces != null) {
				for (Class<?> interf : interfaces) {
					if (interf.getAnnotation(Local.class) != null
							|| interf.getAnnotation(Remote.class) != null) {
						back.add(interf);
					}
				}
			}
		}

		return back;
	}

	/**
	 * This method will do the transformation of primitive types if necessary.
	 * 
	 * @param aktField -
	 *            the field to inspect
	 * @return the declaring type (or primitive representant)
	 */
	public static Class<?> getNonPrimitiveType(Property aktField) {
		return getNonPrimitiveType(aktField.getType());

	}

	/**
	 * This method will do the transformation of primitive types if necessary.
	 * It also transform all collection types (List, etc to Collection)
	 * 
	 * @param aktField -
	 *            the field to inspect
	 * @return the declaring type (or primitive substitute)
	 */
	public static Class<?> getNonPrimitiveType(Class<?> aktField) {
		if (aktField == double.class) {
			return Double.class;
		} else if (aktField == float.class) {
			return Float.class;
		} else if (aktField == int.class) {
			return Integer.class;
		} else if (aktField == byte.class) {
			return Byte.class;
		} else if (aktField == boolean.class) {
			return Boolean.class;
		} else if (aktField == char.class) {
			return Character.class;
		}  else if (aktField == long.class) {
			return Long.class;
		} else if (aktField == short.class) {
			return Short.class;
		} else if (hasSuperclassOrInterface(aktField, Set.class)) {
			return Set.class;
		}else if (hasSuperclassOrInterface(aktField, Collection.class)) {
			return Collection.class;
		}

		return aktField;

	}

	/**
	 * Checks if the current class inherits form the specified class.
	 * 
	 * @param toCheck
	 *            the class to check
	 * @param superclassOrInterface
	 *            the superclass
	 * @return true if a supeclass or interface
	 */
	public static boolean hasSuperclassOrInterface(Class<?> toCheck, Class<?> superclassOrInterface) {
		if (toCheck == null) {
			return false;
		} else if (superclassOrInterface.equals(toCheck)) {
			return true;
		} else if (toCheck.equals(Object.class)) {
			return false;
		} else {
			Class<?>[] interfaces = toCheck.getInterfaces();
			if (interfaces != null) {
				for (Class<?> current : interfaces) {
					if (hasSuperclassOrInterface(current, superclassOrInterface)) {
						return true;
					}
				}
			}
			return hasSuperclassOrInterface(toCheck.getSuperclass(), superclassOrInterface);
		}
	}

	/**
	 * Returns a generator type for a given generator.
	 * 
	 * @author Daniel Wiese
	 * @since 17.04.2006
	 * @param actGenerator -
	 *            given generator
	 * @return returns a given generator type
	 */
	public static GeneratorType getGeneratorTypeAnnotation(Generator<?> actGenerator) {
		Annotation[] classAnnotations = actGenerator.getClass().getAnnotations();
		// iterate over the annotations
		for (Annotation a : classAnnotations) {
			if (a instanceof GeneratorType) {
				final GeneratorType gT = (GeneratorType) a;
				return gT;
			}
		}
		return null;
	}

	/**
	 * Returns the right collection type for the given property.
	 * 
	 * @param forProperty -
	 *            for which property
	 * @return - the right collection type
	 */
	public static <T> Collection<T> getRightCollectionType(Property forProperty) {
		if (forProperty.getType().equals(List.class)) {
			return new ArrayList<T>();
		} else if (forProperty.getType().equals(Set.class)) {
			return new HashSet<T>();
		} else if (forProperty.getType().equals(LinkedList.class)) {
			return new LinkedList<T>();
		} else if (forProperty.getType().equals(Vector.class)) {
			return new Vector<T>();
		} else if (forProperty.getType().equals(Set.class)) {
			return new HashSet<T>();
		} else {
			return new ArrayList<T>();
		}
	}

	/**
	 * Returns all fields (including fields from all super classes) of a class.
	 * 
	 * @param forClass -
	 *            for which class
	 * @return - all fields
	 */
	public static Field[] getAllFields(Class<?> forClass) {
		final List<Field> fields = new ArrayList<Field>();
		Class<?> aktClass = forClass;
		while (!aktClass.equals(Object.class)) {
			Field[] tmp = aktClass.getDeclaredFields();
			for (Field akt : tmp) {
				fields.add(akt);
			}
			aktClass = aktClass.getSuperclass();
		}
		return fields.toArray(new Field[fields.size()]);
	}

	/**
	 * Returns a parameterless method.
	 * 
	 * @param name
	 *            the name of the method
	 * @param inClass
	 *            in which class
	 * @return the method if found or IllegalArgument exception
	 */
	public static Method getParameterlessMethodByName(String name, Class<?> inClass) {
		final Method[] all = getAllMethods(inClass);
		for (Method current : all) {
			if (current.getName().equals(name)) {
				return current;
			}
		}

		throw new IllegalArgumentException("Method (" + name + ") with name not fould in class ("
				+ inClass.getName() + ")");
	}

	/**
	 * Returns all fields (including fields from all super classes) of a class.
	 * 
	 * @param forClass -
	 *            for which class
	 * @return - all fields
	 */
	public static Method[] getAllMethods(Class<?> forClass) {
		final List<Method> methods = new ArrayList<Method>();
		Class<?> aktClass = forClass;
		while (!aktClass.equals(Object.class)) {
			Method[] tmp = aktClass.getDeclaredMethods();
			for (Method akt : tmp) {
				methods.add(akt);
			}
			aktClass = aktClass.getSuperclass();
		}
		return methods.toArray(new Method[methods.size()]);
	}

	/**
	 * Return a short class name. E.g. java.util.StringTokenizer will be
	 * StringTokenizer
	 * 
	 * @param longClassName -
	 *            the long fully qualified class name
	 * @return - short class name
	 */
	public static String getShortClassName(String longClassName) {
		final StringTokenizer tk = new StringTokenizer(longClassName, ".");
		String last = longClassName;
		while (tk.hasMoreTokens()) {
			last = tk.nextToken();
		}

		return last;
	}

	/**
	 * Return a short class name. E.g. java.util.StringTokenizer will be
	 * StringTokenizer
	 * 
	 * @param longClassName -
	 *            the long fully qualified class name
	 * @return - short class name
	 */
	public static String getPackageName(String longClassName) {
		final StringTokenizer tk = new StringTokenizer(longClassName, ".");
		final StringBuilder sb = new StringBuilder();
		String last = longClassName;
		while (tk.hasMoreTokens()) {
			last = tk.nextToken();
			if (tk.hasMoreTokens()) {
				sb.append(last);
				sb.append(".");
			}
		}

		return sb.toString().substring(0, sb.toString().length() - 1);
	}

	/**
	 * Returns the root package directory e.g com.ejb3unit.eg --> returns com.
	 * 
	 * @param location -
	 *            the location of the package
	 * 
	 * @param longPackageName -
	 *            the long fully qualified class name
	 * @return - root file name
	 */
	public static File getRootPackageDir(File location, String longPackageName) {
		final StringTokenizer tk = new StringTokenizer(longPackageName, ".");
		File back = location;
		while (tk.hasMoreTokens()) {
			tk.nextToken();
			back = back.getParentFile();
		}
		return back;
	}

	/**
	 * Return a short class name. E.g. java.util.StringTokenizer will be
	 * StringTokenizer
	 * 
	 * @param clazz -
	 *            for class
	 * @return - short class name
	 */
	public static String getShortClassName(Class<?> clazz) {
		return getShortClassName(clazz.getName());
	}

	/**
	 * Returns a path to an temp directory.
	 * 
	 * @author Daniel Wiese
	 * @since 29.06.2006
	 * @return - a path to a temp directory.
	 */
	public static File getTempDirectory() {
		File tempdir = new File(System.getProperty("java.io.tmpdir"));
		return tempdir;
	}

}