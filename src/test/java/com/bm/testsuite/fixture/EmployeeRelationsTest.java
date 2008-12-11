package com.bm.testsuite.fixture;

import javax.persistence.EntityManager;

import junit.framework.TestCase;

import com.bm.ejb3data.bo.Department;
import com.bm.ejb3data.bo.Employee;
import com.bm.ejb3guice.inject.Ejb3UnitInternalInject;
import com.bm.testsuite.dataloader.CSVInitialDataSet;
import com.bm.utils.injectinternal.InternalInjector;

/**
 * Test class for data sets that contain relations.
 * 
 * @author Peter Doornbosch
 * @see com.bm.ejb3data.bo.Employee
 * @see com.bm.ejb3data.bo.Department
 */
public class EmployeeRelationsTest extends TestCase {

	private final CSVInitialDataSet<Employee> employeeData1 = new CSVInitialDataSet<Employee>(
			Employee.class, "employeeData.csv", "empId", "name", "fullname",
			"department1");

	private final CSVInitialDataSet<Employee> employeeData2 = new CSVInitialDataSet<Employee>(
			Employee.class, "employeeData.csv", "empId", "name", "fullname",
			"department2");

	private final CSVInitialDataSet<Employee> employeeData3 = new CSVInitialDataSet<Employee>(
			Employee.class, "employeeData.csv", "empId", "name", "fullname",
			"department3");

	private final CSVInitialDataSet<Employee> employeeData4 = new CSVInitialDataSet<Employee>(
			Employee.class, "employeeData.csv", "empId", "name", "fullname",
			"department4");

	private final CSVInitialDataSet<Employee> employeeData5 = new CSVInitialDataSet<Employee>(
			Employee.class, "employeeData.csv", "empId", "name", "fullname",
			"department5");

	private final CSVInitialDataSet<Employee> employeeData6 = new CSVInitialDataSet<Employee>(
			Employee.class, "employeeData.csv", "empId", "name", "fullname",
			"department6");

	private final CSVInitialDataSet<Department> departmentData = new CSVInitialDataSet<Department>(
			Department.class, "departmentData.csv", "deptId", "name");

	@Ejb3UnitInternalInject
	private EntityManager entityManager;

	public EmployeeRelationsTest() {
		super();
	}

	public EmployeeRelationsTest(String name) {
		super(name);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void setUp() throws Exception {
		InternalInjector.createInternalInjector(Employee.class, Department.class)
				.injectMembers(this);
		super.setUp();
	}

	/**
	 * @see junit.framework.TestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception {
		employeeData1.cleanup(entityManager);
		employeeData2.cleanup(entityManager);
		employeeData3.cleanup(entityManager);
		employeeData4.cleanup(entityManager);
		employeeData5.cleanup(entityManager);
		employeeData6.cleanup(entityManager);
		departmentData.cleanup(entityManager);
	}

	/**
	 * Test SQL for department1 relation
	 */
	public void testDepartment1SQL() {
		assertEquals(
				"INSERT INTO EMPLOYEE (empId, name, fullname, department1_deptId) VALUES (?, ?, ?, ?)",
				employeeData1.buildInsertSQL()[0]);
	}

	/**
	 * Test that the data is loaded correctly
	 */
	public void testEmployeeData1() {
		departmentData.create();
		employeeData1.create();
		Employee e1 = entityManager.find(Employee.class, 1);
		assertEquals("daniel", e1.name);
		Department dept = e1.department1;
		assertNotNull(dept);
		assertEquals("IT", dept.name);
	}

	/**
	 * Test SQL for department2 relation
	 */
	public void testDepartment2SQL() {
		assertEquals(
				"INSERT INTO EMPLOYEE (empId, name, fullname, dept2) VALUES (?, ?, ?, ?)",
				employeeData2.buildInsertSQL()[0]);
	}

	/**
	 * Test SQL for department3 relation
	 */
	public void testDepartment3SQL() {
		assertEquals(
				"INSERT INTO EMPLOYEE (empId, name, fullname, dept3) VALUES (?, ?, ?, ?)",
				employeeData3.buildInsertSQL()[0]);
	}

	/**
	 * Test SQL for department4 relation
	 */
	public void testDepartment4SQL() {
		assertEquals(
				"INSERT INTO EMPLOYEE (empId, name, fullname, department4_deptId) VALUES (?, ?, ?, ?)",
				employeeData4.buildInsertSQL()[0]);
	}

	/**
	 * Test SQL for department5 relation
	 */
	public void testDepartment5SQL() {
		assertEquals(
				"INSERT INTO EMPLOYEE (empId, name, fullname, dept5) VALUES (?, ?, ?, ?)",
				employeeData5.buildInsertSQL()[0]);
	}

	/**
	 * Test SQL for department6 relation
	 */
	public void testDepartment6SQL() {
		assertEquals(
				"INSERT INTO EMPLOYEE (empId, name, fullname, dept6) VALUES (?, ?, ?, ?)",
				employeeData6.buildInsertSQL()[0]);
	}
}
