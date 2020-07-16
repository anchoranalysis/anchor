/* (C)2020 */
package org.anchoranalysis.image.stack.region.chnlconverter.voxelbox;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import org.anchoranalysis.image.convert.ByteConverter;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;
import org.anchoranalysis.image.voxel.buffer.VoxelBufferFloat;

// Converts voxel buffers to a unsigned 8-bit buffer without scaling any values.
// So values larger than 255 are clipped
public final class VoxelBoxConverterToFloatNoScaling implements VoxelBoxConverter<FloatBuffer> {

    @Override
    public VoxelBuffer<FloatBuffer> convertFromFloat(VoxelBuffer<FloatBuffer> bufferIn) {
        return bufferIn.duplicate();
    }

    @Override
    public VoxelBuffer<FloatBuffer> convertFromInt(VoxelBuffer<IntBuffer> bufferIn) {

        FloatBuffer bufferOut = FloatBuffer.allocate(bufferIn.buffer().capacity());

        while (bufferIn.buffer().hasRemaining()) {
            bufferOut.put((float) bufferIn.buffer().get());
        }

        return VoxelBufferFloat.wrap(bufferOut);
    }

    @Override
    public VoxelBuffer<FloatBuffer> convertFromShort(VoxelBuffer<ShortBuffer> bufferIn) {

        FloatBuffer bufferOut = FloatBuffer.allocate(bufferIn.buffer().capacity());

        while (bufferIn.buffer().hasRemaining()) {
            bufferOut.put((float) bufferIn.buffer().get());
        }

        return VoxelBufferFloat.wrap(bufferOut);
    }

    @Override
    public VoxelBuffer<FloatBuffer> convertFromByte(VoxelBuffer<ByteBuffer> bufferIn) {

        VoxelBufferFloat bufferOut = VoxelBufferFloat.allocate(bufferIn.buffer().capacity());

        while (bufferIn.buffer().hasRemaining()) {
            bufferOut
                    .buffer()
                    .put((float) ByteConverter.unsignedByteToInt(bufferIn.buffer().get()));
        }

        return bufferOut;
    }
}
