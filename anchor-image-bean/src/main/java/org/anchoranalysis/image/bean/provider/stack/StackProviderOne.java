package org.anchoranalysis.image.bean.provider.stack;

import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.image.stack.Stack;

public abstract class StackProviderOne extends StackProvider {

	// START BEAN FIELDS
	@BeanField
	private StackProvider stack;
	// END BEAN FIELDS
	
	@Override
	public Stack create() throws CreateException {
		return createFromStack(
			stack.create()
		);
	}
	
	protected abstract Stack createFromStack(Stack Stack) throws CreateException;

	public StackProvider getStack() {
		return stack;
	}

	public void setStack(StackProvider stack) {
		this.stack = stack;
	}


}
