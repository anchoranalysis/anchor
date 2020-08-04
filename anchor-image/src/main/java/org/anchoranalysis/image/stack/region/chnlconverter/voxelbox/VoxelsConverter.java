/*-
 * #%L
 * anchor-image
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

package org.anchoranalysis.image.stack.region.chnlconverter.voxelbox;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.VoxelsWrapper;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;
import org.anchoranalysis.image.voxel.datatype.VoxelDataTypeFloat;
import org.anchoranalysis.image.voxel.datatype.VoxelDataTypeUnsignedByte;
import org.anchoranalysis.image.voxel.datatype.VoxelDataTypeUnsignedInt;
import org.anchoranalysis.image.voxel.datatype.VoxelDataTypeUnsignedShort;
import org.anchoranalysis.image.voxel.factory.VoxelsFactoryTypeBound;

/**
 * @author Owen Feehan
 * @param <T> desgination-type
 */
public interface VoxelsConverter<T extends Buffer> {

    default Voxels<T> convertFrom(VoxelsWrapper vbIn, VoxelsFactoryTypeBound<T> factory) {
        Voxels<T> vbOut = factory.createInitialized(vbIn.any().extent());
        convertFrom(vbIn, vbOut);
        return vbOut;
    }

    default void convertFrom(VoxelsWrapper vbIn, Voxels<T> vbOut) {
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

    default void convertFromByte(Voxels<ByteBuffer> vbIn, Voxels<T> vbOut) {

        for (int z = 0; z < vbIn.extent().getZ(); z++) {
            VoxelBuffer<ByteBuffer> bufferIn = vbIn.getPixelsForPlane(z);
            vbOut.setPixelsForPlane(z, convertFromByte(bufferIn));
        }
    }

    default void convertFromFloat(Voxels<FloatBuffer> vbIn, Voxels<T> vbOut) {

        for (int z = 0; z < vbIn.extent().getZ(); z++) {
            VoxelBuffer<FloatBuffer> bufferIn = vbIn.getPixelsForPlane(z);
            vbOut.setPixelsForPlane(z, convertFromFloat(bufferIn));
        }
    }

    default void convertFromInt(Voxels<IntBuffer> vbIn, Voxels<T> vbOut) {

        for (int z = 0; z < vbIn.extent().getZ(); z++) {
            VoxelBuffer<IntBuffer> bufferIn = vbIn.getPixelsForPlane(z);
            vbOut.setPixelsForPlane(z, convertFromInt(bufferIn));
        }
    }

    default void convertFromShort(Voxels<ShortBuffer> vbIn, Voxels<T> vbOut) {

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
