package org.anchoranalysis.anchor.mpp.mark.conic.bounds;

import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.core.random.RandomNumberGenerator;
import org.anchoranalysis.image.extent.ImageResolution;
import org.anchoranalysis.image.orientation.Orientation;

public abstract class RotationBounds extends AnchorBean<RotationBounds> {

	public abstract Orientation randomOrientation(RandomNumberGenerator re, ImageResolution res);
}
