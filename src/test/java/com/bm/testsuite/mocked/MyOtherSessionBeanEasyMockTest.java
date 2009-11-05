package com.bm.testsuite.mocked;

import com.bm.cfg.Ejb3UnitCfg;
import javax.sql.DataSource;

import static org.easymock.EasyMock.*;

import com.bm.ejb3data.bo.AnnotatedFieldsSessionBean;
import com.bm.ejb3data.bo.IMySessionBean;

/**
 * This testcase demonstrates how ejb3unit can be used in conjunction with EasyMock.
 * 
 * @author Alphonse Bendt
 * 
 */
public class MyOtherSessionBeanEasyMockTest extends MockedSessionBeanFixture<AnnotatedFieldsSessionBean> {

    private String mockingProvider;

    @Override
    public void setUp() throws Exception {
        mockingProvider = Ejb3UnitCfg.getConfiguration().getValue(Ejb3UnitCfg.KEY_MOCKING_PROVIDER);
        Ejb3UnitCfg.getConfiguration().setProperty(Ejb3UnitCfg.KEY_MOCKING_PROVIDER, "easymock");
        super.setUp();
    }

    @Override
    public void tearDown() throws Exception {
        Ejb3UnitCfg.getConfiguration().setProperty(Ejb3UnitCfg.KEY_MOCKING_PROVIDER, mockingProvider);
        super.tearDown();
    }

    /**
     * Constructor.
     */
    public MyOtherSessionBeanEasyMockTest() {
        super(AnnotatedFieldsSessionBean.class);
    }

    @Override
    public void testNothing() {
        // need to invoke *before* the actual test and the (automatic) verifyMocks is invoked.
        mockProvider.replayMocks();
        
        super.testNothing();
    }


    /**
     * Testmethod.
     */
    public void test_executeOperation() {
        AnnotatedFieldsSessionBean toTest = this.getBeanToTest();
        assertNotNull(toTest);
        final IMySessionBean mySessionBean = getMock(IMySessionBean.class);
        assertNotNull(mySessionBean);

        final DataSource ds = getMock(DataSource.class);

        expect(mySessionBean.getDs()).andReturn(ds);

        mockProvider.replayMocks();

        // call the expected operation
        toTest.executeOperation();
    }
}
