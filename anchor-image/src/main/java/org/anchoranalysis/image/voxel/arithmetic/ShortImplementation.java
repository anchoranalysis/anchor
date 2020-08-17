package org.anchoranalysis.image.voxel.arithmetic;

import java.nio.ShortBuffer;
import java.util.function.IntFunction;
import org.anchoranalysis.image.convert.ByteConverter;
import org.anchoranalysis.image.extent.Extent;

/**
 * TODO what to do when values are too small or too large?
 *
 * @author Owen Feehan
 */
class ShortImplementation extends Base<ShortBuffer> {

    public ShortImplementation(Extent extent, IntFunction<ShortBuffer> bufferForSlice) {
        super(extent, bufferForSlice);
    }

    @Override
    protected void multiplyBuffer(ShortBuffer buffer, double factor) {
        while (buffer.hasRemaining()) {
            short mult = multiplyBy(buffer.get(), factor);
            buffer.put(buffer.position() - 1, mult);
        }
    }

    @Override
    protected void multiplyByBufferIndex(ShortBuffer buffer, int index, double factor) {
        short mult = multiplyBy(buffer.get(index), factor);
        buffer.put(index, mult);
    }

    @Override
    protected void subtractFromBuffer(ShortBuffer buffer, int valueToSubtractFrom) {

        while (buffer.hasRemaining()) {
            // TODO does this also need to use byteconverter?
            int newVal = valueToSubtractFrom - buffer.get();
            buffer.put(buffer.position() - 1, (short) newVal);
        }
    }

    @Override
    protected void addToBufferIndex(ShortBuffer buffer, int index, int valueToBeAdded) {
        // TODO does this also need to use byteconverter?
        short shortVal = (short) (buffer.get(index) + valueToBeAdded);
        buffer.put(index, shortVal);
    }

    private static short multiplyBy(short value, double factor) {
        // TODO do we need to cast to an int first, or can it not just be done directly to a short?
        int mult = (int) (ByteConverter.unsignedShortToInt(value) * factor);
        return (short) mult;
    }
}
