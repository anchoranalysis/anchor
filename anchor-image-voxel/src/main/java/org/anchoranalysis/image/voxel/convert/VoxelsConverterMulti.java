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

package org.anchoranalysis.image.voxel.convert;

import lombok.AllArgsConstructor;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.exception.friendly.AnchorImpossibleSituationException;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.VoxelsUntyped;
import org.anchoranalysis.image.voxel.datatype.FloatVoxelType;
import org.anchoranalysis.image.voxel.datatype.UnsignedByteVoxelType;
import org.anchoranalysis.image.voxel.datatype.UnsignedIntVoxelType;
import org.anchoranalysis.image.voxel.datatype.UnsignedShortVoxelType;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;
import org.anchoranalysis.image.voxel.factory.VoxelsFactoryTypeBound;

/**
 * Converts a channel from one voxel data-type to one of multiple other types.
 *
 * @author Owen Feehan
 */
@AllArgsConstructor
public class VoxelsConverterMulti {

    /**
     * Converts a {@link Voxels} to another type.
     *
     * <p>If {@code outputVoxelType} matches the existing data-type, the existing {@code voxels} is
     * returned uncchanged.
     *
     * <p>Otherwise a new {@link Voxels} of identical size is created for the new type.
     *
     * @param voxels the voxels to convert.
     * @param outputVoxelsFactory a factory for creating the data-type to convert to.
     * @param <S> voxel data type to convert from.
     * @param <T> voxel data type to convert to.
     * @return a {@link Voxels} to match {@code outputVoxelsFactory}, either reused (if already
     *     identical, or newly-created).
     */
    @SuppressWarnings("unchecked")
    public <S, T> Voxels<S> convert(
            VoxelsUntyped voxels, VoxelsFactoryTypeBound<T> outputVoxelsFactory) {

        if (voxels.getVoxelDataType().equals(outputVoxelsFactory.dataType())) {
            return (Voxels<S>) voxels.any();
        } else {
            Voxels<S> voxelsToAssignTo =
                    (Voxels<S>) outputVoxelsFactory.createInitialized(voxels.extent());
            VoxelsConverter<S> converter = converterFor(outputVoxelsFactory.dataType());
            try {
                converter.copyFrom(voxels, voxelsToAssignTo);
            } catch (OperationFailedException e) {
                throw new AnchorImpossibleSituationException();
            }
            return voxelsToAssignTo;
        }
    }

    @SuppressWarnings("unchecked")
    private <S> VoxelsConverter<S> converterFor(VoxelDataType outputVoxelType) {

        if (outputVoxelType.equals(UnsignedByteVoxelType.INSTANCE)) {
            return (VoxelsConverter<S>) new ToUnsignedByteNoScaling();
        } else if (outputVoxelType.equals(UnsignedShortVoxelType.INSTANCE)) {
            return (VoxelsConverter<S>) new ToUnsignedShortNoScaling();
        } else if (outputVoxelType.equals(FloatVoxelType.INSTANCE)) {
            return (VoxelsConverter<S>) new ToFloatNoScaling();
        } else if (outputVoxelType.equals(UnsignedIntVoxelType.INSTANCE)) {
            return (VoxelsConverter<S>) new ToUnsignedInt();
        } else {
            throw new UnsupportedOperationException();
        }
    }
}
