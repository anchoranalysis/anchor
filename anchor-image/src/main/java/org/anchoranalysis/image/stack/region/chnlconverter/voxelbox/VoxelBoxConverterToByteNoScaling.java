/* (C)2020 */
package org.anchoranalysis.image.stack.region.chnlconverter.voxelbox;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import org.anchoranalysis.image.convert.ByteConverter;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;
import org.anchoranalysis.image.voxel.buffer.VoxelBufferByte;
import org.anchoranalysis.image.voxel.datatype.VoxelDataTypeUnsignedByte;

// Converts voxel buffers to a unsigned 8-bit buffer without scaling any values.
// So values larger than 255 are clipped
public final class VoxelBoxConverterToByteNoScaling implements VoxelBoxConverter<ByteBuffer> {

    @Override
    public VoxelBuffer<ByteBuffer> convertFromFloat(VoxelBuffer<FloatBuffer> bufferIn) {

        ByteBuffer bufferOut = ByteBuffer.allocate(bufferIn.buffer().capacity());

        while (bufferIn.buffer().hasRemaining()) {
            float f = bufferIn.buffer().get();
            if (f > VoxelDataTypeUnsignedByte.MAX_VALUE_INT) {
                f = VoxelDataTypeUnsignedByte.MAX_VALUE_INT;
            }
            if (f < 0) {
                f = 0;
            }
            bufferOut.put((byte) f);
        }

        return VoxelBufferByte.wrap(bufferOut);
    }

    @Override
    public VoxelBuffer<ByteBuffer> convertFromInt(VoxelBuffer<IntBuffer> bufferIn) {

        ByteBuffer bufferOut = ByteBuffer.allocate(bufferIn.buffer().capacity());

        while (bufferIn.buffer().hasRemaining()) {
            bufferOut.put((byte) ByteConverter.unsignedIntToLong(bufferIn.buffer().get()));
        }

        return VoxelBufferByte.wrap(bufferOut);
    }

    @Override
    public VoxelBuffer<ByteBuffer> convertFromShort(VoxelBuffer<ShortBuffer> bufferIn) {

        ByteBuffer bufferOut = ByteBuffer.allocate(bufferIn.buffer().capacity());

        while (bufferIn.buffer().hasRemaining()) {
            bufferOut.put((byte) ByteConverter.unsignedShortToInt(bufferIn.buffer().get()));
        }

        return VoxelBufferByte.wrap(bufferOut);
    }

    @Override
    public VoxelBuffer<ByteBuffer> convertFromByte(VoxelBuffer<ByteBuffer> in) {
        return in.duplicate();
    }
}
