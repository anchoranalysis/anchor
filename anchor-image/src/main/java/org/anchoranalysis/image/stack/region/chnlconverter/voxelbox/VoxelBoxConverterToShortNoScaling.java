/* (C)2020 */
package org.anchoranalysis.image.stack.region.chnlconverter.voxelbox;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import org.anchoranalysis.image.convert.ByteConverter;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;
import org.anchoranalysis.image.voxel.buffer.VoxelBufferShort;
import org.anchoranalysis.image.voxel.datatype.VoxelDataTypeUnsignedShort;

// Converts voxel buffers to a unsigned 8-bit buffer without scaling any values.
// So values larger than 255 are clipped
public final class VoxelBoxConverterToShortNoScaling implements VoxelBoxConverter<ShortBuffer> {

    @Override
    public VoxelBuffer<ShortBuffer> convertFromFloat(VoxelBuffer<FloatBuffer> bufferIn) {

        ShortBuffer bufferOut = ShortBuffer.allocate(bufferIn.buffer().capacity());

        while (bufferIn.buffer().hasRemaining()) {
            float f = bufferIn.buffer().get();
            if (f > VoxelDataTypeUnsignedShort.MAX_VALUE_INT) {
                f = VoxelDataTypeUnsignedShort.MAX_VALUE_INT;
            }
            if (f < 0) {
                f = 0;
            }
            bufferOut.put((short) f);
        }

        return VoxelBufferShort.wrap(bufferOut);
    }

    @Override
    public VoxelBuffer<ShortBuffer> convertFromInt(VoxelBuffer<IntBuffer> bufferIn) {

        ShortBuffer bufferOut = ShortBuffer.allocate(bufferIn.buffer().capacity());

        while (bufferIn.buffer().hasRemaining()) {
            bufferOut.put((byte) ByteConverter.unsignedIntToShort(bufferIn.buffer().get()));
        }

        return VoxelBufferShort.wrap(bufferOut);
    }

    @Override
    public VoxelBuffer<ShortBuffer> convertFromShort(VoxelBuffer<ShortBuffer> bufferIn) {
        return bufferIn.duplicate();
    }

    @Override
    public VoxelBuffer<ShortBuffer> convertFromByte(VoxelBuffer<ByteBuffer> bufferIn) {
        ShortBuffer bufferOut = ShortBuffer.allocate(bufferIn.buffer().capacity());

        while (bufferIn.buffer().hasRemaining()) {
            bufferOut.put((byte) ByteConverter.unsignedByteToShort(bufferIn.buffer().get()));
        }

        return VoxelBufferShort.wrap(bufferOut);
    }
}
