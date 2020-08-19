package org.anchoranalysis.image.voxel.extracter;

import java.nio.ShortBuffer;
import org.anchoranalysis.image.convert.ByteConverter;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.buffer.max.MaxIntensityBufferShort;
import org.anchoranalysis.image.voxel.buffer.mean.MeanIntensityShortBuffer;
import org.anchoranalysis.image.voxel.iterator.IterateVoxelsVoxelBoxAsInt;

class ShortImplementation extends Base<ShortBuffer> {

    public ShortImplementation(Voxels<ShortBuffer> voxels) {
        super(voxels);
    }

    @Override
    public void copyBufferIndexTo(
            ShortBuffer sourceBuffer,
            int sourceIndex,
            ShortBuffer destinationBuffer,
            int destinationIndex) {
        destinationBuffer.put(destinationIndex, sourceBuffer.get(sourceIndex));
    }

    @Override
    public Voxels<ShortBuffer> projectMax() {

        Extent extent = voxels.extent();

        MaxIntensityBufferShort projection = new MaxIntensityBufferShort(extent);

        for (int z = 0; z < extent.z(); z++) {
            projection.projectSlice(voxels.sliceBuffer(z));
        }

        return projection.asVoxels();
    }

    @Override
    public Voxels<ShortBuffer> projectMean() {

        Extent extent = voxels.extent();

        MeanIntensityShortBuffer projection = new MeanIntensityShortBuffer(extent);

        for (int z = 0; z < extent.z(); z++) {
            projection.projectSlice(voxels.sliceBuffer(z));
        }

        return projection.getFlatBuffer();
    }

    @Override
    public int voxelWithMaxIntensity() {
        return IterateVoxelsVoxelBoxAsInt.findMaxValue(voxels);
    }

    @Override
    protected int voxelAtBufferIndex(ShortBuffer buffer, int index) {
        return ByteConverter.unsignedShortToInt(buffer.get(index));
    }

    @Override
    protected boolean bufferValueGreaterThan(ShortBuffer buffer, int threshold) {
        return buffer.get() > threshold;
    }

    @Override
    protected boolean bufferValueEqualTo(ShortBuffer buffer, int value) {
        return buffer.get() == value;
    }
}
