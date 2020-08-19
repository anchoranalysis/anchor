package org.anchoranalysis.image.voxel.extracter;

import java.nio.FloatBuffer;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.buffer.max.MaxIntensityBufferFloat;

class FloatImplementation extends Base<FloatBuffer> {

    public FloatImplementation(Voxels<FloatBuffer> voxels) {
        super(voxels);
    }

    @Override
    public void copyBufferIndexTo(
            FloatBuffer sourceBuffer,
            int sourceIndex,
            FloatBuffer destinationBuffer,
            int destinationIndex) {
        destinationBuffer.put(destinationIndex, sourceBuffer.get(sourceIndex));
    }

    @Override
    public Voxels<FloatBuffer> projectMax() {

        Extent extent = voxels.extent();

        MaxIntensityBufferFloat mi = new MaxIntensityBufferFloat(extent);

        for (int z = 0; z < extent.z(); z++) {
            mi.projectSlice(voxels.sliceBuffer(z));
        }

        return mi.asVoxels();
    }

    @Override
    public Voxels<FloatBuffer> projectMean() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int voxelWithMaxIntensity() {

        float max = 0;
        boolean first = true;

        Extent extent = voxels.extent();

        for (int z = 0; z < extent.z(); z++) {

            FloatBuffer pixels = voxels.sliceBuffer(z);

            while (pixels.hasRemaining()) {

                float val = pixels.get();
                if (first || val > max) {
                    max = val;
                    first = false;
                }
            }
        }
        return (int) Math.ceil(max);
    }

    @Override
    protected int voxelAtBufferIndex(FloatBuffer buffer, int index) {
        return (int) buffer.get(index);
    }

    @Override
    protected boolean bufferValueGreaterThan(FloatBuffer buffer, int threshold) {
        return buffer.get() > threshold;
    }

    @Override
    protected boolean bufferValueEqualTo(FloatBuffer buffer, int value) {
        return buffer.get() == value;
    }
}
