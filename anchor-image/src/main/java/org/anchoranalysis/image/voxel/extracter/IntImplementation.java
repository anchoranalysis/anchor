package org.anchoranalysis.image.voxel.extracter;

import java.nio.IntBuffer;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.buffer.max.MaxIntensityBufferInt;

class IntImplementation extends Base<IntBuffer> {
    
    public IntImplementation(Voxels<IntBuffer> voxels) {
        super(voxels);
    }

    @Override
    public Voxels<IntBuffer> projectionMax() {

        Extent extent = voxels.extent();
        
        MaxIntensityBufferInt mi = new MaxIntensityBufferInt(extent);

        for (int z = 0; z < extent.z(); z++) {
            mi.projectSlice(voxels.sliceBuffer(z));
        }

        return mi.asVoxels();
    }
    
    @Override
    public Voxels<IntBuffer> projectionMean() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public int voxelWithMaxIntensity() {

        int max = 0;
        boolean first = true;
        
        Extent extent = voxels.extent();

        for (int z = 0; z < extent.z(); z++) {

            IntBuffer pixels = voxels.sliceBuffer(z);

            while (pixels.hasRemaining()) {

                int val = pixels.get();
                if (first || val > max) {
                    max = val;
                    first = false;
                }
            }
        }
        return max;
    }
    
    @Override
    protected void copyBufferIndexTo(IntBuffer sourceBuffer, int sourceIndex, IntBuffer destinationBuffer, int destinationIndex) {
        destinationBuffer.put(destinationIndex, sourceBuffer.get(sourceIndex));
    }

    @Override
    protected int voxelAtBufferIndex(IntBuffer buffer, int index) {
        return buffer.get(index);
    }
    
    @Override
    protected boolean bufferValueGreaterThan(IntBuffer buffer, int threshold) {
        return buffer.get() > threshold;
    }

    @Override
    protected boolean bufferValueEqualTo(IntBuffer buffer, int value) {
        return buffer.get() == value;
    }
}
