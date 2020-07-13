package org.anchoranalysis.core.index;

import org.anchoranalysis.core.functional.function.FunctionWithException;
import org.anchoranalysis.core.index.container.BoundedIndexContainer;

public class BoundedIndexBridge<T> implements FunctionWithException<Integer, T, GetOperationFailedException> {
	
	private BoundedIndexContainer<T> cntr;
	
	public BoundedIndexBridge(BoundedIndexContainer<T> cntr) {
		super();
		this.cntr = cntr;
	}

	@Override
	public T apply(Integer sourceObject) throws GetOperationFailedException {
		int index = cntr.previousEqualIndex(sourceObject);
		if (index==-1) {
			throw new GetOperationFailedException("Cannot find a previousEqualIndex in the cntr");
		}
		return cntr.get( index );
	}

	// Updates the cntr associated with the bridge
	public void setCntr(BoundedIndexContainer<T> cntr) {
		this.cntr = cntr;
	}

}
