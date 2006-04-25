//$Id: InterceptFieldClassFileTransformer.java,v 1.1 2006/04/17 12:11:09 daniel_wiese Exp $
package org.hibernate.ejb.instrument;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.Arrays;
import java.util.List;

import net.sf.cglib.core.ClassNameReader;
import net.sf.cglib.core.DebuggingClassWriter;
import net.sf.cglib.transform.ClassReaderGenerator;
import net.sf.cglib.transform.ClassTransformer;
import net.sf.cglib.transform.TransformingClassGenerator;
import net.sf.cglib.transform.impl.InterceptFieldEnabled;
import net.sf.cglib.transform.impl.InterceptFieldFilter;
import net.sf.cglib.transform.impl.InterceptFieldTransformer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Type;
import org.objectweb.asm.attrs.Attributes;

/**
 * Enhance the classes allowing them to implements InterceptFieldEnabled
 * This interface is then used by Hibernate for some optimizations.
 *
 * @author Emmanuel Bernard
 */
public class InterceptFieldClassFileTransformer implements javax.persistence.spi.ClassTransformer {
	Log log = LogFactory.getLog( InterceptFieldClassFileTransformer.class.getName() );
	private List<String> entities;

	public InterceptFieldClassFileTransformer(List<String> entities) {
		this.entities = entities;
	}

	public byte[]
			transform(
			ClassLoader loader, String className, Class<?>			classBeingRedefined,
			ProtectionDomain protectionDomain, byte[]			  classfileBuffer
	) throws IllegalClassFormatException {
		ClassReader reader;
		try {
			reader = new ClassReader( new ByteArrayInputStream( classfileBuffer ) );
		}
		catch (IOException e) {
			log.error( "Unable to read class", e );
			throw new IllegalClassFormatException( "Unable to read class: " + e.getMessage() );
		}

//		if ( className.startsWith( "net/sf/cglib")
//				|| className.startsWith( "org/hibernate/ejb/instrument")
//				|| className.startsWith( "org/hibernate/proxy")
//				|| className.startsWith( "org/hibernate/intercept") ) {
//			return classfileBuffer;
//		}
		
		if ( ! entities.contains( className.replace( "/", "." ) ) ) return classfileBuffer;

		String name[] = ClassNameReader.getClassInfo( reader );
		ClassWriter w = new DebuggingClassWriter( true );
		ClassTransformer t = getClassTransformer( name );
		if ( t != null ) {
			if ( log.isDebugEnabled() ) {
				log.info( "Enhancing " + className );
			}
			ByteArrayOutputStream out;
			byte[] result;
			try {
				reader = new ClassReader( new ByteArrayInputStream( classfileBuffer ) );
				new TransformingClassGenerator(
						new ClassReaderGenerator(
								reader,
								attributes(), skipDebug()
						), t
				).generateClass( w );
				out = new ByteArrayOutputStream();
				out.write( w.toByteArray() );
				result = out.toByteArray();
				out.close();
			}
			catch (Exception e) {
				log.error( "Unable to transform class", e );
				throw new IllegalClassFormatException( "Unable to transform class: " + e.getMessage() );
			}
			return result;
		}
		return classfileBuffer;
	}

	private Attribute[] attributes() {
		return Attributes.getDefaultAttributes();
	}

	private boolean skipDebug() {
		return false;
	}

	private ClassTransformer getClassTransformer(String[] classInfo) {

		if ( Arrays.asList( classInfo ).contains( InterceptFieldEnabled.class.getName() ) ) {
			return null;
		}
		else {
			return new InterceptFieldTransformer(
					new InterceptFieldFilter() {
						public boolean acceptRead(Type owner, String name) {
							return true;
						}

						public boolean acceptWrite(Type owner, String name) {
							return true;
						}
					}
			);
		}

	}
}
