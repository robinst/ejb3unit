package com.bm.ejb3data.bo;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="Employees")
public class FullTimeEmployee2 extends AbstractEmployee2 {

	protected Integer salary;
	
	public Integer getSalary() {
		return salary;
	}

	public void setSalary(Integer salary) {
		this.salary = salary;
	}
}
