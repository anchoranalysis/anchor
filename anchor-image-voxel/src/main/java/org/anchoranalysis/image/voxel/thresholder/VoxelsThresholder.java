/*-
 * #%L
 * anchor-image
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

package org.anchoranalysis.image.voxel.thresholder;

import java.util.Optional;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.VoxelsWrapper;
import org.anchoranalysis.image.voxel.binary.BinaryVoxels;
import org.anchoranalysis.image.voxel.binary.BinaryVoxelsFactory;
import org.anchoranalysis.image.voxel.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;
import org.anchoranalysis.image.voxel.datatype.UnsignedByteVoxelType;
import org.anchoranalysis.image.voxel.iterator.IterateVoxelsObjectMaskOptional;
import org.anchoranalysis.image.voxel.object.ObjectMask;

/**
 * Performs threshold operation on voxels.
 *
 * <p>An <i>on</i> voxel is placed in the output-buffer if {@code voxel-value >= level} or
 * <i>off</i> otherwise.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class VoxelsThresholder {

    public static void thresholdForLevel(
            Voxels<UnsignedByteBuffer> inputBuffer, int level, BinaryValuesByte bvOut) {
        // We know that as the inputType is byte, it will be performed in place
        thresholdForLevel(VoxelsWrapper.wrap(inputBuffer), level, bvOut, Optional.empty(), false);
    }

    // Perform inplace
    public static BinaryVoxels<UnsignedByteBuffer> thresholdForLevel(
            VoxelsWrapper inputBuffer,
            int level,
            BinaryValuesByte bvOut,
            Optional<ObjectMask> objectMask,
            boolean alwaysDuplicate) {
        Voxels<UnsignedByteBuffer> boxOut = inputBuffer.asByteOrCreateEmpty(alwaysDuplicate);

        if (inputBuffer.getVoxelDataType().equals(UnsignedByteVoxelType.INSTANCE)) {

            IterateVoxelsObjectMaskOptional.withBuffer(
                    objectMask, inputBuffer.asByte(), new ThresholdEachVoxel(level, boxOut, bvOut));
        }

        return BinaryVoxelsFactory.reuseByte(boxOut, bvOut.createInt());
    }
}
