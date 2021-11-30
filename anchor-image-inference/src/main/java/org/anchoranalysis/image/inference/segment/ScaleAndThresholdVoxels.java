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
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.binary.BinaryVoxels;
import org.anchoranalysis.image.voxel.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;
import org.anchoranalysis.image.voxel.interpolator.Interpolator;
import org.anchoranalysis.image.voxel.interpolator.InterpolatorImgLib2;
import org.anchoranalysis.image.voxel.interpolator.InterpolatorImgLib2Linear;
import org.anchoranalysis.image.voxel.thresholder.VoxelsThresholder;
import org.anchoranalysis.io.imagej.iterpolator.InterpolatorImageJ;
import org.anchoranalysis.spatial.box.Extent;

/**
 * Scales the size of a {@code Voxels<FloatBuffer>} and then thresholds it.
 *
 * <p>Two modes are possible:
 *
 * <ul>
 *   <li>Higher quality - performs the interpolation using ImgLib2 which is higher quality but
 *       approximately 3 times slower.
 *   <li>Lower quality - performs the interpolation using ImageJ which is worse quality but much
 *       faster.
 * </ul>
 *
 * @author Owen Feehan
 */
public class ScaleAndThresholdVoxels {

    private final Interpolator interpolator;

    /**
     * Create for a particular quality.
     *
     * @param highQuality if true a higher quality (but slower) interpolator is used.
     */
    public ScaleAndThresholdVoxels(boolean highQuality) {
        interpolator = createInterpolator(highQuality);
    }

    /**
     * Scales voxels representing a mask to a target size, and then thresholds.
     *
     * @param voxels voxels before any scaling.
     * @param targetSize the desired size of the {@code voxels} after scaling.
     * @param maskMinValue the minimum voxel value to accept (and all above it) as <i>on</i> voxels
     *     in the mask.
     * @return a mask, of size {@code targetSize}, corresponding to a scaled and thresholded {@code
     *     voxels}.
     */
    public BinaryVoxels<UnsignedByteBuffer> scaleAndThreshold(
            Voxels<FloatBuffer> voxels, Extent targetSize, float maskMinValue) {
        // Scale and interpolate voxels to march the bounding-box
        voxels = voxels.extract().resizedXY(targetSize.x(), targetSize.y(), interpolator);

        return VoxelsThresholder.thresholdFloat(
                voxels, maskMinValue, BinaryValuesByte.getDefault());
    }

    /** The interpolator used for scaling the predicted mask up to the entire bounding-box. */
    private static Interpolator createInterpolator(boolean highQuality) {
        if (highQuality) {
            InterpolatorImgLib2 interpolator = new InterpolatorImgLib2Linear();
            // To avoid using the default mirroring strategy. Instead we wish to consider pixels
            // outside
            // the box as being zero-valued.
            interpolator.extendWith(0);
            return interpolator;
        } else {
            return new InterpolatorImageJ();
        }
    }
}
