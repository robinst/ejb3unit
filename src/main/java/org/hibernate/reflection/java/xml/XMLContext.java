//$Id: XMLContext.java,v 1.1 2006/04/17 12:11:13 daniel_wiese Exp $
package org.hibernate.reflection.java.xml;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.hibernate.util.StringHelper;

/**
 * @author Emmanuel Bernard
 */
public class XMLContext {
	private static Log log = LogFactory.getLog( XMLContext.class );
	private Default globalDefaults;
	private Map<String,Element> classOverriding = new HashMap<String, Element>();
	private Map<String,Default> defaultsOverriding = new HashMap<String, Default>();

	public void addDocument(Document doc) {
		Element root = doc.getRootElement();

		//global defaults
		Element metadata = root.element( "persistence-unit-metadata" );
		if (metadata != null) {
			if (globalDefaults == null) {
				globalDefaults = new Default();
				globalDefaults.setMetadataComplete(
						metadata.element( "xml-mapping-metadata-complete" ) != null ?
								Boolean.TRUE :
								null
				);
				Element defaultElement = metadata.element( "persistence-unit-defaults" );
				Element unitElement = defaultElement.element( "schema" );
				globalDefaults.setSchema( unitElement != null ? unitElement.getTextTrim() : null );
				unitElement = defaultElement.element( "catalog" );
				globalDefaults.setCatalog( unitElement != null ? unitElement.getTextTrim() : null );
				unitElement = defaultElement.element( "access" );
				globalDefaults.setAccess( unitElement != null ? unitElement.getTextTrim() : null );
				unitElement = defaultElement.element( "cascade-persist" );
				globalDefaults.setCascadePersist( unitElement != null ?	Boolean.TRUE : null	);
				//TODO entity listeners
			}
			else {
				log.warn("Found more than one <persistence-unit-metadata>, ignored");
			}
		}

		//entity mapping default
		Default entityMappingDefault = new Default();
		Element unitElement = root.element("package");
		String packageName = unitElement != null ? unitElement.getTextTrim() : "";
		entityMappingDefault.setPackageName( packageName );
		unitElement = root.element("schema");
		entityMappingDefault.setSchema( unitElement != null ? unitElement.getTextTrim() : null );
		unitElement = root.element("catalog");
		entityMappingDefault.setCatalog( unitElement != null ? unitElement.getTextTrim() : null );
		unitElement = root.element("access");
		entityMappingDefault.setAccess( unitElement != null ? unitElement.getTextTrim() : null );


		List<Element> entities = (List<Element>) root.elements( "entity" );
		addClass( entities, packageName, entityMappingDefault );

		entities = (List<Element>) root.elements( "mapped-superclass" );
		addClass( entities, packageName, entityMappingDefault );

		entities = (List<Element>) root.elements( "embeddable" );
		addClass( entities, packageName, entityMappingDefault );
	}

	private void addClass(List<Element> entities, String packageName, Default defaults) {
		for (Element element : entities) {
			Attribute attribute = element.attribute( "class" );
			String className = buildSafeClassName( attribute.getText(), packageName );
			if ( classOverriding.containsKey( className ) ) {
				//maybe switch it to warn?
				throw new IllegalStateException( "Duplicate XML entry for " + className);
			}
			classOverriding.put( className, element );
			Default localDefault = new Default();
			localDefault.override( defaults );
			attribute = element.attribute( "metadata-complete" );
			if (attribute != null) localDefault.setMetadataComplete( (Boolean) attribute.getData() );
			defaultsOverriding.put( className, localDefault );

			log.debug( "Adding XML overriding information for " + className);
		}
	}

	private static String buildSafeClassName(String className, String defaultPackageName) {
		if ( className.indexOf( '.' ) < 0 && defaultPackageName != null) {
			className = StringHelper.qualify(defaultPackageName, className);
		}
		return className;
	}

	public Default getDefault(String className) {
		Default xmlDefault = new Default();
		xmlDefault.override( globalDefaults );
		Default entityMappingOverriding = defaultsOverriding.get( className );
		xmlDefault.override( entityMappingOverriding );
		return xmlDefault;
	}

	public Element getXMLTree(String className, String methodName) {
		return classOverriding.get(className);
	}

	public static class Default {
		private String access;
		private String packageName;
		private String schema;
		private String catalog;
		private Boolean metadataComplete;
		private Boolean cascadePersist;

		public String getAccess() {
			return access;
		}

		protected void setAccess(String access) {
			this.access = access;
		}

		public String getCatalog() {
			return catalog;
		}

		protected void setCatalog(String catalog) {
			this.catalog = catalog;
		}

		public String getPackageName() {
			return packageName;
		}

		protected void setPackageName(String packageName) {
			this.packageName = packageName;
		}

		public String getSchema() {
			return schema;
		}

		protected void setSchema(String schema) {
			this.schema = schema;
		}

		public Boolean getMetadataComplete() {
			return metadataComplete;
		}

		protected void setMetadataComplete(Boolean metadataComplete) {
			this.metadataComplete = metadataComplete;
		}

		public Boolean getCascadePersist() {
			return cascadePersist;
		}

		void setCascadePersist(Boolean cascadePersist) {
			this.cascadePersist = cascadePersist;
		}

		public void override(Default globalDefault) {
			if (globalDefault != null) {
				if ( globalDefault.getAccess() != null) access = globalDefault.getAccess();
				if ( globalDefault.getPackageName() != null) packageName = globalDefault.getPackageName();
				if ( globalDefault.getSchema() != null) schema = globalDefault.getSchema();
				if ( globalDefault.getCatalog() != null) catalog = globalDefault.getCatalog();
				if ( globalDefault.getMetadataComplete() != null) metadataComplete = globalDefault.getMetadataComplete();
				//TODO fix that in stone if cascade-persist is set already?
				if ( globalDefault.getCascadePersist() != null) cascadePersist = globalDefault.getCascadePersist();
			}
		}

	}
}
