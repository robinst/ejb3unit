package com.bm.ejb3data.bo;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * Simple test class for testing the MappedSuperclass annotation with entity inheritance.
 * @see FullTimeEmployee2
 */
@MappedSuperclass
public class AbstractEmployee2 {

	@Id
	@GeneratedValue
	protected int empId;

	protected String name;
	protected String fullname;
}
