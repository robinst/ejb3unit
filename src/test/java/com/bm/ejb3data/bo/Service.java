/**
 * 
 */
package com.bm.ejb3data.bo;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/** 
 * @author LIEMANS
 * @generated "UML to Java V5.0 (com.ibm.xtools.transform.uml2.java5.internal.UML2JavaTransform)"
 */
@Entity
@Table(name = "T_RD_SERVICE")
public class Service implements Serializable{
	
	/**
	 * 
	 */
	@Transient
	private static final long serialVersionUID = -7301677238392817158L;

	@Id
	@GeneratedValue
	@Column(name = "PK_ID_SERVICE", unique = true, nullable = false)
	private Integer id;
	
	@Column(name = "CODE", unique = false, nullable = true, length = 15)
	private String code;
	
	
	@Column(name = "LIBELLE", unique = false, nullable = true, length = 15)
	private String libelle;
	

	
	@ManyToOne(optional=true)
	@JoinColumn(name = "FK_ID_SOCIETE", nullable=true)
	private Societe societe;
	
	

	@ManyToOne(optional=true)
	@JoinColumn(name = "FK_ID_ETABLISSEMENT", nullable=true)
	private Etablissement etablissement;


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
	 * @return the societe
	 * @generated "UML to Java V5.0 (com.ibm.xtools.transform.uml2.java5.internal.UML2JavaTransform)"
	 */
	public Societe getSociete() {
		// begin-user-code
		return societe;
		// end-user-code
	}

	/** 
	 * @param theSociete the societe to set
	 * @generated "UML to Java V5.0 (com.ibm.xtools.transform.uml2.java5.internal.UML2JavaTransform)"
	 */
	public void setSociete(Societe theSociete) {
		// begin-user-code
		societe = theSociete;
		// end-user-code
	}

	/**
	 * Redefinition de equals
	 */
	public boolean equals(Object other) {
		if (other instanceof Service) {
			final Service otherCast = (Service) other;
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

	public Etablissement getEtablissement() {
		return etablissement;
	}

	public void setEtablissement(Etablissement etablissement) {
		this.etablissement = etablissement;
	}
}