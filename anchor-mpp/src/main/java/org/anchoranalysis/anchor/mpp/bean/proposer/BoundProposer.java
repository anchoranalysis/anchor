package org.anchoranalysis.anchor.mpp.bean.proposer;

import org.anchoranalysis.anchor.mpp.bean.bound.RslvdBound;
import org.anchoranalysis.anchor.mpp.bound.BidirectionalBound;
import org.anchoranalysis.anchor.mpp.params.ICompatibleWith;
import org.anchoranalysis.anchor.mpp.proposer.error.ErrorNode;
import org.anchoranalysis.core.geometry.Point3d;
import org.anchoranalysis.image.extent.ImageDim;
import org.anchoranalysis.math.rotation.RotationMatrix;

public abstract class BoundProposer extends ProposerBean<BoundProposer> implements ICompatibleWith {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5912235420817922585L;

	public abstract BidirectionalBound propose( Point3d pos, RotationMatrix orientation, ImageDim bndScene, RslvdBound minMaxBound, ErrorNode proposerFailureDescription );
}
