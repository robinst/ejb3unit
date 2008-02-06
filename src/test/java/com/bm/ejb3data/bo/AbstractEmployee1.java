package com.bm.ejb3data.bo;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

/**
 * Simple abstract employee (entity) class for testing entity inheritance.
 * @see	FullTimeEmployee1
 */
@Entity
@Table(name="Employees")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
public abstract class AbstractEmployee1 {

	@Id
	@GeneratedValue
	protected int empId;

	protected String name;
	protected String fullname;

	public int getEmpId() {
		return empId;
	}
	public void setEmpId(int empId) {
		this.empId = empId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getFullname() {
		return fullname;
	}
	public void setFullname(String fullname) {
		this.fullname = fullname;
	}
	
}
