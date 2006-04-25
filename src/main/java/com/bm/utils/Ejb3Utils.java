package com.bm.utils;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;

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

	private Ejb3Utils() {
		// intentinally left blank
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
	public static Class getNonPrimitiveType(Property aktField) {
		return getNonPrimitiveType(aktField.getType());

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
	 * Returns a generator type for a givven generator.
	 * @author Daniel Wiese
	 * @since 17.04.2006
	 * @param actGenerator - given genrator
	 * @return returns a given genrator type
	 */
	public static GeneratorType getGeneratorTypeAnnotation(Generator actGenerator) {
		Annotation[] classAnnotations = actGenerator.getClass()
				.getAnnotations();
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
	 * Returns the riht collection type for the given property.
	 * 
	 * @param forProperty -
	 *            for which property
	 * @return - the rigt collection type
	 */
	public static Collection getRightCollectionType(Property forProperty) {
		if (forProperty.getType().equals(List.class)) {
			return new ArrayList();
		} else if (forProperty.getType().equals(Set.class)) {
			return new HashSet();
		} else if (forProperty.getType().equals(LinkedList.class)) {
			return new LinkedList();
		} else if (forProperty.getType().equals(Vector.class)) {
			return new Vector();
		} else if (forProperty.getType().equals(Set.class)) {
			return new HashSet();
		} else {
			return new ArrayList();
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

}
