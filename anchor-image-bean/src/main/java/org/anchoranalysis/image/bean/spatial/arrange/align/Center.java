package org.anchoranalysis.image.bean.spatial.arrange.align;

import org.anchoranalysis.spatial.box.BoundingBox;

/**
 * Align the bounding-box in the center of {@code larger}, without resizing.
 *
 * @author Owen Feehan
 */
public class Center extends BoxAligner {

    @Override
    public BoundingBox align(BoundingBox smaller, BoundingBox larger) {
        return CenterUtilities.centerSmaller(smaller.cornerMin(), smaller.extent(), larger);
    }
}
