package com.bm.ejb3data.bo;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@NamedQuery(name = "ExpertiseAreas.findAll", query = "select o from ExpertiseAreas o")
@Table(name = "EXPERTISE_AREAS")
@IdClass(ExpertiseAreasPK.class)
public class ExpertiseAreas implements Serializable {
	private static final long serialVersionUID = 1L;
	@Column(name = "EXPERTISE_LEVEL", nullable = false)
	private String expertiseLevel;
	private String notes;
	@Id
	@Column(name = "PROD_ID", nullable = false, insertable = false, updatable = false)
	private Long prodId;
	@Id
	@Column(name = "USER_ID", nullable = false, insertable = false, updatable = false)
	private Long userId;

	public ExpertiseAreas() {
	}

	public String getExpertiseLevel() {
		return expertiseLevel;
	}

	public void setExpertiseLevel(String expertiseLevel) {
		this.expertiseLevel = expertiseLevel;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public Long getProdId() {
		return prodId;
	}

	public void setProdId(Long prodId) {
		this.prodId = prodId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((prodId == null) ? 0 : prodId.hashCode());
		result = prime * result + ((userId == null) ? 0 : userId.hashCode());
		return result;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final ExpertiseAreas other = (ExpertiseAreas) obj;
		if (prodId == null) {
			if (other.prodId != null)
				return false;
		} else if (!prodId.equals(other.prodId))
			return false;
		if (userId == null) {
			if (other.userId != null)
				return false;
		} else if (!userId.equals(other.userId))
			return false;
		return true;
	}

}