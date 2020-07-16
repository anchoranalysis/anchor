/* (C)2020 */
package org.anchoranalysis.image.stack.region.chnlconverter.voxelbox;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;
import org.anchoranalysis.image.voxel.buffer.VoxelBufferByte;
import org.anchoranalysis.image.voxel.datatype.VoxelDataTypeUnsignedByte;
import org.anchoranalysis.image.voxel.datatype.VoxelDataTypeUnsignedInt;
import org.anchoranalysis.image.voxel.datatype.VoxelDataTypeUnsignedShort;

// Converts voxel buffers to a unsigned 8-bit buffer scaling against the maximum value in each
// buffer.
// So there is no clipping of values, but some might become very small.
//
// Note that the Type.MAX_VALUE in Java assumes siged types.  So we multiply by two to get unsigned
// sizes
public final class VoxelBoxConverterToByteScaleByType implements VoxelBoxConverter<ByteBuffer> {

    // This doesn't really make sense for a float, as the maximum value is so much higher, so we
    // take
    //  it as being the same as Integer.MAX_VALUE
    @Override
    public VoxelBuffer<ByteBuffer> convertFromFloat(VoxelBuffer<FloatBuffer> bufferIn) {

        double div =
                (double) VoxelDataTypeUnsignedInt.MAX_VALUE
                        / VoxelDataTypeUnsignedByte.MAX_VALUE_INT;

        ByteBuffer bufferOut = ByteBuffer.allocate(bufferIn.buffer().capacity());

        while (bufferIn.buffer().hasRemaining()) {
            double f = bufferIn.buffer().get();

            f = f / div;

            bufferOut.put((byte) f);
        }

        return VoxelBufferByte.wrap(bufferOut);
    }

    @Override
    public VoxelBuffer<ByteBuffer> convertFromInt(VoxelBuffer<IntBuffer> bufferIn) {

        double div =
                (double) VoxelDataTypeUnsignedInt.MAX_VALUE / VoxelDataTypeUnsignedByte.MAX_VALUE;

        ByteBuffer bufferOut = ByteBuffer.allocate(bufferIn.buffer().capacity());

        while (bufferIn.buffer().hasRemaining()) {
            bufferOut.put((byte) (bufferIn.buffer().get() / div));
        }

        return VoxelBufferByte.wrap(bufferOut);
    }

    @Override
    public VoxelBuffer<ByteBuffer> convertFromShort(VoxelBuffer<ShortBuffer> bufferIn) {

        double div =
                (double) VoxelDataTypeUnsignedShort.MAX_VALUE / VoxelDataTypeUnsignedByte.MAX_VALUE;

        ByteBuffer bufferOut = ByteBuffer.allocate(bufferIn.buffer().capacity());

        while (bufferIn.buffer().hasRemaining()) {
            bufferOut.put((byte) (bufferIn.buffer().get() / div));
        }

        return VoxelBufferByte.wrap(bufferOut);
    }

    @Override
    public VoxelBuffer<ByteBuffer> convertFromByte(VoxelBuffer<ByteBuffer> in) {
        return in.duplicate();
    }
}
