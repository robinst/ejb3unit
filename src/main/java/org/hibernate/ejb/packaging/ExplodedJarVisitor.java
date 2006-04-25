//$Id: ExplodedJarVisitor.java,v 1.1 2006/04/17 12:11:12 daniel_wiese Exp $
package org.hibernate.ejb.packaging;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.BufferedInputStream;
import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Emmanuel Bernard
 */
public class ExplodedJarVisitor extends JarVisitor {
	private static Log log = LogFactory.getLog( ExplodedJarVisitor.class );

	public ExplodedJarVisitor(URL url, Filter[] filters) {
		super(url, filters);
	}

	public ExplodedJarVisitor(String fileName, Filter[] filters) {
		super(fileName, filters);
	}

	protected void doProcessElements() throws IOException {
		File jarFile;
		jarFile = new File( jarUrl.getFile() );
		if ( !jarFile.exists() ) {
			log.warn( "Exploded jar does not exists (ignored): " + jarUrl );
			return;
		}
		if ( !jarFile.isDirectory() ) {
			log.warn( "Exploded jar file not a directory (ignored): " + jarUrl );
			return;
		}
		getClassNamesInTree( jarFile, null );
	}

	private void getClassNamesInTree(File jarFile, String header) throws IOException {
		File[] files = jarFile.listFiles();
		header = header == null ? "" : header + "/";
		for ( File localFile : files ) {
			if ( !localFile.isDirectory() ) {
				String entryName = localFile.getName();
				addElement( header + entryName,
						new BufferedInputStream( new FileInputStream( localFile ) ),
						new BufferedInputStream( new FileInputStream( localFile ) )
				);

			}
			else {
				getClassNamesInTree( localFile, header + localFile.getName() );
			}
		}
	}
}
