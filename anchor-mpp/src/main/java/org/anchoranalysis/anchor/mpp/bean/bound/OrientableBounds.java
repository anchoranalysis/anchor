/* (C)2020 */
package org.anchoranalysis.anchor.mpp.bean.bound;

import org.anchoranalysis.core.random.RandomNumberGenerator;
import org.anchoranalysis.image.extent.ImageResolution;
import org.anchoranalysis.image.orientation.Orientation;

public abstract class OrientableBounds extends MarkBounds {

    /** */
    private static final long serialVersionUID = -7378361406755456211L;

    public abstract Orientation randomOrientation(
            RandomNumberGenerator randomNumberGenerator, ImageResolution res);
}
