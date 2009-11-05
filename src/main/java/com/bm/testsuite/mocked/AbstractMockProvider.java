package com.bm.testsuite.mocked;

import java.util.ArrayList;
import java.util.List;
import org.jmock.Mockery;

/**
 * this class acts as a base for MockProvider implementations.
 * 
 * @author Alphonse Bendt
 */
public abstract class AbstractMockProvider implements MockProvider {

    /**
     * contains all mocks created by this provider.
     */
    protected final List mocks = new ArrayList();

    /**
     * access the JMock Mockery to define expectations.
     *
     * @return a JMock Context if available.
     */
    public Mockery getContext() {
        throw new UnsupportedOperationException();
    }

    public final <M> M getMock(Class<M> interfaze) {
        M mock = doGetMock(interfaze);

        mocks.add(mock);

        return mock;
    }

    /**
     * @param <M> the type of mock that should be created
     * @param clazz the type of mock that should be created
     * @return a mock object
     */
    abstract <M> M doGetMock(Class<M> clazz);

    public void verifyMocks() {
    }

    public void replayMocks() {
    }
}
