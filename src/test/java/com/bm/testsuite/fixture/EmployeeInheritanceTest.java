package com.bm.testsuite.fixture;

import javax.persistence.EntityManager;

import junit.framework.TestCase;

import com.bm.cfg.Ejb3UnitCfg;
import com.bm.ejb3data.bo.FullTimeEmployee1;
import com.bm.ejb3data.bo.FullTimeEmployee2;
import com.bm.ejb3data.bo.FullTimeEmployee3;
import com.bm.testsuite.dataloader.CSVInitialDataSet;
import com.bm.utils.AccessType;
import com.bm.utils.AccessTypeFinder;

/**
 * Test class for entity inheritance.
 * 
 * @author Peter Doornbosch
 * @see com.bm.ejb3data.bo.FullTimeEmployee1
 * @see com.bm.ejb3data.bo.AbstractEmployee1
 */
public class EmployeeInheritanceTest extends TestCase {

	private EntityManager entityManager;

	@Override
	public void setUp() {
		Ejb3UnitCfg.addEntytiesToTest(FullTimeEmployee1.class);
		Ejb3UnitCfg.addEntytiesToTest(FullTimeEmployee3.class);
		entityManager = Ejb3UnitCfg.getConfiguration().getEntityManagerFactory().createEntityManager();
	}
	
	public void testAccessTypeEntity() {
		assertEquals(AccessType.FIELD, AccessTypeFinder.findAccessType(FullTimeEmployee1.class));
	}

	public void testAccessTypeMappedSuperclass() {
		assertEquals(AccessType.FIELD, AccessTypeFinder.findAccessType(FullTimeEmployee2.class));
	}

	public void testInheritedFields() {
		CSVInitialDataSet<FullTimeEmployee1> employeeData = new CSVInitialDataSet<FullTimeEmployee1>(
				FullTimeEmployee1.class, "fulltimeEmployeeData.csv", "empId", "name", "fullname", "salary");
		// No exception: ok.
	}
	
	public void testSql1() {
		CSVInitialDataSet<FullTimeEmployee1> employeeData = new CSVInitialDataSet<FullTimeEmployee1>(
				FullTimeEmployee1.class, "fulltimeEmployeeData.csv", "empId", "name", "fullname", "salary");
		
		assertEquals("INSERT INTO Employees (empId, name, fullname, salary, DTYPE) VALUES (?, ?, ?, ?, 'FT')", employeeData.buildInsertSQL());
	}

	public void testSql2() {
		CSVInitialDataSet<FullTimeEmployee2> employeeData = new CSVInitialDataSet<FullTimeEmployee2>(
				FullTimeEmployee2.class, "fulltimeEmployeeData.csv", "empId", "name", "fullname", "salary");
		
		assertEquals("INSERT INTO Employees (empId, name, fullname, salary) VALUES (?, ?, ?, ?)", employeeData.buildInsertSQL());
	}

	public void testSql3() {
		CSVInitialDataSet<FullTimeEmployee3> employeeData = new CSVInitialDataSet<FullTimeEmployee3>(
				FullTimeEmployee3.class, "fulltimeEmployeeData.csv", "empId", "name", "fullname", "salary");
		
		assertEquals("INSERT INTO Employees3 (empId, name, fullname, salary, type) VALUES (?, ?, ?, ?, 1)", employeeData.buildInsertSQL());
	}
	
	public void testInitialData1() {
		CSVInitialDataSet<FullTimeEmployee1> employeeData = new CSVInitialDataSet<FullTimeEmployee1>(
				FullTimeEmployee1.class, "fulltimeEmployeeData.csv", "empId", "name", "fullname", "salary");
		employeeData.create();
		FullTimeEmployee1 employee = entityManager.find(FullTimeEmployee1.class, 1);
		assertNotNull(employee);
		assertEquals("john", employee.getName());
		assertEquals(new Integer(10000), employee.getSalary());
	}

	public void testInitialData3() {
		CSVInitialDataSet<FullTimeEmployee3> employeeData = new CSVInitialDataSet<FullTimeEmployee3>(
				FullTimeEmployee3.class, "fulltimeEmployeeData.csv", "empId", "name", "fullname", "salary");
		employeeData.create();
		FullTimeEmployee3 employee = entityManager.find(FullTimeEmployee3.class, 1);
		assertNotNull(employee);
		assertEquals("john", employee.getName());
		assertEquals(new Integer(10000), employee.getSalary());
	}
}
