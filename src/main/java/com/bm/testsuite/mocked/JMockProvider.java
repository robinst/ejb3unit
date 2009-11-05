package com.bm.testsuite.mocked;

import org.jmock.Mockery;

/**
 * @author Alphonse Bendt
 */
public class JMockProvider extends AbstractMockProvider {

    protected final Mockery context = new Mockery();

    @Override
    public void verifyMocks() {
        context.assertIsSatisfied();
    }

    @Override
    public <M> M doGetMock(Class<M> interfaze) {
        return context.mock(interfaze);
    }

    @Override
    public Mockery getContext() {
        return context;
    }
}
