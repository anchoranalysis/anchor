/* (C)2020 */
package org.anchoranalysis.image.stack.region.chnlconverter.voxelbox;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import org.anchoranalysis.image.convert.ByteConverter;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;
import org.anchoranalysis.image.voxel.buffer.VoxelBufferByte;

// Converts voxel buffers to a unsigned 8-bit buffer scaling against a the minimum and maximum
// constant.
// Linear between these two limits.
//
// Note that the Type.MAX_VALUE in Java assumes siged types.  So we multiply by two to get unsigned
// sizes
public final class VoxelBoxConverterToByteScaleByMinMaxValue
        implements VoxelBoxConverter<ByteBuffer> {

    private double scale = 0;
    private int subtract = 0;

    public VoxelBoxConverterToByteScaleByMinMaxValue(int minValue, int maxValue) {
        super();
        setMinMaxValues(minValue, maxValue);
    }

    public void setMinMaxValues(int minValue, int maxValue) {
        this.scale = 255.0 / (maxValue - minValue);
        this.subtract = minValue;
    }

    // This doesn't really make sense for a float, as the maximum value is so much higher, so we
    // take
    //  it as being the same as Integer.MAX_VALUE
    @Override
    public VoxelBuffer<ByteBuffer> convertFromFloat(VoxelBuffer<FloatBuffer> bufferIn) {

        ByteBuffer bufferOut = ByteBuffer.allocate(bufferIn.buffer().capacity());

        while (bufferIn.buffer().hasRemaining()) {
            float val = bufferIn.buffer().get();

            val = (int) scale * (val - subtract);

            if (val > 255) {
                val = 255;
            }
            if (val < 0) {
                val = 0;
            }

            bufferOut.put((byte) val);
        }

        return VoxelBufferByte.wrap(bufferOut);
    }

    @Override
    public VoxelBuffer<ByteBuffer> convertFromInt(VoxelBuffer<IntBuffer> bufferIn) {

        ByteBuffer bufferOut = ByteBuffer.allocate(bufferIn.buffer().capacity());

        while (bufferIn.buffer().hasRemaining()) {

            long valOrig = bufferIn.buffer().get();

            double val = scale * (valOrig - subtract);

            if (val > 255) {
                val = 255;
            }
            if (val < 0) {
                val = 0;
            }

            bufferOut.put((byte) val);
        }

        return VoxelBufferByte.wrap(bufferOut);
    }

    @Override
    public VoxelBuffer<ByteBuffer> convertFromShort(VoxelBuffer<ShortBuffer> bufferIn) {

        ByteBuffer bufferOut = ByteBuffer.allocate(bufferIn.buffer().capacity());

        while (bufferIn.buffer().hasRemaining()) {

            int valOrig = ByteConverter.unsignedShortToInt(bufferIn.buffer().get());

            double val = scale * (valOrig - subtract);

            if (val > 255) {
                val = 255;
            }
            if (val < 0) {
                val = 0;
            }

            bufferOut.put((byte) val);
        }

        return VoxelBufferByte.wrap(bufferOut);
    }

    @Override
    public VoxelBuffer<ByteBuffer> convertFromByte(VoxelBuffer<ByteBuffer> in) {
        return in.duplicate();
    }
}
