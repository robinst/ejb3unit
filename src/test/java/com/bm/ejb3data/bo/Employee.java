package com.bm.ejb3data.bo;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * Simple Employee class for testing relations.
 */
@Entity
public class Employee implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private int empId;

	public String name;
	public String fullname;

	// @JoinTable(name="empdept", joinColumns=@JoinColumn(name="empid",
	// nullable=true), inverseJoinColumns=@JoinColumn(name="deptid",
	// nullable=true))

	// The most simple, straight-forward (bi-directional) many-to-one relation
	@ManyToOne
	public Department department1;

	// Same as before, now with a simple join-column annotation
	@ManyToOne()
	@JoinColumn(name = "dept2")
	public Department department2;

	// Same as before, now with the annotations in reversed order
	@JoinColumn(name = "dept3")
	@ManyToOne()
	public Department department3;

	// The most simple, straight-forward (unidirectional) many-to-one relation
	@ManyToOne
	private Department department4;

	// Same as before, now with a simple join-column annotation
	@ManyToOne()
	@JoinColumn(name = "dept5")
	private Department department5;

	// Same as before, now with the annotations in reversed order
	@JoinColumn(name = "dept6")
	@ManyToOne()
	private Department department6;

	/**
	 * Getter to return the serialVersionUID.
	 * 
	 * @return the serialVersionUID
	 */
	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	/**
	 * Getter to return the empId.
	 * 
	 * @return the empId
	 */
	public int getEmpId() {
		return empId;
	}

	/**
	 * Getter to return the name.
	 * 
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Getter to return the fullname.
	 * 
	 * @return the fullname
	 */
	public String getFullname() {
		return fullname;
	}

	/**
	 * Getter to return the department1.
	 * 
	 * @return the department1
	 */
	public Department getDepartment1() {
		return department1;
	}

	/**
	 * Getter to return the department2.
	 * 
	 * @return the department2
	 */
	public Department getDepartment2() {
		return department2;
	}

	/**
	 * Getter to return the department3.
	 * 
	 * @return the department3
	 */
	public Department getDepartment3() {
		return department3;
	}

	/**
	 * Getter to return the department4.
	 * 
	 * @return the department4
	 */
	public Department getDepartment4() {
		return department4;
	}

	/**
	 * Getter to return the department5.
	 * 
	 * @return the department5
	 */
	public Department getDepartment5() {
		return department5;
	}

	/**
	 * Getter to return the department6.
	 * 
	 * @return the department6
	 */
	public Department getDepartment6() {
		return department6;
	}

}
