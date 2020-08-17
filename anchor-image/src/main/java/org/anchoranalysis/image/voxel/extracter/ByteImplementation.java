package org.anchoranalysis.image.voxel.extracter;

import java.nio.ByteBuffer;
import org.anchoranalysis.image.convert.ByteConverter;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.buffer.max.MaxIntensityBufferByte;
import org.anchoranalysis.image.voxel.buffer.mean.MeanIntensityByteBuffer;
import org.anchoranalysis.image.voxel.iterator.IterateVoxelsAsInt;

class ByteImplementation extends Base<ByteBuffer> {

    public ByteImplementation(Voxels<ByteBuffer> voxels) {
        super(voxels);
    }

    @Override
    public void copyBufferIndexTo(
            ByteBuffer sourceBuffer,
            int sourceIndex,
            ByteBuffer destinationBuffer,
            int destinationIndex) {
        destinationBuffer.put(destinationIndex, sourceBuffer.get(sourceIndex));
    }

    @Override
    protected int voxelAtBufferIndex(ByteBuffer buffer, int index) {
        return ByteConverter.unsignedByteToInt(buffer.get(index));
    }

    @Override
    public Voxels<ByteBuffer> projectionMax() {

        Extent extent = voxels.extent();

        MaxIntensityBufferByte mi = new MaxIntensityBufferByte(extent);

        for (int z = 0; z < extent.z(); z++) {
            mi.projectSlice(voxels.sliceBuffer(z));
        }

        return mi.asVoxels();
    }

    @Override
    public Voxels<ByteBuffer> projectionMean() {

        Extent extent = voxels.extent();

        MeanIntensityByteBuffer mi = new MeanIntensityByteBuffer(extent);

        for (int z = 0; z < extent.z(); z++) {
            mi.projectSlice(voxels.sliceBuffer(z));
        }

        return mi.getFlatBuffer();
    }

    @Override
    public int voxelWithMaxIntensity() {
        return IterateVoxelsAsInt.findMaxValue(voxels);
    }

    @Override
    protected boolean bufferValueGreaterThan(ByteBuffer buffer, int threshold) {
        return ByteConverter.unsignedByteToInt(buffer.get()) > threshold;
    }

    @Override
    protected boolean bufferValueEqualTo(ByteBuffer buffer, int value) {
        return ByteConverter.unsignedByteToInt(buffer.get()) == value;
    }
}
