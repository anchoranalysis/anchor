package org.anchoranalysis.image.voxel;

import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.Extent;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
public class ExtentMatchHelper {

    public static void checkExtentMatch(BoundingBox box1, BoundingBox box2) {
        Extent extent1 = box1.extent();
        Extent extent2 = box2.extent();
        if (!extent1.equals(extent2)) {
            throw new IllegalArgumentException(
                    String.format(
                            "The extents of the two bounding-boxes are not identical: %s vs %s",
                            extent1, extent2));
        }
    }
}
