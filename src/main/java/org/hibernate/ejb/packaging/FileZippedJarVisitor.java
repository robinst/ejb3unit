//$Id: FileZippedJarVisitor.java,v 1.1 2006/04/17 12:11:12 daniel_wiese Exp $
package org.hibernate.ejb.packaging;

import java.io.IOException;
import java.io.BufferedInputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Work on a JAR that can be accessed through a File
 *
 * @author Emmanuel Bernard
 */
public class FileZippedJarVisitor extends JarVisitor {
	private static Log log = LogFactory.getLog( FileZippedJarVisitor.class );

	protected FileZippedJarVisitor(String fileName, Filter[] filters) {
		super( fileName, filters );
	}

	protected FileZippedJarVisitor(URL url, Filter[] filters) {
		super( url, filters );
	}

	protected void doProcessElements()  throws IOException {
		JarFile jarFile;
		try {
			jarFile = new JarFile( jarUrl.getFile() );
		}
		catch (IOException ze) {
			log.warn( "Unable to find file (ignored): " + jarUrl, ze );
			return;
		}
		Enumeration<? extends ZipEntry> entries = jarFile.entries();
		while ( entries.hasMoreElements() ) {
			ZipEntry entry = entries.nextElement();
			if ( !entry.isDirectory() ) {
				addElement( entry.getName(),
						new BufferedInputStream( jarFile.getInputStream( entry ) ),
						new BufferedInputStream( jarFile.getInputStream( entry ) )
				);
			}
		}
	}
}
