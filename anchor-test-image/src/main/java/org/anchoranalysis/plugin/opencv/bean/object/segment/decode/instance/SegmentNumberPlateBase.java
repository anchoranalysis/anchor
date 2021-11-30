package org.anchoranalysis.plugin.opencv.bean.object.segment.decode.instance;

import org.anchoranalysis.spatial.box.BoundingBox;
import org.anchoranalysis.spatial.box.BoundingBoxFactory;

/**
 * An extension of {@link InstanceSegmentationTestBase} that finds a car number-plate located
 * approximately in a particular position.
 *
 * @author Owen Feehan
 */
public abstract class SegmentNumberPlateBase extends InstanceSegmentationTestBase {

    @Override
    protected BoundingBox targetBox() {
        return BoundingBoxFactory.at(307, 310, 208, 46);
    }
}
