package com.bm.testsuite.mocked;

import com.bm.ejb3data.bo.AnnotatedFieldsSessionBean;
import com.bm.ejb3data.bo.IMySessionBean;
import com.bm.cfg.Ejb3UnitCfg;

import javax.sql.DataSource;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.atLeastOnce;

/**
 * @author Paul Sowden
 * @version Aug 7, 2009 10:28:09 AM
 */
public class MyOtherSessionBeanMockitoTest  extends
		MockedSessionBeanFixture<AnnotatedFieldsSessionBean> {

    private String mockingProvider;

    @Override
    public void setUp() throws Exception {
        mockingProvider =   Ejb3UnitCfg.getConfiguration().getValue(Ejb3UnitCfg.KEY_MOCKING_PROVIDER);
        Ejb3UnitCfg.getConfiguration().setProperty(Ejb3UnitCfg.KEY_MOCKING_PROVIDER, Ejb3UnitCfg.MOCKITO_VALUE);
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
	public MyOtherSessionBeanMockitoTest() {
		super(AnnotatedFieldsSessionBean.class);
	}

	/**
	 * Testmethod.
	 */
	public void test_executeOperation() {
        Ejb3UnitCfg.getConfiguration().setProperty(Ejb3UnitCfg.KEY_MOCKING_PROVIDER, Ejb3UnitCfg.MOCKITO_VALUE);

		AnnotatedFieldsSessionBean toTest = this.getBeanToTest();
		assertNotNull(toTest);
		final IMySessionBean mySessionBean = getMock(IMySessionBean.class);
		assertNotNull(mySessionBean);

		final DataSource ds = getMock(DataSource.class);
        when(mySessionBean.getDs()).thenReturn(ds);

        toTest.executeOperation();

        verify(mySessionBean, atLeastOnce()).getDs();
	}
}
