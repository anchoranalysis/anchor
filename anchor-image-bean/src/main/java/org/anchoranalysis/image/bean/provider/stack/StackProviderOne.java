/* (C)2020 */
package org.anchoranalysis.image.bean.provider.stack;

import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.image.stack.Stack;

public abstract class StackProviderOne extends StackProvider {

    // START BEAN FIELDS
    @BeanField @Getter @Setter private StackProvider stack;
    // END BEAN FIELDS

    @Override
    public Stack create() throws CreateException {
        return createFromStack(stack.create());
    }

    protected abstract Stack createFromStack(Stack stack) throws CreateException;
}
