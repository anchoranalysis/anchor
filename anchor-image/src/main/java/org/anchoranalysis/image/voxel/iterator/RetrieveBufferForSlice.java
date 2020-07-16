/* (C)2020 */
package org.anchoranalysis.image.voxel.iterator;

import java.nio.Buffer;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.voxel.box.VoxelBox;

/**
 * Exposes a {@link ProcessVoxelOffset} as a {@link ProcessVoxelSliceBuffer} by retrieving a buffer
 * from a voxel-box for each z-slice.
 *
 * <p>Note that {@link} notifyChangeZ need not be be called for all slices (perhaps only a subset),
 * but {@link process} must be called for ALL voxels on a given slice.
 *
 * @author Owen Feehan
 * @param <T> buffer-type for slice
 */
public final class RetrieveBufferForSlice<T extends Buffer> implements ProcessVoxel {

    private final VoxelBox<T> voxelBox;
    private final ProcessVoxelSliceBuffer<T> process;

    private T bufferSlice;
    /** A 2D offset within the current slice */
    private int offsetWithinSlice;

    public RetrieveBufferForSlice(VoxelBox<T> voxelBox, ProcessVoxelSliceBuffer<T> process) {
        super();
        this.voxelBox = voxelBox;
        this.process = process;
    }

    @Override
    public void notifyChangeZ(int z) {
        process.notifyChangeZ(z);
        offsetWithinSlice = 0;
        this.bufferSlice = voxelBox.getPixelsForPlane(z).buffer();
    }

    @Override
    public void process(Point3i point) {
        process.process(point, bufferSlice, offsetWithinSlice++);
    }
}
