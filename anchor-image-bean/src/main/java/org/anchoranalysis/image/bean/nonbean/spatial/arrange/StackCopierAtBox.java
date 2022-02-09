/*-
 * #%L
 * anchor-image-bean
 * %%
 * Copyright (C) 2010 - 2022 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */
package org.anchoranalysis.image.bean.nonbean.spatial.arrange;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.image.core.stack.Stack;
import org.anchoranalysis.spatial.box.BoundingBox;
import org.anchoranalysis.spatial.box.Extent;
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
            if (source.dimensions().z() == 1) {
                // When the source z-size is different to the target
                // z-size, let's repeat the source z-slices to fill the target.
                copyImageRepeatedZ(source, destination, box);
            } else if (box.extent().z() == 1) {
                copyImage(source.projectMax(), destination, box, 0);
            } else {
                copyImageRepeatedZ(source.projectMax(), destination, box);
            }
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
        Extent extent = source.extent();

        ReadableTuple3i cornerMin = box.cornerMin();
        ReadableTuple3i cornerMax = box.calculateCornerMaxInclusive();

        BufferPair buffers = new BufferPair(destination.getNumberChannels());

        for (int z = 0; z < extent.z(); z++) {
            buffers.assign(source, destination, z, z + cornerMin.z() + zShift);
            buffers.copySlice(cornerMin, cornerMax, destination.extent());
        }
    }
}
