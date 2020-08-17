package org.anchoranalysis.image.voxel.arithmetic;

import java.nio.FloatBuffer;
import java.util.function.IntFunction;
import org.anchoranalysis.image.extent.Extent;

class FloatImplementation extends Base<FloatBuffer> {

    public FloatImplementation(Extent extent, IntFunction<FloatBuffer> bufferForSlice) {
        super(extent, bufferForSlice);
    }
    
    @Override
    protected void multiplyBuffer(FloatBuffer buffer, double factor) {
        while (buffer.hasRemaining()) {
            float mult = (float) (buffer.get() * factor);
            buffer.put(buffer.position() - 1, mult);
        }
    }

    @Override
    protected void subtractFromBuffer(FloatBuffer buffer, int valueToSubtractFrom) {
        while (buffer.hasRemaining()) {
            float newVal = valueToSubtractFrom - buffer.get();
            buffer.put(buffer.position() - 1, newVal);
        }
        
    }

    @Override
    protected void addToBufferIndex(FloatBuffer buffer, int index, int valueToBeAdded) {
        float sum = buffer.get(index) + valueToBeAdded;
        buffer.put(index, sum);
    }

    @Override
    protected void multiplyByBufferIndex(FloatBuffer buffer, int index, double factor) {
        float mult = (float) (buffer.get(index) * factor);
        buffer.put(index, mult);
    }
}
