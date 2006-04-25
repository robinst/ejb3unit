package org.hibernate.reflection.java;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;
import javax.persistence.Embeddable;

import org.dom4j.Element;
import org.hibernate.annotationfactory.AnnotationDescriptor;
import org.hibernate.annotationfactory.AnnotationFactory;
import org.hibernate.reflection.java.xml.XMLContext;

/**
 * Encapsulates the overriding of Java annotations from an EJB 3.0 descriptor.
 * 
 * @author Paolo Perrotta
 * @author Davide Marchignoli
 */
class EJB3ClassAnnotationReader extends JavaAnnotationReader {
	private XMLContext xmlContext;
	private String className;
	private static final Map<Class, String> annotationToXml;

	static {
		annotationToXml = new HashMap<Class, String>();
		annotationToXml.put(Entity.class, "entity");
		annotationToXml.put(MappedSuperclass.class, "mapped-superclass");
		annotationToXml.put(Embeddable.class, "embeddable");
	}

	public EJB3ClassAnnotationReader( AnnotatedElement el, XMLContext xmlContext ) {
		super( el );
		this.xmlContext = xmlContext;
		if (el instanceof Class) {
			Class clazz = (Class) el;
			className = clazz.getName();
		}
	}

	public <T extends Annotation> T getAnnotation(Class<T> annotationType) {

		return super.getAnnotation( annotationType );
	}

	public <T extends Annotation> boolean isAnnotationPresent(Class<T> annotationType) {
		return super.isAnnotationPresent( annotationType );
	}

	public Annotation[] getAnnotations() {
		XMLContext.Default defaults = xmlContext.getDefault(className);
		Element tree = xmlContext.getXMLTree( className, null);
		Annotation[] annotations = super.getAnnotations();
		List<Annotation> annotationList = new ArrayList<Annotation>(annotations.length + 5);
		for (Annotation annotation : annotations) {
			if (! annotationToXml.containsKey( annotation ) ) {
				//unknown annotations are left over
				annotationList.add(annotation);
			}
		}
		Annotation current = getEntity(tree, defaults);
		if (current != null) annotationList.add(current);
		return super.getAnnotations();
	}

	private Entity getEntity(Element tree, XMLContext.Default defaults) {
		if (tree == null) {
			return Boolean.TRUE.equals( defaults.getMetadataComplete() ) ? super.getAnnotation( Entity.class ) : null;
		}
		else {
			if ( "entity".equals( tree.getName() ) ) {
				AnnotationDescriptor entity = new AnnotationDescriptor( Entity.class );
				entity.setValue( "name", tree.attributeValue( "name", "" ) );
				return AnnotationFactory.create( entity );
			}
			else {
				return null; //this is not an entity
			}
		}
	}

	private Annotation getTopLevelElement(Class<Annotation> annotation, Element tree, XMLContext.Default defaults) {
		if (tree == null) {
			return getAnnotation( annotation );
		}
		else {
			if ( tree.getName().equals( annotationToXml.get( annotation ) ) ) {
				//TODO resume ***********************************
				return null;
			}
			else {
				return null; //this is not an entity
			}
		}
	}


}
