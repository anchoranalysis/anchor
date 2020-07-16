/* (C)2020 */
package org.anchoranalysis.image.stack.region.chnlconverter.voxelbox;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import org.anchoranalysis.image.voxel.box.VoxelBox;
import org.anchoranalysis.image.voxel.box.VoxelBoxWrapper;
import org.anchoranalysis.image.voxel.box.factory.VoxelBoxFactoryTypeBound;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;
import org.anchoranalysis.image.voxel.datatype.VoxelDataTypeFloat;
import org.anchoranalysis.image.voxel.datatype.VoxelDataTypeUnsignedByte;
import org.anchoranalysis.image.voxel.datatype.VoxelDataTypeUnsignedInt;
import org.anchoranalysis.image.voxel.datatype.VoxelDataTypeUnsignedShort;

/**
 * @author Owen Feehan
 * @param <T> desgination-type
 */
public interface VoxelBoxConverter<T extends Buffer> {

    default VoxelBox<T> convertFrom(VoxelBoxWrapper vbIn, VoxelBoxFactoryTypeBound<T> factory) {
        VoxelBox<T> vbOut = factory.create(vbIn.any().extent());
        convertFrom(vbIn, vbOut);
        return vbOut;
    }

    default void convertFrom(VoxelBoxWrapper vbIn, VoxelBox<T> vbOut) {
        // Otherwise, depending on the input type we spawn in different directions
        VoxelDataType inType = vbIn.getVoxelDataType();
        if (inType.equals(VoxelDataTypeUnsignedByte.INSTANCE)) {
            convertFromByte(vbIn.asByte(), vbOut);
        } else if (inType.equals(VoxelDataTypeFloat.INSTANCE)) {
            convertFromFloat(vbIn.asFloat(), vbOut);
        } else if (inType.equals(VoxelDataTypeUnsignedShort.INSTANCE)) {
            convertFromShort(vbIn.asShort(), vbOut);
        } else if (inType.equals(VoxelDataTypeUnsignedInt.INSTANCE)) {
            convertFromInt(vbIn.asInt(), vbOut);
        }
    }

    default void convertFromByte(VoxelBox<ByteBuffer> vbIn, VoxelBox<T> vbOut) {

        for (int z = 0; z < vbIn.extent().getZ(); z++) {
            VoxelBuffer<ByteBuffer> bufferIn = vbIn.getPixelsForPlane(z);
            vbOut.setPixelsForPlane(z, convertFromByte(bufferIn));
        }
    }

    default void convertFromFloat(VoxelBox<FloatBuffer> vbIn, VoxelBox<T> vbOut) {

        for (int z = 0; z < vbIn.extent().getZ(); z++) {
            VoxelBuffer<FloatBuffer> bufferIn = vbIn.getPixelsForPlane(z);
            vbOut.setPixelsForPlane(z, convertFromFloat(bufferIn));
        }
    }

    default void convertFromInt(VoxelBox<IntBuffer> vbIn, VoxelBox<T> vbOut) {

        for (int z = 0; z < vbIn.extent().getZ(); z++) {
            VoxelBuffer<IntBuffer> bufferIn = vbIn.getPixelsForPlane(z);
            vbOut.setPixelsForPlane(z, convertFromInt(bufferIn));
        }
    }

    default void convertFromShort(VoxelBox<ShortBuffer> vbIn, VoxelBox<T> vbOut) {

        for (int z = 0; z < vbIn.extent().getZ(); z++) {
            VoxelBuffer<ShortBuffer> bufferIn = vbIn.getPixelsForPlane(z);
            vbOut.setPixelsForPlane(z, convertFromShort(bufferIn));
        }
    }

    VoxelBuffer<T> convertFromByte(VoxelBuffer<ByteBuffer> in);

    VoxelBuffer<T> convertFromFloat(VoxelBuffer<FloatBuffer> in);

    VoxelBuffer<T> convertFromInt(VoxelBuffer<IntBuffer> in);

    VoxelBuffer<T> convertFromShort(VoxelBuffer<ShortBuffer> in);
}
