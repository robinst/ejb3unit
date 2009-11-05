package com.bm.testsuite.mocked;

import org.jmock.Mockery;

/**
 * contract between MockedSessionBeanFixture and MockProvider.
 *
 * @author Alphonse Bendt
 */
public interface MockProvider {
    /**
     * @param <M> the type that should be mocked
     * @param interfaze the type that should be mocked
     * @return a mock object of the appropiate type
     */
    <M> M getMock(Class<M> interfaze);

    /**
     * implementations should prepare all mocks created so far for actual testing.
     */
    void replayMocks();


    /**
     * implementations should check the expectations of all mocks created so far.
     */
    void verifyMocks();

    Mockery getContext();
}
