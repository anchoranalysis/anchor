/* (C)2020 */
package org.anchoranalysis.mpp.sgmn.dummyProvider;

import static org.mockito.Mockito.*;

import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.image.bean.provider.stack.StackProvider;
import org.anchoranalysis.image.stack.Stack;

public class DummyStackProvider extends StackProvider {

    @Override
    public Stack create() throws CreateException {
        return mock(Stack.class);
    }
}
