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

package org.anchoranalysis.image.voxel.interpolator;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.image.voxel.VoxelsUntyped;
import org.anchoranalysis.image.voxel.datatype.FloatVoxelType;
import org.anchoranalysis.image.voxel.datatype.IncorrectVoxelTypeException;
import org.anchoranalysis.image.voxel.datatype.UnsignedByteVoxelType;
import org.anchoranalysis.image.voxel.datatype.UnsignedShortVoxelType;

/**
 * Helper utility functions for an {@link Interpolator}.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class InterpolateHelper {

    /**
     * Creates an implementation of {@link TransferViaSpecificType} of appropriate type for
     * interpolation.
     *
     * @param source the source voxels to be copied from.
     * @param destination the destination voxels to be interpolated into.
     * @return an appropriately-typed implementation of {@link TransferViaSpecificType}.
     */
    static TransferViaSpecificType<?> createTransfer( // NOSONAR
            VoxelsUntyped source, VoxelsUntyped destination) {

        if (!source.getVoxelDataType().equals(destination.getVoxelDataType())) {
            throw new IncorrectVoxelTypeException(
                    "Data types don't match between source and destination");
        }

        if (source.getVoxelDataType().equals(UnsignedByteVoxelType.INSTANCE)) {
            return new TransferViaSpecificType<>(
                    source,
                    destination,
                    VoxelsUntyped::asByte,
                    (interpolator,
                            voxelsSource,
                            voxelsDestination,
                            extentSource,
                            extentDestination) ->
                            interpolator.interpolateByte(
                                    voxelsSource,
                                    voxelsDestination,
                                    extentSource,
                                    extentDestination));
        } else if (source.getVoxelDataType().equals(UnsignedShortVoxelType.INSTANCE)) {
            return new TransferViaSpecificType<>(
                    source,
                    destination,
                    VoxelsUntyped::asShort,
                    (interpolator,
                            voxelsSource,
                            voxelsDestination,
                            extentSource,
                            extentDestination) ->
                            interpolator.interpolateShort(
                                    voxelsSource,
                                    voxelsDestination,
                                    extentSource,
                                    extentDestination));
        } else if (source.getVoxelDataType().equals(FloatVoxelType.INSTANCE)) {
            return new TransferViaSpecificType<>(
                    source,
                    destination,
                    VoxelsUntyped::asFloat,
                    (interpolator,
                            voxelsSource,
                            voxelsDestination,
                            extentSource,
                            extentDestination) ->
                            interpolator.interpolateFloat(
                                    voxelsSource,
                                    voxelsDestination,
                                    extentSource,
                                    extentDestination));
        } else {
            throw new IncorrectVoxelTypeException(
                    "Only unsigned byte and short and float are supported");
        }
    }
}
