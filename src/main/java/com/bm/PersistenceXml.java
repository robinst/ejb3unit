package com.bm;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class PersistenceXml {

	private static final Logger logger = Logger.getLogger(PersistenceXml.class);

	/**
	 * Given a properly configured ejb3unit.properties file this will read a
	 * persistence.xml file and will give you back all the classes in that
	 * correspond to those in the persistence.xml. This allows all known
	 * entities to be loaded before you run any tests. Hence your dependencies
	 * (within tests) should be satisfied.
	 * 
	 * @return the classes
	 * @param persitenceUnitName
	 *            the name of the persistence unit
	 * @throws IOException
	 *             when there is a problem reading the xml
	 * @throws ClassNotFoundException
	 *             when the xml points you to a non existant class
	 */
	public static Class<?>[] getClasses(String persitenceUnitName) throws IOException,
			ClassNotFoundException {
		return getClasses(Thread.currentThread().getContextClassLoader(),
				persitenceUnitName);
	}

	/**
	 * Given a properly configured ejb3unit.properties file this will read a
	 * persistence.xml file and will give you back all the classes in that
	 * correspond to those in the persistence.xml. This allows all known
	 * entities to be loaded before you run any tests. Hence your dependencies
	 * (within tests) should be satisfied.
	 * 
	 * @param cl
	 *            the class loader
	 * @param persitenceUnitName
	 *            the name of the persistence unit
	 * @return the classes
	 * @throws IOException
	 *             when there is a problem reading the xml
	 * @throws ClassNotFoundException
	 *             when the xml points you to a non existant class
	 */
	public static Class<?>[] getClasses(ClassLoader cl, String persitenceUnitName)
			throws IOException, ClassNotFoundException {

		URL[] persistenceUnits;

		try {
			// Find all the persistence.xml's on the classpath
			// TODO fetch these two values from the ebj3.properties file
			persistenceUnits = Classpath.search(cl, "META-INF/", "persistence.xml");
		} catch (IOException e) {
			throw new IllegalArgumentException(
					"Unexpected problem accessing the persistence.xml", e);
		}
		// if there was nothing to search for, return that as the error
		if (persistenceUnits.length < 1) {
			throw new FileNotFoundException(
					"Unable to find any persistence.xml file on the classpath!!!");
		}

		Set<Class<?>> classes = new HashSet<Class<?>>();

		for (URL url : persistenceUnits) {

			try {
				// Open and parse the persistence.xml into a Document
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				factory.setValidating(false);
				Document document = factory.newDocumentBuilder().parse(
						new File(url.toURI()));
				// serialize(document, System.out);

				// Use XPath to read all <Class> elements from the Document
				XPathFactory xpathFactory = XPathFactory.newInstance();
				XPath xPath = xpathFactory.newXPath();
				NodeList classNodes = (NodeList) xPath.evaluate(
						"//persistence-unit[@name='" + persitenceUnitName + "']/class",
						document, XPathConstants.NODESET);

				logger.info("Found " + classNodes.getLength() + " <class> elements in "
						+ url.toString());

				for (int j = 0; j < classNodes.getLength(); j++) {
					// read all <class> elements for this <persistence-unit>
					String className = classNodes.item(j).getFirstChild().getNodeValue();
					if (logger.isDebugEnabled()) {
						logger.debug("Loading class derived from persistence.xml: "
								+ className);
					}
					try {
						classes.add(Class.forName(className));
					} catch (ClassNotFoundException e) {
						logger
								.error(
										"Unable load "
												+ className
												+ ", it's not on the classpath, check your persistence.xml",
										e);
						throw e;
					}
				}
			} catch (XPathExpressionException e) {
				logger
						.fatal(
								"There's a problem with the xpath expressions in the code, this is a bug! please report it.",
								e);
			} catch (org.xml.sax.SAXException e) {
				logger.error("Unable to parse the xml in the persistence.xml file", e);
			} catch (ParserConfigurationException e) {
				logger
						.fatal(
								"Unable to configure the xml parser to read the persistence.xml, this is a BUG",
								e);
			} catch (URISyntaxException e) {
				logger
						.fatal(
								"Detected persistence.xml on the classpath but unable to load - BUG",
								e);
			}
		}
		return classes.toArray(new Class[classes.size()]);
	}
}
