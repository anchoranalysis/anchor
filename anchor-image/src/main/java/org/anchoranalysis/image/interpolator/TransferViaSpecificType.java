package org.anchoranalysis.image.interpolator;

import java.util.function.Function;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.VoxelsWrapper;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;

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
            VoxelsWrapper source,
            VoxelsWrapper destination,
            Function<VoxelsWrapper, Voxels<T>> extractVoxels,
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
        if (!transferredSlice.equals(destinationSlice)) {
            destination.replaceSlice(z, transferredSlice);
        }
    }
}
