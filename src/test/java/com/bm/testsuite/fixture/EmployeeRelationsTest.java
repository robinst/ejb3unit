package com.bm.testsuite.fixture;

import javax.persistence.EntityManager;

import junit.framework.TestCase;

import com.bm.cfg.Ejb3UnitCfg;
import com.bm.ejb3data.bo.Department;
import com.bm.ejb3data.bo.Employee;
import com.bm.testsuite.dataloader.CSVInitialDataRelationSet;

/**
 * Test class for data sets that contain relations. 
 * 
 * @author Peter Doornbosch
 * @see com.bm.ejb3data.bo.Employee
 * @see com.bm.ejb3data.bo.Department
 */
public class EmployeeRelationsTest extends TestCase {

	private final CSVInitialDataRelationSet employeeData1 = new CSVInitialDataRelationSet<Employee>(
			Employee.class, "employeeData.csv", "empId", "name", "fullname", "department1");

	private final CSVInitialDataRelationSet employeeData2 = new CSVInitialDataRelationSet<Employee>(
			Employee.class, "employeeData.csv", "empId", "name", "fullname", "department2");

	private final CSVInitialDataRelationSet employeeData3 = new CSVInitialDataRelationSet<Employee>(
			Employee.class, "employeeData.csv", "empId", "name", "fullname", "department3");
	
	private final CSVInitialDataRelationSet employeeData4 = new CSVInitialDataRelationSet<Employee>(
			Employee.class, "employeeData.csv", "empId", "name", "fullname", "department4");
	
	private final CSVInitialDataRelationSet employeeData5 = new CSVInitialDataRelationSet<Employee>(
			Employee.class, "employeeData.csv", "empId", "name", "fullname", "department5");
	
	private final CSVInitialDataRelationSet employeeData6 = new CSVInitialDataRelationSet<Employee>(
			Employee.class, "employeeData.csv", "empId", "name", "fullname", "department6");
	
	private final CSVInitialDataRelationSet departmentData = new CSVInitialDataRelationSet<Department>(
			Department.class, "departmentData.csv", "deptId", "name");

	private EntityManager entityManager;
	
	public EmployeeRelationsTest() {
		super();
	}

	public EmployeeRelationsTest(String name) {
		super(name);
	}

	@Override
	public void setUp() {
		Ejb3UnitCfg.addEntytiesToTest(Employee.class);
		Ejb3UnitCfg.addEntytiesToTest(Department.class);
		entityManager = Ejb3UnitCfg.getConfiguration().getEntityManagerFactory().createEntityManager();
	}

	/**
	 * @see junit.framework.TestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception {
		employeeData1.cleanup(entityManager);
		departmentData.cleanup(entityManager);
	}
	
	/**
	 *	Test SQL for department1 relation
	 */
	public void testDepartment1SQL() {
		assertEquals("INSERT INTO EMPLOYEE (empId, name, fullname, department1_deptId) VALUES (?, ?, ?, ?)", 
				employeeData1.buildInsertSQL());
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
	 *	Test SQL for department2 relation
	 */
	public void testDepartment2SQL() {
		assertEquals("INSERT INTO EMPLOYEE (empId, name, fullname, dept2) VALUES (?, ?, ?, ?)", 
				employeeData2.buildInsertSQL());
	}
	
	/**
	 *	Test SQL for department3 relation
	 */
	public void testDepartment3SQL() {
		assertEquals("INSERT INTO EMPLOYEE (empId, name, fullname, dept3) VALUES (?, ?, ?, ?)", 
				employeeData3.buildInsertSQL());
	}

	/**
	 *	Test SQL for department4 relation
	 */
	public void testDepartment4SQL() {
		assertEquals("INSERT INTO EMPLOYEE (empId, name, fullname, department4_deptId) VALUES (?, ?, ?, ?)", 
				employeeData4.buildInsertSQL());
	}

	/**
	 *	Test SQL for department5 relation
	 */
	public void testDepartment5SQL() {
		assertEquals("INSERT INTO EMPLOYEE (empId, name, fullname, dept5) VALUES (?, ?, ?, ?)", 
				employeeData5.buildInsertSQL());
	}
	
	/**
	 *	Test SQL for department6 relation
	 */
	public void testDepartment6SQL() {
		assertEquals("INSERT INTO EMPLOYEE (empId, name, fullname, dept6) VALUES (?, ?, ?, ?)", 
				employeeData6.buildInsertSQL());
	}
}
