package com.bm.testsuite.fixture;

import com.bm.ejb3data.bo.ValuedFullTimeEmployee1;
import javax.persistence.EntityManager;

import junit.framework.TestCase;

import com.bm.ejb3data.bo.FullTimeEmployee1;
import com.bm.ejb3data.bo.FullTimeEmployee2;
import com.bm.ejb3data.bo.FullTimeEmployee3;
import com.bm.ejb3guice.inject.Ejb3UnitInternalInject;
import com.bm.testsuite.dataloader.CSVInitialDataSet;
import com.bm.utils.AccessType;
import com.bm.utils.AccessTypeFinder;
import com.bm.utils.injectinternal.InternalInjector;

/**
 * Test class for entity inheritance.
 * 
 * @author Peter Doornbosch
 * @see com.bm.ejb3data.bo.FullTimeEmployee1
 * @see com.bm.ejb3data.bo.AbstractEmployee1
 */
public class EmployeeInheritanceTest extends TestCase {

	@Ejb3UnitInternalInject
	private EntityManager entityManager;

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void setUp() throws Exception {
		InternalInjector.createInternalInjector(FullTimeEmployee1.class,
				FullTimeEmployee3.class).injectMembers(this);
		super.setUp();
	}

	public void testAccessTypeEntity() {
		assertEquals(AccessType.FIELD, AccessTypeFinder
				.findAccessType(FullTimeEmployee1.class));
	}

	public void testAccessTypeMappedSuperclass() {
		assertEquals(AccessType.FIELD, AccessTypeFinder
				.findAccessType(FullTimeEmployee2.class));
	}

	public void testInheritedFields() {
		CSVInitialDataSet<FullTimeEmployee1> employeeData = new CSVInitialDataSet<FullTimeEmployee1>(
				FullTimeEmployee1.class, "fulltimeEmployeeData.csv", "empId", "name",
				"fullname", "salary");
		assertNotNull(employeeData);
		// No exception: ok.
	}

	public void testInheritedHierarchyFields() {
		CSVInitialDataSet<ValuedFullTimeEmployee1> employeeData = new CSVInitialDataSet<ValuedFullTimeEmployee1>(
				ValuedFullTimeEmployee1.class, "valuedFulltimeEmployeeData.csv", "empId", "name",
				"fullname", "salary", "pocketMoney");
		assertNotNull(employeeData);
		// No exception: ok.
	}

        public void testHierachySql1() {
		CSVInitialDataSet<ValuedFullTimeEmployee1> employeeData = new CSVInitialDataSet<ValuedFullTimeEmployee1>(
				ValuedFullTimeEmployee1.class, "valuedFulltimeEmployeeData.csv", "empId", "name",
				"fullname", "salary", "pocketMoney");

		assertEquals(
				"INSERT INTO Employees (empId, name, fullname, salary, pocketMoney, DTYPE) VALUES (?, ?, ?, ?, ?, 'VFT')",
				employeeData.buildInsertSQL()[0]);
	}
        
	public void testSql1() {
		CSVInitialDataSet<FullTimeEmployee1> employeeData = new CSVInitialDataSet<FullTimeEmployee1>(
				FullTimeEmployee1.class, "fulltimeEmployeeData.csv", "empId", "name",
				"fullname", "salary");

		assertEquals(
				"INSERT INTO Employees (empId, name, fullname, salary, DTYPE) VALUES (?, ?, ?, ?, 'FT')",
				employeeData.buildInsertSQL()[0]);
	}

	public void testSql2() {
		CSVInitialDataSet<FullTimeEmployee2> employeeData = new CSVInitialDataSet<FullTimeEmployee2>(
				FullTimeEmployee2.class, "fulltimeEmployeeData.csv", "empId", "name",
				"fullname", "salary");

		assertEquals(
				"INSERT INTO Employees (empId, name, fullname, salary) VALUES (?, ?, ?, ?)",
				employeeData.buildInsertSQL()[0]);
	}

	public void testSql3() {
		CSVInitialDataSet<FullTimeEmployee3> employeeData = new CSVInitialDataSet<FullTimeEmployee3>(
				FullTimeEmployee3.class, "fulltimeEmployeeData.csv", "empId", "name",
				"fullname", "salary");

		assertEquals(
				"INSERT INTO Employees3 (empId, name, fullname, salary, type) VALUES (?, ?, ?, ?, 1)",
				employeeData.buildInsertSQL()[0]);
	}

	public void testInitialData1() {
		CSVInitialDataSet<FullTimeEmployee1> employeeData = new CSVInitialDataSet<FullTimeEmployee1>(
				FullTimeEmployee1.class, "fulltimeEmployeeData.csv", "empId", "name",
				"fullname", "salary");
		try {
			employeeData.create();
			FullTimeEmployee1 employee = entityManager.find(FullTimeEmployee1.class, 1);
			assertNotNull(employee);
			assertEquals("john", employee.getName());
			assertEquals(new Integer(10000), employee.getSalary());
		} finally {
			employeeData.cleanup(entityManager);
		}
	}

	public void testInitialData3() {
		CSVInitialDataSet<FullTimeEmployee3> employeeData = new CSVInitialDataSet<FullTimeEmployee3>(
				FullTimeEmployee3.class, "fulltimeEmployeeData.csv", "empId", "name",
				"fullname", "salary");
		try {
			employeeData.create();
			FullTimeEmployee3 employee = entityManager.find(FullTimeEmployee3.class, 1);
			assertNotNull(employee);
			assertEquals("john", employee.getName());
			assertEquals(new Integer(10000), employee.getSalary());
		} finally {
			employeeData.cleanup(entityManager);
		}
	}
}
