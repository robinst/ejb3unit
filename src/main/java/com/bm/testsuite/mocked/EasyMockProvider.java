package com.bm.testsuite.mocked;

import java.util.Iterator;
import org.easymock.EasyMock;

/**
 * @author Alphonse Bendt
 */
public class EasyMockProvider extends AbstractMockProvider {

    public <M> M doGetMock(Class<M> interfaze) {
        return EasyMock.createNiceMock(interfaze);
    }

    @Override
    public void replayMocks() {
        for (Iterator it = mocks.iterator(); it.hasNext();) {
            Object mock = it.next();
            EasyMock.replay(mock);
        }
    }

    @Override
    public void verifyMocks() {
        for (Iterator it = mocks.iterator(); it.hasNext();) {
            Object mock = it.next();
            EasyMock.verify(mock);
        }
    }
}
