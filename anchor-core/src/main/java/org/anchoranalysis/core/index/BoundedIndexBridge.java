package org.anchoranalysis.core.index;



import org.anchoranalysis.core.functional.FunctionWithException;
import org.anchoranalysis.core.index.container.IBoundedIndexContainer;

public class BoundedIndexBridge<T> implements FunctionWithException<Integer, T, GetOperationFailedException> {
	
	private IBoundedIndexContainer<T> cntr;
	
	public BoundedIndexBridge(IBoundedIndexContainer<T> cntr) {
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
	public void setCntr(IBoundedIndexContainer<T> cntr) {
		this.cntr = cntr;
	}

}
