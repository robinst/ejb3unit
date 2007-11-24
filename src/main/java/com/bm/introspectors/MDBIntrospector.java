package com.bm.introspectors;

import org.jboss.annotation.ejb.Consumer;

import com.bm.ejb3metadata.annotations.metadata.ClassAnnotationMetadata;
import com.bm.ejb3metadata.annotations.metadata.MetaDataCache;

/**
 * Introspector for MDB (supports also jboss mdb.
 * @author Daniel Wiese
 * @since 24.03.2007
 * @param <T> the type
 */
public class MDBIntrospector<T> extends AbstractIntrospector<T> {

	/**
	 * Constructor.
	 * @param toInspect the class to inspect
	 */
	public MDBIntrospector(Class<? extends T> toInspect) {
		super(toInspect);
		
		boolean isSessionBean = accept(toInspect);
		if (!isSessionBean) {
			throw new RuntimeException(
					"The class is not a mdb bean");
		}

		// FIXME: distinguish between field and method based annotations
		// analyse the fields
		this.processAccessTypeField(toInspect);

	}

	/**
	 * Returns true is this intorspector accept this class.
	 * 
	 * @param toCheck -
	 *            to check
	 * @return true id the introspector will accept this class
	 */
	@SuppressWarnings("unchecked")
	public static boolean accept(Class toCheck) {
		ClassAnnotationMetadata classMeta = MetaDataCache.getMetaData(toCheck);
		return classMeta.isMdb()
				|| (toCheck.getAnnotation(Consumer.class) != null);
	}
}
