package org.anchoranalysis.plugin.opencv.bean.object.segment.decode.instance;

import java.util.Arrays;
import java.util.List;
import org.anchoranalysis.spatial.box.BoundingBox;
import org.anchoranalysis.spatial.box.BoundingBoxFactory;

/**
 * An extension of {@link InstanceSegmentationTestBase} that finds a car number-plate located
 * approximately in a particular position.
 *
 * @author Owen Feehan
 */
public abstract class SegmentNumberPlateBase extends InstanceSegmentationTestBase {

    private static final BoundingBox BOX3 = BoundingBoxFactory.at(438, 310, 78, 40);

    @Override
    protected List<BoundingBox> expectedBoxesRGB() {
        BoundingBox box1 = BoundingBoxFactory.at(302, 315, 127, 42);
        BoundingBox box2 = BoundingBoxFactory.at(393, 199, 31, 28);
        return Arrays.asList(box1, box2, BOX3);
    }

    @Override
    protected List<BoundingBox> expectedBoxesGrayscale() {
        BoundingBox box1 = BoundingBoxFactory.at(316, 319, 104, 33);
        BoundingBox box2 = BoundingBoxFactory.at(394, 199, 27, 27);
        return Arrays.asList(box1, box2);
    }
}
