package org.anchoranalysis.anchor.overlay;

import org.anchoranalysis.anchor.overlay.collection.OverlayCollection;
import org.anchoranalysis.core.index.SingleIndexCntr;

public class OverlayedInstantState extends SingleIndexCntr {

	private OverlayCollection overlayCollection;
	
	public OverlayedInstantState(int iter, OverlayCollection overlayCollection) {
		super(iter);
		assert(overlayCollection!=null);
		this.overlayCollection = overlayCollection;
	}

	public OverlayCollection getOverlayCollection() {
		return overlayCollection;
	}
}
