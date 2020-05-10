package org.anchoranalysis.anchor.mpp.pxlmark;

import org.anchoranalysis.anchor.mpp.pixelpart.IndexByChnl;

/**
 * 
 * @author Owen Feehan
 *
 * @param <T> index-type
 */
public abstract class PxlMarkWithPartition<T> extends PxlMark {

	// Quick access to what is inside and what is outside
	protected final IndexByChnl<T> partitionList;
	
	public PxlMarkWithPartition() {
		partitionList = new IndexByChnl<>();
	}
	
	protected PxlMarkWithPartition(PxlMarkWithPartition<T> src) {
		// No duplication, only shallow copy (for now). This might change in future.
		this.partitionList = src.partitionList;
	}
}
