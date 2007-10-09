package com.bm.ejb3metadata.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.jar.JarInputStream;
import java.util.zip.ZipEntry;

import javax.ejb.Local;
import javax.ejb.Remote;

import com.bm.utils.Ejb3Utils;

/**
 * Util class for ejb3unit.
 * 
 * @author Daniel Wiese
 */
public final class MetadataUtils {

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
	 * This field is used to OSTYPE_NETWARE.
	 */
	public static final int OSTYPE_NETWARE = 7;

	/**
	 * This field is used to OSTYPE_OS2.
	 */
	public static final int OSTYPE_OS2 = 8;

	/**
	 * This field is used to OSTYPE_UNKNOWN.
	 */
	public static final int OSTYPE_UNKNOWN = 9;

	/** default type * */
	private static int type = OSTYPE_UNKNOWN;

	private MetadataUtils() {
		// intentinally left blank
	}

	/**
	 * Isolates a jar file when a file was found inside a jar.
	 * 
	 * @param fileInJar -
	 *            the path to the file inside the jar file
	 * @return - the name of the jar file
	 */
	public static String isolateJarName(URL fileInJar) {
		return Ejb3Utils.isolateJarName(fileInJar);
	}
	

	/**
	 * Determines the OS.
	 * 
	 * @return an integer identifying the OS (one of the OSTYPE constants)
	 */
	public static int getOs() {
		if (type == OSTYPE_UNKNOWN) {

			final String osname = System.getProperty("os.name").toLowerCase();

			if (osname.indexOf("windows") != -1) {
				if (osname.indexOf("nt") != -1 || osname.indexOf("2000") != -1
						|| osname.indexOf("xp") != -1) {
					type = OSTYPE_WINNT;
				} else if (osname.indexOf("ce") != -1) {
					type = OSTYPE_WINCE;
				} else {
					type = OSTYPE_WINDOWS;
				}
			} else if (osname.indexOf("linux") != -1
					|| osname.indexOf("bsd") != -1) {
				type = OSTYPE_LINUX;
			} else if (osname.indexOf("mac os") != -1
					|| osname.indexOf("macos") != -1) {
				type = OSTYPE_MAC;
			} else if (osname.indexOf("solaris") != -1) {
				type = OSTYPE_SOLARIS; // could also be old freebsd version
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
	 * Dump the contents of a JarArchive to the dpecified destination.
	 * 
	 * @param in -
	 *            the jar archive as input stream
	 * @param dest -
	 *            the destination (to extract the content)
	 * @return - a list with all extracted files
	 * @throws IOException -
	 *             in an error case
	 */
	public static List<File> unjar(InputStream in, File dest)
			throws IOException {

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
	 * Scan for files in jar file.
	 * 
	 * @param in -
	 *            the jar archive as input stream
	 * 
	 * @return - a list with all extracted files
	 * @throws IOException -
	 *             in an error case
	 */
	public static List<String> scanFileNamesInArchive(InputStream in)
			throws IOException {

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
	public static List<Class> getLocalRemoteInterfaces(Class toAnalyse) {
		final List<Class> back = new ArrayList<Class>();
		if (toAnalyse != null) {
			Class[] interfaces = toAnalyse.getInterfaces();
			if (interfaces != null) {
				for (Class<Object> interf : interfaces) {
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
	 * This method will do the transformation of primitive types if neccessary.
	 * 
	 * @param aktField -
	 *            the field to inspect
	 * @return the declaring type (or primitive representant)
	 */
	public static Class getNonPrimitiveType(Class aktField) {
		if (aktField == double.class) {
			return Double.class;
		} else if (aktField == float.class) {
			return Float.class;
		} else if (aktField == int.class) {
			return Integer.class;
		} else if (aktField == boolean.class) {
			return Boolean.class;
		} else if (aktField == char.class) {
			return Character.class;
		} else if (aktField == byte[].class) {
			return Byte.class;
		} else if (aktField == long.class) {
			return Long.class;
		} else if (aktField == short.class) {
			return Short.class;
		} else {
			return aktField;
		}

	}

	/**
	 * Returns all fields (including fields from all superclasses) of a class.
	 * 
	 * @param forClass -
	 *            for which class
	 * @return - all fields
	 */
	public static Field[] getAllFields(Class forClass) {
		final List<Field> fields = new ArrayList<Field>();
		Class aktClass = forClass;
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
	 * Returns all fields (including fields from all superclasses) of a class.
	 * 
	 * @param forClass -
	 *            for which class
	 * @return - all fields
	 */
	public static Method[] getAllMethods(Class forClass) {
		final List<Method> methods = new ArrayList<Method>();
		Class aktClass = forClass;
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
	 * Retrun a short class name. E.g. java.util.StringTokenizer will be
	 * StringTokenizer
	 * 
	 * @param longClassName -
	 *            the long fully qualified calss name
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
	 * Retrun a short class name. E.g. java.util.StringTokenizer will be
	 * StringTokenizer
	 * 
	 * @param longClassName -
	 *            the long fully qualified calss name
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
	 * Retrurns the root package directory e.g com.ejb3unit.eg --> returns com.
	 * 
	 * @param location -
	 *            the location of th epackage
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
	 * Retrun a short class name. E.g. java.util.StringTokenizer will be
	 * StringTokenizer
	 * 
	 * @param clazz -
	 *            for class
	 * @return - short class name
	 */
	public static String getShortClassName(Class clazz) {
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
