package org.anchoranalysis.plugin.opencv.bean.object.segment.decode.instance;

import java.util.Arrays;
import java.util.List;
import org.anchoranalysis.spatial.box.BoundingBox;
import org.anchoranalysis.spatial.box.BoundingBoxFactory;

/**
 * An extension of {@link InstanceSegmentationTestBase} that finds a car located approximately in a
 * particular position.
 *
 * @author Owen Feehan
 */
public abstract class SegmentCarBase extends InstanceSegmentationTestBase {

    private static final BoundingBox BOX = BoundingBoxFactory.at(116, 18, 576, 398);

    @Override
    protected List<BoundingBox> expectedBoxesRGB() {
        return Arrays.asList(BOX);
    }

    @Override
    protected List<BoundingBox> expectedBoxesGrayscale() {
        return Arrays.asList(BOX);
    }
}
