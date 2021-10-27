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
 * Performs a <a href="https://en.wikipedia.org/wiki/Thresholding_(image_processing)">thresholding operation</a> on voxels.
 *
 * <p>An <i>on</i> voxel is placed in the output-buffer if {@code voxel-value >= level} or
 * <i>off</i> otherwise.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class VoxelsThresholder {

    /**
     * Applies thresholding to {@link Voxels} of <i>unsigned byte</i> data type.
     * 
     * @param voxels the voxels, which are consumed, and replaced with <i>on</i> and <i>off</i> values.
     * @param level the level for thresholding, see the class description.
     * @param binaryValues how to encode the <i>on</i> and <i>off</i> states for the thresholding output.
     */
    public static void thresholdByte(
            Voxels<UnsignedByteBuffer> voxels, int level, BinaryValuesByte binaryValues) {
        // We know that as the inputType is byte, it will be performed in place
        try {
            threshold(new VoxelsUntyped(voxels), level, binaryValues, Optional.empty(), false);
        } catch (OperationFailedException e) {
            throw new AnchorImpossibleSituationException();
        }
    }

    /**
     * Applies thresholding to {@link Voxels} of <i>float</i> data type.
     * 
     * @param voxels the voxels, which are left unchanged.
     * @param level the level for thresholding, see the class description.
     * @param binaryValues how to encode the <i>on</i> and <i>off</i> states for the thresholding output.
     * @return a newly created {@link BinaryVoxels}, of identical size to {@code voxels} containing the output of the thresholding.
     */
    public static BinaryVoxels<UnsignedByteBuffer> thresholdFloat(
            Voxels<FloatBuffer> voxels, float level, BinaryValuesByte binaryValues) {
        try {
            return threshold(
                    new VoxelsUntyped(voxels), level, binaryValues, Optional.empty(), false);
        } catch (OperationFailedException e) {
            throw new AnchorImpossibleSituationException();
        }
    }

    /**
     * Applies thresholding to {@link VoxelsUntyped}.
     * 
     * <p>Only <i>unsigned byte</i> <i>float</i> data types are supported.
     * 
     * @param voxels the voxels, which are always unchanged if {@code alwaysDuplicate} is true, and otherwise will be changed if they are of <i>unsigned byte</i> type.
     * @param level the level for thresholding, see the class description.
     * @param binaryValues how to encode the <i>on</i> and <i>off</i> states for the thresholding output.
     * @param alwaysDuplicate if true, {@code voxels} are never reused in the output, with new buffers always created.
     * @param objectMask if set, restricts the region where thresholding occurs to correspond to this object-mask.
     * @return a {@link BinaryVoxels}, reusing {@code voxels} if they are of type <i>unsigned byte</i> (and {@code alwaysDuplicate} is false), otherwise created newly.
     * @throws OperationFailedException if an unsupported data-type exists in {@code voxels}.
     */
    public static BinaryVoxels<UnsignedByteBuffer> threshold(
            VoxelsUntyped voxels,
            float level,
            BinaryValuesByte binaryValues,
            Optional<ObjectMask> objectMask,
            boolean alwaysDuplicate)
            throws OperationFailedException {

        Voxels<UnsignedByteBuffer> out;
        if (voxels.getVoxelDataType().equals(UnsignedByteVoxelType.INSTANCE)) {
            out = voxelsAsByteOrEmpty(voxels, alwaysDuplicate);
            IterateVoxelsObjectMaskOptional.withBuffer(
                    objectMask,
                    voxels.asByte(),
                    new ThresholdEachVoxelByte((int) level, out, binaryValues));

        } else if (voxels.getVoxelDataType().equals(FloatVoxelType.INSTANCE)) {
            out = VoxelsFactory.getUnsignedByte().createInitialized(voxels.extent());
            IterateVoxelsObjectMaskOptional.withTwoBuffers(
                    objectMask, voxels.asFloat(), out, new ThresholdEachVoxelFloat(level, binaryValues));
        } else {
            throw new OperationFailedException(
                    "Unsupported voxel-data-type, only unsigned byte and float are supported");
        }
        return BinaryVoxelsFactory.reuseByte(out, binaryValues.asInt());
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
