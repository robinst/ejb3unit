package com.bm.testsuite.mocked;

import com.bm.cfg.Ejb3UnitCfg;

/**
 * @author Alphonse Bendt
 */
public class MockProviderFactory {

    public static MockProvider createMockProvider() {
        String mockLib = Ejb3UnitCfg.getConfiguration().getValue(Ejb3UnitCfg.KEY_MOCKING_PROVIDER);

        if (mockLib == null) {
            mockLib = Ejb3UnitCfg.JMOCK_VALUE;
        }

        if (Ejb3UnitCfg.JMOCK_VALUE.equals(mockLib)) {
            return new JMockProvider();
        } else if (Ejb3UnitCfg.MOCKITO_VALUE.equals(mockLib)) {
            return new MockitoProvider();
        } else if (Ejb3UnitCfg.EASYMOCK_VALUE.equals(mockLib)) {
            return new EasyMockProvider();
        } else {
            throw new RuntimeException("no mock provider available for: '" + mockLib + "'");
        }
    }
}
