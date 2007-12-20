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
public class Department implements Serializable
{
	@Id
	@GeneratedValue
	private int deptId;

	public String name;
	
	@OneToMany(mappedBy="department1")
	private List<Employee> employees1;

	@OneToMany(mappedBy="department2")
	private List<Employee> employees2;

	@OneToMany(mappedBy="department3")
	private List<Employee> employees3;

}
