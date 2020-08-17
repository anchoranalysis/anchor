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

package org.anchoranalysis.image.binary.voxel;

import java.nio.ByteBuffer;
import org.anchoranalysis.image.binary.values.BinaryValues;
import org.anchoranalysis.image.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.voxel.Voxels;

class BinaryVoxelsByte extends BinaryVoxels<ByteBuffer> {

    private BinaryValuesByte binaryValuesByte;
    
    public BinaryVoxelsByte(Voxels<ByteBuffer> voxels, BinaryValues binaryValuesByte) {
        super(voxels, binaryValuesByte);
        this.binaryValuesByte = binaryValues().createByte();
    }

    @Override
    public boolean isOn(int x, int y, int z) {
        int offset = voxels().extent().offset(x, y);
        return voxels().sliceBuffer(z).get(offset) != binaryValuesByte.getOffByte();
    }

    @Override
    public boolean isOff(int x, int y, int z) {
        return !isOn(x, y, z);
    }

    @Override
    public void setOn(int x, int y, int z) {
        int offset = voxels().extent().offset(x, y);
        voxels().sliceBuffer(z).put(offset, binaryValuesByte.getOnByte());
    }

    @Override
    public void setOff(int x, int y, int z) {
        int offset = voxels().extent().offset(x, y);
        voxels().sliceBuffer(z).put(offset, binaryValuesByte.getOffByte());
    }

    public BinaryValuesByte asByte() {
        return binaryValuesByte;
    }

    @Override
    public BinaryVoxelsByte duplicate() {
        return new BinaryVoxelsByte(voxels().duplicate(), binaryValues());
    }
    
    @Override
    protected BinaryVoxels<ByteBuffer> binaryVoxelsFor(Voxels<ByteBuffer> slice,
            BinaryValues binaryValues) {
        return new BinaryVoxelsByte(slice, binaryValues);
    }
}
