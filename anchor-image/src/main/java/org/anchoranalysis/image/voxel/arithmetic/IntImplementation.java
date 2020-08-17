package org.anchoranalysis.image.voxel.arithmetic;

import java.nio.IntBuffer;
import java.util.function.IntFunction;
import org.anchoranalysis.image.extent.Extent;

class IntImplementation extends Base<IntBuffer> {
    
    public IntImplementation(Extent extent, IntFunction<IntBuffer> bufferForSlice) {
        super(extent, bufferForSlice);
    }

    @Override
    protected void multiplyBuffer(IntBuffer buffer, double factor) {
        while (buffer.hasRemaining()) {
            int mult = (int) (buffer.get() * factor);
            buffer.put(buffer.position() - 1, mult);
        }
    }

    @Override
    protected void subtractFromBuffer(IntBuffer buffer, int valueToSubtractFrom) {
        while (buffer.hasRemaining()) {
            int newVal = valueToSubtractFrom - buffer.get();
            buffer.put(buffer.position() - 1, newVal);
        }
    }

    // TODO when values are too small or too large
    @Override
    protected void addToBufferIndex(IntBuffer buffer, int index, int valueToBeAdded) {
        int intVal = buffer.get(index) + valueToBeAdded;
        buffer.put(index, intVal);
    }

    @Override
    protected void multiplyByBufferIndex(IntBuffer buffer, int index, double factor) {
        throw new IllegalArgumentException("Currently unsupported method");
    }
}
