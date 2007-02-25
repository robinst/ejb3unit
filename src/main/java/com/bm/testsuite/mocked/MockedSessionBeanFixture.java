package com.bm.testsuite.mocked;

import java.util.Map;

import org.jmock.Mock;

import com.bm.inject.DependencyInjector;
import com.bm.inject.Injection;
import com.bm.inject.impl.JMockInjectionFactory;
import com.bm.testsuite.BaseTest;

/**
 * This class enables the testing of Seteless/Statefull Session beans and JBoss
 * Service classes under Mock-Isolation control. All JSR 220 (EJB 3) fields will
 * be injected as JMock objects.
 * 
 * @author Daniel Wiese
 * 
 * @param <T> -
 *            the type of the bena to test.
 */
public abstract class MockedSessionBeanFixture<T> extends BaseTest {

    private final Class<T> beanUnderTestClass;

    private T beanToTest = null;

    private Map<String, Injection<Mock>> controlls = null;

    /**
     * Default constructor.
     * 
     * @param beanToTest -
     *            the bean to test.
     */
    public MockedSessionBeanFixture(Class<T> beanToTest) {
        this.beanUnderTestClass = beanToTest;
    }

    /**
     * Returns the mock controll to the given property.
     * 
     * @param property -
     *            the name of the property
     * @return - the mock controll
     */
    protected Mock getMockControl(String property) {
        if (this.controlls != null) {
            return this.controlls.get(property).getHandle();
        } else {
            return null;
        }
    }

    /**
     * Returns the bean to test.
     * 
     * @return - the bean to test
     */
    protected T getBeanToTest() {
        return this.beanToTest;
    }

    /**
     * Sets a value for a field in the tested-bean instance.
     * 
     * @author Daniel Wiese
     * @since 02.05.2006
     * @param fieldName -
     *            the name of the field
     * @param toSet -
     *            the value to set
     */
    protected void setValueForField(String fieldName, Object toSet) {
        this.setValueForField(this.beanToTest, fieldName, toSet);
    }

    /**
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.beanToTest = beanUnderTestClass.newInstance();
        // inject automatically all fields
        final JMockInjectionFactory defaultFactory = new JMockInjectionFactory();
        final DependencyInjector<Mock> injector = new DependencyInjector<Mock>(defaultFactory);

        this.controlls = injector.injectDependecies(this.beanToTest);
        // register the mock controlls;
        for (Injection<Mock> current : controlls.values()) {
            registerToVerify(current.getHandle());
        }
    }

    /**
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        this.beanToTest = null;
    }

}
