package com.bm.ejb3data.bo;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

/**
 * Simple abstract employee (entity) class for testing entity inheritance with
 * an integer discriminator type.
 * @see	FullTimeEmployee3
 */
@Entity
@Table(name="Employees3")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="type", discriminatorType=DiscriminatorType.INTEGER)
public abstract class AbstractEmployee3 {

	@Id
	@GeneratedValue
	protected int empId;

	protected String name;
	protected String fullname;
	
	public String getName() {
		return name;
	}
}
