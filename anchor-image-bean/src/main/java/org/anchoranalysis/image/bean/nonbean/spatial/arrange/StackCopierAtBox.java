package org.anchoranalysis.image.bean.nonbean.spatial.arrange;

import com.google.common.base.Preconditions;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.image.core.stack.Stack;
import org.anchoranalysis.spatial.box.BoundingBox;
import org.anchoranalysis.spatial.box.Extent;
import org.anchoranalysis.spatial.point.Point3i;
import org.anchoranalysis.spatial.point.ReadableTuple3i;

/**
 * Copies a {@code source} stack into a {@code destination} stack at a particular {@link
 * BoundingBox}.
 *
 * <p>Ordinarily both {@code source} and {@code destination} must have an identical number of
 * channels. However, exceptionally, if {@code source} is single-channeled it is replicated as
 * needed to match {@code destination}.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StackCopierAtBox {

    /**
     * Copies a {@code source} stack into a {@code destination} stack at a particular {@link
     * BoundingBox}.
     *
     * @param source the stack that is copied (either singled-channeled or containing the same
     *     number of channels as {@code destination}.
     * @param destination the stack into which {@code source} is copied.
     * @param box the bounding-box in {@code destination} into which {@code source} is copied.
     */
    public static void copyImageInto(Stack source, Stack destination, BoundingBox box) {
        if (box.extent().z() != source.dimensions().z()) {
            // When the source z-size is different to the target
            // z-size, let's repeat the source z-slices to fill the target.
            copyImageRepeatedZ(source, destination, box);
        } else {
            copyImage(source, destination, box, 0);
        }
    }

    /**
     * Like {@link #copyImage} but repeatedly copies the z-slices in {@code source}, to completely
     * fill all z-slices in {@code destination}.
     */
    private static void copyImageRepeatedZ(Stack source, Stack destination, BoundingBox box) {
        int zShift = 0;
        do {
            copyImage(source, destination, box, zShift);
            zShift += source.dimensions().z();
        } while (zShift < destination.dimensions().z());
    }

    /**
     * Copies a {@code source} stack into a {@code destination} stack at a particular {@link
     * BoundingBox}.
     *
     * @param source the stack that is copied.
     * @param destination the stack into which {@code source} is copied.
     * @param box the bounding-box in {@code target} into which {@code source} is copied.
     * @param zShift uses a z-slice that is shifted positive in the destinaiton stack, relative to
     *     the source. 0 disables.
     */
    private static void copyImage(Stack source, Stack destination, BoundingBox box, int zShift) {
        Preconditions.checkArgument(
                source.getNumberChannels() == destination.getNumberChannels()
                        || source.getNumberChannels() == 1);

        Extent extent = source.extent();

        ReadableTuple3i cornerMin = box.cornerMin();
        Point3i cornerMax = box.calculateCornerMax();

        BufferPair buffers = new BufferPair(destination.getNumberChannels());

        for (int z = 0; z < extent.z(); z++) {
            buffers.assign(source, destination, z, z + cornerMin.z() + zShift);
            buffers.copySlice(cornerMin, cornerMax, destination.extent());
        }
    }
}
