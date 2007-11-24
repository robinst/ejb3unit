package com.bm.introspectors;

import com.bm.ejb3metadata.annotations.metadata.ClassAnnotationMetadata;
import com.bm.ejb3metadata.annotations.metadata.MetaDataCache;

/**
 * SessionBeanIntrospector.
 * 
 * @author Daniel Wiese
 * @param <T> -
 *            the type of the session bean
 * @since 08.11.2005
 */
public class SessionBeanIntrospector<T> extends AbstractIntrospector<T> {

	/**
	 * Constructor.
	 * 
	 * @param toInspect -
	 *            the sesion bean to inspect
	 */
	public SessionBeanIntrospector(Class<? extends T> toInspect) {
		super(toInspect);

		boolean isSessionBean = accept(toInspect);
		if (!isSessionBean) {
			throw new RuntimeException(
					"The class is not a session/service/mdb bean");
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
	public static boolean accept(Class toCheck) {
		ClassAnnotationMetadata classMeta = MetaDataCache.getMetaData(toCheck);
		return classMeta.isBean();
	}

}
