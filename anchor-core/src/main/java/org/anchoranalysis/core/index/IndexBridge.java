package org.anchoranalysis.core.index;



import org.anchoranalysis.core.functional.FunctionWithException;

public class IndexBridge<T> implements FunctionWithException<Integer,T,GetOperationFailedException> {
	
	private ITypedGetFromIndex<T> cntr;
	
	public IndexBridge(ITypedGetFromIndex<T> cntr) {
		super();
		this.cntr = cntr;
	}

	@Override
	public T apply(Integer sourceObject) throws GetOperationFailedException {
		return cntr.get( sourceObject);
	}

	// Updates the cntr associated with the bridge
	public void setCntr(ITypedGetFromIndex<T> cntr) {
		this.cntr = cntr;
	}

}
