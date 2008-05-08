package com.bm.ejb3data.bo;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

/**
 * Simple Department class for testing relations
 * 
 */
@Entity
public class Department implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private int deptId;

	public String name;

	@OneToMany(mappedBy = "department1")
	private List<Employee> employees1;

	@OneToMany(mappedBy = "department2")
	private List<Employee> employees2;

	@OneToMany(mappedBy = "department3")
	private List<Employee> employees3;

	@OneToMany(mappedBy = "department4", targetEntity = Employee.class)
	private List employees4;

	/**
	 * Getter to return the serialVersionUID.
	 * 
	 * @return the serialVersionUID
	 */
	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	/**
	 * Getter to return the deptId.
	 * 
	 * @return the deptId
	 */
	public int getDeptId() {
		return deptId;
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
	 * Getter to return the employees1.
	 * 
	 * @return the employees1
	 */
	public List<Employee> getEmployees1() {
		return employees1;
	}

	/**
	 * Getter to return the employees2.
	 * 
	 * @return the employees2
	 */
	public List<Employee> getEmployees2() {
		return employees2;
	}

	/**
	 * Getter to return the employees3.
	 * 
	 * @return the employees3
	 */
	public List<Employee> getEmployees3() {
		return employees3;
	}

}
