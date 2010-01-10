package com.bm.junit4;

import com.bm.cfg.Ejb3UnitCfg;
import com.bm.ejb3data.bo.AnnotatedFieldsSessionBean;
import com.bm.ejb3data.bo.IMySessionBean;
import com.bm.testsuite.junit4.MockedSessionBeanJUnit4Fixture;
import javax.sql.DataSource;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

/**
 *
 * @author Alphonse Bendt
 */
public class MyOtherSessionBeanEasyMockJUnit4Test extends MockedSessionBeanJUnit4Fixture<AnnotatedFieldsSessionBean>{

    public MyOtherSessionBeanEasyMockJUnit4Test() {
        super(AnnotatedFieldsSessionBean.class);
    }

     private static String mockingProvider;

     @BeforeClass
    public static void setUpEasyMock() throws Exception {
        mockingProvider = Ejb3UnitCfg.getConfiguration().getValue(Ejb3UnitCfg.KEY_MOCKING_PROVIDER);
        Ejb3UnitCfg.getConfiguration().setProperty(Ejb3UnitCfg.KEY_MOCKING_PROVIDER, "easymock");
    }

    @AfterClass
    public static void tearDownEasyMock() throws Exception {
        Ejb3UnitCfg.getConfiguration().setProperty(Ejb3UnitCfg.KEY_MOCKING_PROVIDER, mockingProvider);
    }

    @Test
    public void testSomething() {
         AnnotatedFieldsSessionBean toTest = this.getBeanToTest();
        assertNotNull(toTest);
        final IMySessionBean mySessionBean = getMock(IMySessionBean.class);
        assertNotNull(mySessionBean);

        final DataSource ds = getMock(DataSource.class);

        expect(mySessionBean.getDs()).andReturn(ds);

               getMockProvider().replayMocks();


        // call the expected operation
        toTest.executeOperation();

        
    }

}
