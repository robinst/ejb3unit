package org.hibernate.ejb.packaging;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import javax.persistence.spi.PersistenceUnitTransactionType;

/**
 * Simple represenation of persistence.xml
 *
 * @author <a href="mailto:bill@jboss.org">Bill Burke</a>
 * @version $Revision: 1.1 $
 */
public class PersistenceMetadata {

	private String name;
	private String nonJtaDatasource;
	private String jtaDatasource;
	private String provider;
	private PersistenceUnitTransactionType transactionType = PersistenceUnitTransactionType.JTA;
	private List<String> classes = new ArrayList<String>();
	private List<String> packages = new ArrayList<String>();
	private List<String> mappingFiles = new ArrayList<String>();
	private Set<String> jarFiles = new HashSet<String>();
	private List<InputStream> hbmfiles = new ArrayList<InputStream>();
	private Properties props = new Properties();
	private boolean excludeUnlistedClasses = false;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public PersistenceUnitTransactionType getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(PersistenceUnitTransactionType transactionType) {
		this.transactionType = transactionType;
	}

	public String getNonJtaDatasource() {
		return nonJtaDatasource;
	}

	public void setNonJtaDatasource(String nonJtaDatasource) {
		this.nonJtaDatasource = nonJtaDatasource;
	}

	public String getJtaDatasource() {
		return jtaDatasource;
	}

	public void setJtaDatasource(String jtaDatasource) {
		this.jtaDatasource = jtaDatasource;
	}

	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		if ( provider != null && provider.endsWith( ".class" ) ) {
			this.provider = provider.substring( 0, provider.length() - 6 );
		}
		this.provider = provider;
	}

	public List<String> getClasses() {
		return classes;
	}

	public void setClasses(List<String> classes) {
		this.classes = classes;
	}

	public List<String> getPackages() {
		return packages;
	}

	public void setPackages(List<String> packages) {
		this.packages = packages;
	}

	public List<String> getMappingFiles() {
		return mappingFiles;
	}

	public void setMappingFiles(List<String> mappingFiles) {
		this.mappingFiles = mappingFiles;
	}

	public Set<String> getJarFiles() {
		return jarFiles;
	}

	public void setJarFiles(Set<String> jarFiles) {
		this.jarFiles = jarFiles;
	}

	public Properties getProps() {
		return props;
	}

	public void setProps(Properties props) {
		this.props = props;
	}

	public List<InputStream> getHbmfiles() {
		return hbmfiles;
	}

	public void setHbmfiles(List<InputStream> hbmfiles) {
		this.hbmfiles = hbmfiles;
	}

	public boolean getExcludeUnlistedClasses() {
		return excludeUnlistedClasses;
	}

	public void setExcludeUnlistedClasses(boolean excludeUnlistedClasses) {
		this.excludeUnlistedClasses = excludeUnlistedClasses;
	}
}
