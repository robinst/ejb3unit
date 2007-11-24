package com.bm.ejb3metadata.annotations.metadata;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.ejb.ApplicationException;

import com.bm.ejb3metadata.xml.struct.EJB3;

/**
 * This class represents the annotation metadata of all classes of an EjbJar
 * file. From this class, we can get metadata of all beans.
 * 
 * @author Daniel Wiese
 */
public class EjbJarAnnotationMetadata {

	private final static org.apache.log4j.Logger logger = org.apache.log4j.Logger
			.getLogger(EjbJarAnnotationMetadata.class);

	/**
	 * List of class annotations metadata.
	 */
	private Map<String, ClassAnnotationMetadata> classesAnnotationMetadata = null;

	/**
	 * Map of interface names and the first possible implementation Currently we
	 * don´t manage different implementations of a session bean TODO check the
	 * spect: if a Local/Romete interface has 2 different implementation
	 */
	private Map<String, String> interface2implemantation = null;

	/**
	 * Link to the Deployment Descriptor object.
	 */
	private EJB3 ejb3 = null;

	/**
	 * List of application exceptions used on this ejb-jar.
	 */
	private Map<String, ApplicationException> applicationExceptions = null;

	/**
	 * Constructor.
	 */
	public EjbJarAnnotationMetadata() {
		classesAnnotationMetadata = new HashMap<String, ClassAnnotationMetadata>();
	}

	/**
	 * Merge the items from another
	 * 
	 * @param other
	 */
	public void mergeClassAnnotationMetadata(EjbJarAnnotationMetadata other) {
		classesAnnotationMetadata.putAll(other.classesAnnotationMetadata);
	}

	/**
	 * Add annotation metadata for a given class.
	 * 
	 * @param classAnnotationMetadata
	 *            annotation metadata of a class.
	 */
	public void addClassAnnotationMetadata(final ClassAnnotationMetadata classAnnotationMetadata) {
		String key = classAnnotationMetadata.getClassName();
		// already exists ?
		if (classesAnnotationMetadata.containsKey(key)) {
			String msg = "EjbJarAnnotationMetadata.addClassAnnotationMetadata.alreadyPresent";
			logger.debug(msg);
			// throw new IllegalStateException(msg);
		}
		classesAnnotationMetadata.put(key, classAnnotationMetadata);
	}

	/**
	 * Returns the name of the bean class by passing the rmote/ local interface
	 * name.
	 * 
	 * @return the name of the bean class by passing the rmote/ local interface
	 *         name.
	 * @param interfaceName -
	 *            the name of the local / remote interface
	 */
	public String getBeanImplementationForInterface(String interfaceName) {
		return this.interface2implemantation.get(interfaceName);
	}

	/**
	 * Returns the name of the bean class by passing the rmote/ local interface
	 * name.
	 * 
	 * @return the name of the bean class by passing the rmote/ local interface
	 *         name.
	 * @param interfaceName -
	 *            the name of the local / remote interface
	 */
	public String getBeanImplementationForInterface(Class interfaceName) {
		final String name = interfaceName.getName();
		if (interface2implemantation == null) {
			buildInterfaceImplementationMap();
		}
		return this.interface2implemantation.get(name.replace('.', '/'));
	}

	/**
	 * Builds a map of Local/Remote interfaces and it´s implementations.
	 */
	private void buildInterfaceImplementationMap() {
		interface2implemantation = new HashMap<String, String>();
		for (ClassAnnotationMetadata current : this.getClassAnnotationMetadataCollection()) {
			if (current.getLocalInterfaces() != null) {
				for (String interfaze : current.getLocalInterfaces().getInterfaces()) {
					// build no implementation map for anonymus classes and
					// inner classes
					if (current.getClassName().indexOf('$') < 0) {
						this.interface2implemantation.put(interfaze, current.getClassName());
					}
				}
			}
			if (current.getRemoteInterfaces() != null) {
				for (String interfaze : current.getRemoteInterfaces().getInterfaces()) {
					// build no implementation map for anonymus classes and
					// inner classes
					if (current.getClassName().indexOf('$') < 0) {
						this.interface2implemantation.put(interfaze, current.getClassName());
					}
				}
			}
		}
	}

	/**
	 * Get class annotation metadata.
	 * 
	 * @param className
	 *            key of the map of annotations bean.
	 * @return Bean annotation metadata of a given name.
	 */
	public ClassAnnotationMetadata getClassAnnotationMetadata(final String className) {
		return classesAnnotationMetadata.get(className);
	}

	/**
	 * Get collections of bean annotation metadata.
	 * 
	 * @return collections of bean annotation metadata.
	 */
	public Collection<ClassAnnotationMetadata> getClassAnnotationMetadataCollection() {
		return classesAnnotationMetadata.values();
	}

	/**
	 * @return the ejb3 deployment descriptor object.
	 */
	public EJB3 getEjb3() {
		return ejb3;
	}

	/**
	 * Sets the ejb3 deployment descriptor object.
	 * 
	 * @param ejb3
	 *            the ejb3 deployment descriptor object.
	 */
	public void setEjb3(final EJB3 ejb3) {
		this.ejb3 = ejb3;
	}

	/**
	 * Gets the list of application exceptions defined on this ejb jar metadata.
	 * 
	 * @return the list of application exceptions defined on this ejb jar
	 *         metadata.
	 */
	public Map<String, ApplicationException> getApplicationExceptions() {
		if (applicationExceptions != null) {
			return applicationExceptions;
		}

		// compute it
		applicationExceptions = new HashMap<String, ApplicationException>();

		// For each class, look if it is an application exception
		for (ClassAnnotationMetadata classMetadata : getClassAnnotationMetadataCollection()) {
			ApplicationException appException = classMetadata.getApplicationException();
			// found it then add it in the map.
			if (appException != null) {
				applicationExceptions.put(classMetadata.getClassName().replaceAll("/", "."),
						appException);
			}
		}
		return applicationExceptions;
	}

	/**
	 * Returns the interface2implemantation.
	 * 
	 * @return Returns the interface2implemantation.
	 */
	public Map<String, String> getInterface2implemantation() {
		if (interface2implemantation == null) {
			buildInterfaceImplementationMap();
		}
		return interface2implemantation;
	}

}
