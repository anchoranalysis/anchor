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

package org.anchoranalysis.image.channel.converter.voxels;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.function.Function;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.VoxelsWrapper;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;
import org.anchoranalysis.image.voxel.datatype.FloatVoxelType;
import org.anchoranalysis.image.voxel.datatype.UnsignedByteVoxelType;
import org.anchoranalysis.image.voxel.datatype.UnsignedIntVoxelType;
import org.anchoranalysis.image.voxel.datatype.UnsignedShortVoxelType;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;
import org.anchoranalysis.image.voxel.factory.VoxelsFactoryTypeBound;

/**
 * Converts voxels from one data-type to another
 *
 * @author Owen Feehan
 * @param <T> destination-type (what the voxels will be converted <b>to</b>)
 */
public abstract class VoxelsConverter<T extends Buffer> {

    public Voxels<T> convertFrom(VoxelsWrapper voxelsIn, VoxelsFactoryTypeBound<T> factory) {
        Voxels<T> voxelsOut = factory.createInitialized(voxelsIn.any().extent());
        convertFrom(voxelsIn, voxelsOut);
        return voxelsOut;
    }

    public void convertFrom(VoxelsWrapper voxelsIn, Voxels<T> voxelsOut) {
        // Otherwise, depending on the input type we spawn in different directions
        VoxelDataType inType = voxelsIn.getVoxelDataType();
        if (inType.equals(UnsignedByteVoxelType.INSTANCE)) {
            convertFromByte(voxelsIn.asByte(), voxelsOut);
        } else if (inType.equals(FloatVoxelType.INSTANCE)) {
            convertFromFloat(voxelsIn.asFloat(), voxelsOut);
        } else if (inType.equals(UnsignedShortVoxelType.INSTANCE)) {
            convertFromShort(voxelsIn.asShort(), voxelsOut);
        } else if (inType.equals(UnsignedIntVoxelType.INSTANCE)) {
            convertFromInt(voxelsIn.asInt(), voxelsOut);
        }
    }

    public void convertFromByte(Voxels<ByteBuffer> in, Voxels<T> out) {
        convertFrom(in, out, this::convertFromByte);
    }

    public void convertFromShort(Voxels<ShortBuffer> in, Voxels<T> out) {
        convertFrom(in, out, this::convertFromShort);
    }

    public void convertFromInt(Voxels<IntBuffer> in, Voxels<T> out) {
        convertFrom(in, out, this::convertFromInt);
    }

    public void convertFromFloat(Voxels<FloatBuffer> in, Voxels<T> out) {
        convertFrom(in, out, this::convertFromFloat);
    }

    public abstract VoxelBuffer<T> convertFromByte(VoxelBuffer<ByteBuffer> in);

    public abstract VoxelBuffer<T> convertFromFloat(VoxelBuffer<FloatBuffer> in);

    public abstract VoxelBuffer<T> convertFromInt(VoxelBuffer<IntBuffer> in);

    public abstract VoxelBuffer<T> convertFromShort(VoxelBuffer<ShortBuffer> in);

    private <S extends Buffer> void convertFrom(
            Voxels<S> in, Voxels<T> out, Function<VoxelBuffer<S>, VoxelBuffer<T>> converter) {
        in.extent().iterateOverZ(z -> out.replaceSlice(z, converter.apply(in.slice(z))));
    }
}
