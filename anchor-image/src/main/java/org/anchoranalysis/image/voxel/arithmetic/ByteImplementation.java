package org.anchoranalysis.image.voxel.arithmetic;

import java.nio.ByteBuffer;
import java.util.function.IntFunction;
import org.anchoranalysis.image.convert.ByteConverter;
import org.anchoranalysis.image.extent.Extent;

class ByteImplementation extends Base<ByteBuffer> {

    public ByteImplementation(Extent extent, IntFunction<ByteBuffer> bufferForSlice) {
        super(extent, bufferForSlice);
    }

    @Override
    protected void multiplyBuffer(ByteBuffer buffer, double factor) {
        while (buffer.hasRemaining()) {
            byte mult = scaleClippedByte(factor, buffer.get());
            buffer.put(buffer.position() - 1, mult);
        }
    }

    @Override
    protected void subtractFromBuffer(ByteBuffer buffer, int valueToSubtractFrom) {
        while (buffer.hasRemaining()) {
            byte subtracted = subtractFromClippedByte(valueToSubtractFrom, buffer.get());
            buffer.put(buffer.position() - 1, subtracted);
        }
    }

    @Override
    protected void multiplyByBufferIndex(ByteBuffer buffer, int index, double factor) {
        byte mult = scaleClippedByte(factor, buffer.get(index));
        buffer.put(index, mult);
    }

    @Override
    protected void addToBufferIndex(ByteBuffer buffer, int index, int valueToBeAdded) {
        byte added = addClippedByte(valueToBeAdded, buffer.get(index));
        buffer.put(index, added);
    }

    private static byte addClippedByte(int value, byte pixelValue) {
        return (byte) addClipped(value, ByteConverter.unsignedByteToInt(pixelValue));
    }

    private static byte subtractFromClippedByte(int valueToSubtractFrom, byte pixelValue) {
        return (byte) (valueToSubtractFrom - ByteConverter.unsignedByteToInt(pixelValue));
    }

    private static byte scaleClippedByte(double factor, byte pixelValue) {
        return (byte) scaleClipped(factor, ByteConverter.unsignedByteToInt(pixelValue));
    }

    private static int scaleClipped(double factor, int pixelValue) {
        int intVal = (int) Math.round(factor * pixelValue);
        if (intVal < 0) {
            return 0;
        }
        if (intVal > 255) {
            return 255;
        }
        return intVal;
    }

    private static int addClipped(int value, int pixelValue) {
        int intVal = pixelValue + value;
        if (intVal < 0) {
            return 0;
        }
        if (intVal > 255) {
            return 255;
        }
        return intVal;
    }
}
