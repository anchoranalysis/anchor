/*-
 * #%L
 * anchor-io-bioformats
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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

package org.anchoranalysis.io.bioformats.copyconvert.tobyte;

import lombok.AllArgsConstructor;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;

/**
 * Scales a voxels by a certain ratio, so that the range of values in the source data fully maps to
 * the entire range available in the target data.
 *
 * <p>This can involve either downscaling or upscaling.
 *
 * <p>Depending on the operation, values may be larger then the range, and thus need to be clamped.
 *
 * @author Owen Feehan
 */
@AllArgsConstructor
class ApplyScaling {

    private float scaleFactor = 1;

    private final int maxTargetValue;

    /**
     * Creates to scale to an unsigned data-type of particular size.
     *
     * @param effectiveBits how many bits are used by the source data.
     * @param targetDataType the target data type to be converted to.
     */
    public ApplyScaling(int effectiveBits, VoxelDataType targetDataType) {
        this.maxTargetValue = (int) targetDataType.maxValue();
        int targetUnsignedBits = targetDataType.bitDepth();
        this.scaleFactor =
                ConvertHelper.twoToPower(
                        targetUnsignedBits
                                - effectiveBitsForScaleFactor(effectiveBits, targetUnsignedBits));
    }

    /**
     * Scales a value, if necessary, to map it to range of the target data-type.
     *
     * @param unscaled the unscaled value, as per the range of the source data.
     * @return the scaled-value.
     */
    public int scale(int unscaled) {
        return (int) (unscaled * scaleFactor);
    }

    /**
     * Like {@link #scale(int)} but additionally ensures value is in the appropriate range.
     *
     * @param value the value to scale and clamp.
     * @return the value after scaling and clamping.
     */
    public int scaleAndClamp(int value) {
        value = scale(value);

        if (value > maxTargetValue) {
            value = maxTargetValue;
        }
        if (value < 0) {
            value = 0;
        }
        return value;
    }

    /**
     * What value of effectiveBits to use when determining a scaling factor.
     *
     * <p>When the value is smaller than the target, and is thus being scaled <i>up</i>, it's better
     * to ensure a max value of 256, as this let's it be larger than 255, and it will be clamped
     * down.
     *
     * <p>When the value is larger than the target, and is this being scaled <i>down</i>, this
     * should not be done.
     *
     * <p>If this correction doesn't happen, values will be mapped to {@code 0...128} instead.
     *
     * @param effectiveBits how many bits are used by the source data.
     * @param targetUnsignedBits how many bits (assuming unsigned data) desired in the target data.
     * @return the number of effective bits to use for calculating a conversion ratio.
     */
    private static int effectiveBitsForScaleFactor(int effectiveBits, int targetUnsignedBits) {
        if (effectiveBits < targetUnsignedBits) {
            return effectiveBits - 1;
        } else {
            return effectiveBits;
        }
    }
}
