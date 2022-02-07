package org.anchoranalysis.image.voxel.extracter;

import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedBufferAsInt;
import org.anchoranalysis.image.voxel.iterator.IterateVoxelsAll;
import org.anchoranalysis.image.voxel.iterator.MinMaxRange;

/**
 * Extension of {@code VoxelsExtracterBase} where the buffer-type is an instance of {@link
 * UnsignedBufferAsInt}.
 *
 * @author owen
 * @param <T> voxel-data-type
 */
abstract class VoxelsExtracterAsIntBase<T extends UnsignedBufferAsInt>
        extends VoxelsExtracterBase<T> {

    /**
     * Create with {@link Voxels}.
     *
     * @param voxels the voxels to extract from.
     */
    protected VoxelsExtracterAsIntBase(Voxels<T> voxels) {
        super(voxels);
    }

    @Override
    public long voxelWithMaxIntensity() {
        return IterateVoxelsAll.intensityMax(voxels);
    }

    @Override
    public long voxelWithMinIntensity() {
        return IterateVoxelsAll.intensityMin(voxels);
    }

    @Override
    public MinMaxRange voxelsWithMinMaxIntensity() {
        return IterateVoxelsAll.intensityMinMax(voxels);
    }
}
