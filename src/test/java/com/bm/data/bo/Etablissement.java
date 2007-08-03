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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * @author LIEMANS
 * @generated "UML to Java V5.0
 *            (com.ibm.xtools.transform.uml2.java5.internal.UML2JavaTransform)"
 */
@Entity
@Table(name = "T_RD_ETABLISSEMENT")
public class Etablissement {

	@Id
	@GeneratedValue
	@Column(name = "PK_ID_ETABLISSEMENT", unique = true, nullable = false)
	private Long id;

	@Column(name = "SK_CODE", unique = false, nullable = true, length = 4)
	private String code;
	
	@Column(name = "LABEL", unique = false, nullable = true, length = 30)
	private String libelle;

	@ManyToOne(optional=true)
	@JoinColumn(name = "FK_ID_SOCIETE", nullable=true)
	private Societe societe;

	@OneToMany(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
	@JoinColumn(name = "FK_ID_ETABLISSEMENT", nullable=true)
	private List<Service> services;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the label
	 * @generated "UML to Java V5.0
	 *            (com.ibm.xtools.transform.uml2.java5.internal.UML2JavaTransform)"
	 */
	public String getLibelle() {
		// begin-user-code
		return libelle;
		// end-user-code
	}

	/**
	 * @param theLabel
	 *            the label to set
	 * @generated "UML to Java V5.0
	 *            (com.ibm.xtools.transform.uml2.java5.internal.UML2JavaTransform)"
	 */
	public void setLibelle(String theLabel) {
		// begin-user-code
		libelle = theLabel;
		// end-user-code
	}

	/**
	 * @return the societe
	 * @generated "UML to Java V5.0
	 *            (com.ibm.xtools.transform.uml2.java5.internal.UML2JavaTransform)"
	 */
	public Societe getSociete() {
		// begin-user-code
		return societe;
		// end-user-code
	}

	/**
	 * @param theSociete
	 *            the societe to set
	 * @generated "UML to Java V5.0
	 *            (com.ibm.xtools.transform.uml2.java5.internal.UML2JavaTransform)"
	 */
	public void setSociete(Societe theSociete) {
		// begin-user-code
		societe = theSociete;
		// end-user-code
	}

	public List<Service> getServices() {
		return services;
	}

	public void setServices(List<Service> services) {
		this.services = services;
	}
	

	/**
	 * Getter to return the code.
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * Setter to set the code to set.
	 * @param code the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * Redefinition de equals
	 */
	public boolean equals(Object other) {
		if (other instanceof Etablissement) {
			final Etablissement otherCast = (Etablissement) other;
			final EqualsBuilder builder = new EqualsBuilder();
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
		builder.append(this.getLibelle());
		return builder.toHashCode();
	}

}