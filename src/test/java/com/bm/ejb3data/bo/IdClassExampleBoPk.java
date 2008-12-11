package com.bm.ejb3data.bo;

import java.io.Serializable;
import javax.persistence.Column;

public class IdClassExampleBoPk implements Serializable {
	
	private static final long serialVersionUID = 1L;

        // The Column annotation _must_ be present in PK class
        // and it is ignored in the entity class due to a Hibernate bug.
        // Specs say the opposite.
        
        //@Column(name = "wkn_id", nullable = false)
	private int wkn;
        //@Column(name = "day_id", nullable = false)
	private short day;
        //@Column(name = "framenr_id", nullable = false)
	private short framenr;

	public int getWkn() {
		return wkn;
	}

	public void setWkn(int wkn) {
		this.wkn = wkn;
	}

	public short getDay() {
		return day;
	}

	public void setDay(short day) {
		this.day = day;
	}

	public short getFramenr() {
		return framenr;
	}

	public void setFramenr(short frameNr) {
		this.framenr = frameNr;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + day;
		result = prime * result + framenr;
		result = prime * result + wkn;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		IdClassExampleBoPk other = (IdClassExampleBoPk) obj;
		if (day != other.day)
			return false;
		if (framenr != other.framenr)
			return false;
		if (wkn != other.wkn)
			return false;
		return true;
	}

}
