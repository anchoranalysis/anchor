package org.anchoranalysis.image.bean.spatial.arrange.tile;

import lombok.AllArgsConstructor;
import lombok.Value;
import org.anchoranalysis.spatial.box.BoundingBox;

/**
 * The corner-point and the size in a particular dimension. Like a 1D equivalent of a {@link
 * BoundingBox}.
 */
@Value
@AllArgsConstructor
class SizeAtPoint {
    /** The corner point. */
    private int point;

    /** The size. */
    private int size;
}
