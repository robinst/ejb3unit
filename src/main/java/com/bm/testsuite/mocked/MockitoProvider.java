package com.bm.testsuite.mocked;

import static org.mockito.Mockito.mock;

/**
 * @author Alphonse Bendt
 */
public class MockitoProvider extends AbstractMockProvider {

    @Override
    public <M> M doGetMock(Class<M> interfaze) {
        return mock(interfaze);
    }

}
