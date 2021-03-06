package com.bm.testsuite.mocked;

import java.util.Arrays;
import java.util.Map;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.persistence.PersistenceContext;

import org.jmock.Mockery;

import com.bm.creators.BeanCreationListener;
import com.bm.creators.MockedDIModuleCreator;
import com.bm.ejb3guice.inject.Ejb3Guice;
import com.bm.ejb3guice.inject.Injector;
import com.bm.ejb3guice.inject.Module;
import com.bm.ejb3guice.inject.Stage;
import com.bm.ejb3metadata.annotations.metadata.MetaDataCache;
import com.bm.testsuite.BaseTest;
import com.bm.testsuite.interfaces.IMockedSessionBeanFixture;

/**
 * This class enables the testing of Seteless/Statefull Session beans and JBoss
 * Service classes under Mock-Isolation control. All JSR 220 (EJB 3) fields will
 * be injected using a pluggable MockProvider.
 *
 * @author Daniel Wiese
 * @param <T> -
 * the type of the bena to test.
 */
public abstract class MockedSessionBeanFixture<T> extends BaseTest implements IMockedSessionBeanFixture<T> {

    private final Class<T> beanUnderTestClass;
    private T beanToTest = null;
    protected MockProvider mockProvider;
    private Map<Class<?>, Object> mockedDependencies = null;

    /**
     * Default constructor.
     *
     * @param beanToTest -
     *                   the bean to test.
     */
    public MockedSessionBeanFixture(
            Class<T> beanToTest) {
        this.beanUnderTestClass = beanToTest;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public <M> M getMock(Class<M> interfaze) {
        if (this.mockedDependencies != null && this.mockedDependencies.containsKey(interfaze)) {
            return (M) this.mockedDependencies.get(interfaze);
        } else {
            return mockProvider.getMock(interfaze);
        }
    }

    /**
     * {@inheritDoc}
     */
    public T getBeanToTest() {
        return this.beanToTest;
    }

    /**
     * {@inheritDoc}
     */
    public void setValueForField(String fieldName, Object toSet) {
        this.setValueForField(this.beanToTest, fieldName, toSet);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setUp() throws Exception {
        super.setUp();
        mockProvider = MockProviderFactory.createMockProvider();

        MockedDIModuleCreator module = MetaDataCache.getMockModuleCreator(beanUnderTestClass,
                mockProvider);
        this.beanToTest = createBeanIstance(module);
        this.mockedDependencies = module.getMocks();
    }

    @SuppressWarnings("unchecked")
    private T createBeanIstance(Module module) {

        // final T back = Ejb3Utils.getNewInstance(toCreate);
        Module[] mods = {module};
        BeanCreationListener createdbeans = new BeanCreationListener();
        Injector injector = Ejb3Guice.createInjector(Stage.PRODUCTION, Arrays.asList(mods),
                Ejb3Guice.markerToArray(EJB.class, Resource.class, PersistenceContext.class),
                createdbeans);
        final T instance = injector.getInstance(beanUnderTestClass);
        return instance;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void tearDown() throws Exception {
        mockProvider.verifyMocks();
        super.tearDown();
        this.beanToTest = null;
    }

    /**
     * {@inheritDoc}
     */
    public Mockery getContext() {
        return mockProvider.getContext();
    }
}
