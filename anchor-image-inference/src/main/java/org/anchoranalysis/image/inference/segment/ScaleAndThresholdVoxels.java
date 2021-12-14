/*-
 * #%L
 * anchor-plugin-image
 * %%
 * Copyright (C) 2010 - 2021 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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
package org.anchoranalysis.image.inference.segment;

import java.nio.FloatBuffer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.binary.BinaryVoxels;
import org.anchoranalysis.image.voxel.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;
import org.anchoranalysis.image.voxel.resizer.VoxelsResizer;
import org.anchoranalysis.image.voxel.thresholder.VoxelsThresholder;
import org.anchoranalysis.spatial.box.Extent;

/**
 * Scales the size of a {@code Voxels<FloatBuffer>} and then thresholds it.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ScaleAndThresholdVoxels {

    /**
     * Scales voxels representing a mask to a target size, and then thresholds.
     *
     * @param voxels voxels before any scaling.
     * @param targetSize the desired size of the {@code voxels} after scaling.
     * @param resizer the interpolator to use for scaling.
     * @param maskMinValue the minimum voxel value to accept (and all above it) as <i>on</i> voxels
     *     in the mask.
     * @return a mask, of size {@code targetSize}, corresponding to a scaled and thresholded {@code
     *     voxels}.
     */
    public static BinaryVoxels<UnsignedByteBuffer> scaleAndThreshold(
            Voxels<FloatBuffer> voxels,
            Extent targetSize,
            VoxelsResizer resizer,
            float maskMinValue) {
        // Scale and interpolate voxels to march the bounding-box
        voxels = voxels.extract().resizedXY(targetSize.x(), targetSize.y(), resizer);

        return VoxelsThresholder.thresholdFloat(
                voxels, maskMinValue, BinaryValuesByte.getDefault());
    }
}
