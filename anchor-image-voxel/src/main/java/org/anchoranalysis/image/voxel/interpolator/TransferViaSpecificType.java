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

import java.util.function.Function;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.VoxelsUntyped;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;
import org.anchoranalysis.spatial.box.Extent;

/**
 * Transfers voxels via a specific data-type.
 *
 * @author Owen Feehan
 * @param <T> voxel-data-type
 */
class TransferViaSpecificType<T> {

    @FunctionalInterface
    public interface TransferSlice<T> {
        VoxelBuffer<T> transferSlice(
                Interpolator interpolator,
                VoxelBuffer<T> sourceBuffer,
                VoxelBuffer<T> destinationBuffer,
                Extent extentSource,
                Extent extentDestination);
    }

    private final TransferSlice<T> transferSlice;
    private final Voxels<T> source;
    private final Voxels<T> destination;
    private VoxelBuffer<T> slice;

    public TransferViaSpecificType(
            VoxelsUntyped source,
            VoxelsUntyped destination,
            Function<VoxelsUntyped, Voxels<T>> extractVoxels,
            TransferSlice<T> transferSlice) {
        this.source = extractVoxels.apply(source);
        this.destination = extractVoxels.apply(destination);
        this.transferSlice = transferSlice;
    }

    public void assignSlice(int z) {
        slice = source.slice(z);
    }

    public void transferCopyTo(int z) {
        destination.replaceSlice(z, slice.duplicate());
    }

    public void transferTo(int z, Interpolator interpolator) {
        VoxelBuffer<T> destinationSlice = destination.slice(z);
        VoxelBuffer<T> transferredSlice =
                transferSlice.transferSlice(
                        interpolator,
                        slice,
                        destinationSlice,
                        source.extent(),
                        destination.extent());
        if (transferredSlice != destinationSlice) {
            destination.replaceSlice(z, transferredSlice);
        }
    }
}
