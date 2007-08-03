/**
 * 
 */
package com.bm.data.bo;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/** 
 * @author LIEMANS
 * @generated "UML to Java V5.0 (com.ibm.xtools.transform.uml2.java5.internal.UML2JavaTransform)"
 */
@Entity
@Table(name = "T_RD_SOCIETE")
public class Societe {
	@Id
	@GeneratedValue
	@Column(name = "PK_ID_SOCIETE", unique = true, nullable = false)
	private Integer id;
	
	@Column(name = "SK_CODE", unique = false, nullable = true, length = 4)
	private String code;
	
	@Column(name = "LABEL", unique = false, nullable = true, length = 30)
	private String libelle;
	
	@OneToMany(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY, mappedBy = "societe")
	private List<Etablissement> etablissements;
	
	
	@OneToMany(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY, mappedBy = "societe")
	private List<Service> services;
	

	/** 
	 * @return the establishment
	 * @generated "UML to Java V5.0 (com.ibm.xtools.transform.uml2.java5.internal.UML2JavaTransform)"
	 */
	public List<Etablissement> getEtablissements() {
		// begin-user-code
		return etablissements;
		// end-user-code
	}

	/** 
	 * @param theEstablishment the establishment to set
	 * @generated "UML to Java V5.0 (com.ibm.xtools.transform.uml2.java5.internal.UML2JavaTransform)"
	 */
	public void setEtablissements(List<Etablissement> theEstablishment) {
		// begin-user-code
		etablissements = theEstablishment;
		// end-user-code
	}

	/** 
	 * @return the service
	 * @generated "UML to Java V5.0 (com.ibm.xtools.transform.uml2.java5.internal.UML2JavaTransform)"
	 */
	public List<Service> getServices() {
		// begin-user-code
		return services;
		// end-user-code
	}

	/** 
	 * @param theService the service to set
	 * @generated "UML to Java V5.0 (com.ibm.xtools.transform.uml2.java5.internal.UML2JavaTransform)"
	 */
	public void setServices(List<Service> theService) {
		// begin-user-code
		services = theService;
		// end-user-code
	}


	/** 
	 * @return the label
	 * @generated "UML to Java V5.0 (com.ibm.xtools.transform.uml2.java5.internal.UML2JavaTransform)"
	 */
	public String getLibelle() {
		// begin-user-code
		return libelle;
		// end-user-code
	}

	/** 
	 * @param theLabel the label to set
	 * @generated "UML to Java V5.0 (com.ibm.xtools.transform.uml2.java5.internal.UML2JavaTransform)"
	 */
	public void setLibelle(String theLabel) {
		// begin-user-code
		libelle = theLabel;
		// end-user-code
	}


	/** 
	 * @return the code
	 * @generated "UML to Java V5.0 (com.ibm.xtools.transform.uml2.java5.internal.UML2JavaTransform)"
	 */
	public String getCode() {
		// begin-user-code
		return code;
		// end-user-code
	}

	/** 
	 * @param theCode the code to set
	 * @generated "UML to Java V5.0 (com.ibm.xtools.transform.uml2.java5.internal.UML2JavaTransform)"
	 */
	public void setCode(String theCode) {
		// begin-user-code
		code = theCode;
		// end-user-code
	}

	/** 
	 * @return the id
	 * @generated "UML to Java V5.0 (com.ibm.xtools.transform.uml2.java5.internal.UML2JavaTransform)"
	 */
	public Integer getId() {
		// begin-user-code
		return id;
		// end-user-code
	}

	/** 
	 * @param theId the id to set
	 * @generated "UML to Java V5.0 (com.ibm.xtools.transform.uml2.java5.internal.UML2JavaTransform)"
	 */
	public void setId(Integer theId) {
		// begin-user-code
		id = theId;
		// end-user-code
	}





	/**
	 * Redefinition de equals
	 */
	public boolean equals(Object other) {
		if (other instanceof Societe) {
			final Societe otherCast = (Societe) other;
			final EqualsBuilder builder = new EqualsBuilder();
			builder.append(this.getCode(), otherCast.getCode());
			builder.append(this.getLibelle(), otherCast.getLibelle());
			return builder.isEquals();

		} else {
			return false;
		}
	}
	
	/**
	 * Redefinition de hashCode
	 */
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder(17, 37);
		builder.append(this.getCode());
		builder.append(this.getLibelle());
		return builder.toHashCode();
	}

}