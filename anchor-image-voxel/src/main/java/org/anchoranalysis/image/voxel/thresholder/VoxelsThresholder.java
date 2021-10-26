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

import java.nio.FloatBuffer;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.exception.friendly.AnchorImpossibleSituationException;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.VoxelsUntyped;
import org.anchoranalysis.image.voxel.binary.BinaryVoxels;
import org.anchoranalysis.image.voxel.binary.BinaryVoxelsFactory;
import org.anchoranalysis.image.voxel.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;
import org.anchoranalysis.image.voxel.datatype.FloatVoxelType;
import org.anchoranalysis.image.voxel.datatype.UnsignedByteVoxelType;
import org.anchoranalysis.image.voxel.factory.VoxelsFactory;
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

    public static void thresholdForLevelByte(
            Voxels<UnsignedByteBuffer> buffer, int level, BinaryValuesByte binaryValuesOut) {
        // We know that as the inputType is byte, it will be performed in place
        try {
            thresholdForLevel(new VoxelsUntyped(buffer), level, binaryValuesOut, Optional.empty(), false);
        } catch (OperationFailedException e) {
            throw new AnchorImpossibleSituationException();
        }
    }

    public static BinaryVoxels<UnsignedByteBuffer> thresholdForLevelFloat(
            Voxels<FloatBuffer> buffer, float level, BinaryValuesByte binaryValuesOut) {
        // We know that as the inputType is byte, it will be performed in place
        try {
            return thresholdForLevel(
                    new VoxelsUntyped(buffer), level, binaryValuesOut, Optional.empty(), false);
        } catch (OperationFailedException e) {
            throw new AnchorImpossibleSituationException();
        }
    }

    // Perform inplace
    public static BinaryVoxels<UnsignedByteBuffer> thresholdForLevel(
            VoxelsUntyped voxels,
            float level,
            BinaryValuesByte binaryValuesOut,
            Optional<ObjectMask> objectMask,
            boolean alwaysDuplicate)
            throws OperationFailedException {

        Voxels<UnsignedByteBuffer> out;
        if (voxels.getVoxelDataType().equals(UnsignedByteVoxelType.INSTANCE)) {
            out = voxelsAsByteOrEmpty(voxels, alwaysDuplicate);
            IterateVoxelsObjectMaskOptional.withBuffer(
                    objectMask,
                    voxels.asByte(),
                    new ThresholdEachVoxelByte((int) level, out, binaryValuesOut));

        } else if (voxels.getVoxelDataType().equals(FloatVoxelType.INSTANCE)) {
            out = VoxelsFactory.getUnsignedByte().createInitialized(voxels.extent());
            IterateVoxelsObjectMaskOptional.withTwoBuffers(
                    objectMask, voxels.asFloat(), out, new ThresholdEachVoxelFloat(level, binaryValuesOut));
        } else {
            throw new OperationFailedException(
                    "Unsupported voxel-data-type, only unsigned byte and float are supported");
        }
        return BinaryVoxelsFactory.reuseByte(out, binaryValuesOut.asInt());
    }
    
    /**
     * Reuses the existing buffer if of type {@link UnsignedByteBuffer}, otherwise creates a new empty byte buffer.
     *
     * @param buffer the buffer to reuse, copy, or create a an empty buffer in it's place of different type.
     * @param duplicate if true, an existing buffer of type {@link UnsignedByteBuffer} will not be reused directly, but duplicated.
     * @return either the current buffer (possibly duplicated if {@code duplicate} is true} or an empty buffer if the same-size.
     */
    private static Voxels<UnsignedByteBuffer> voxelsAsByteOrEmpty(VoxelsUntyped buffer, boolean duplicate) {
        Voxels<UnsignedByteBuffer> boxOut;

        // If the input-channel is Byte then we do it in-place
        // Otherwise we create new voxels
        if (!duplicate && buffer.getVoxelDataType().equals(UnsignedByteVoxelType.INSTANCE)) {
            boxOut = buffer.asByte();
        } else {
            boxOut = VoxelsFactory.getUnsignedByte().createInitialized(buffer.any().extent());
        }

        return boxOut;
    }
}
