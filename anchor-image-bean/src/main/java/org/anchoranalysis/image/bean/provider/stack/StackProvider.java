/* (C)2020 */
package org.anchoranalysis.image.bean.provider.stack;

import org.anchoranalysis.bean.annotation.GroupingRoot;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.image.bean.provider.BeanImgStackProvider;
import org.anchoranalysis.image.stack.Stack;

@GroupingRoot
public abstract class StackProvider extends BeanImgStackProvider<StackProvider, Stack> {

    @Override
    public abstract Stack create() throws CreateException;

    public Stack createStack() throws CreateException {
        return create();
    }
}
