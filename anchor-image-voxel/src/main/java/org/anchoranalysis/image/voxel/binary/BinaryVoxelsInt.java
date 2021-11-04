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

package org.anchoranalysis.image.voxel.binary;

import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.binary.values.BinaryValuesInt;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedIntBuffer;

class BinaryVoxelsInt extends BinaryVoxels<UnsignedIntBuffer> {

    public BinaryVoxelsInt(Voxels<UnsignedIntBuffer> voxels, BinaryValuesInt binaryValues) {
        super(voxels, binaryValues);
    }

    @Override
    public boolean isOn(int x, int y, int z) {
        int offset = voxels().extent().offset(x, y);
        return voxels().sliceBuffer(z).getRaw(offset) != binaryValues().getOffInt();
    }

    @Override
    public boolean isOff(int x, int y, int z) {
        return !isOn(x, y, z);
    }

    @Override
    public void setOn(int x, int y, int z) {
        int offset = voxels().extent().offset(x, y);
        voxels().sliceBuffer(z).putRaw(offset, binaryValues().getOnInt());
    }

    @Override
    public void setOff(int x, int y, int z) {
        int offset = voxels().extent().offset(x, y);
        voxels().sliceBuffer(z).putRaw(offset, binaryValues().getOffInt());
    }

    @Override
    public BinaryVoxels<UnsignedIntBuffer> duplicate() {
        return new BinaryVoxelsInt(voxels().duplicate(), binaryValues());
    }

    @Override
    protected BinaryVoxels<UnsignedIntBuffer> binaryVoxelsFor(
            Voxels<UnsignedIntBuffer> voxels, BinaryValuesInt binaryValues) {
        return new BinaryVoxelsInt(voxels, binaryValues);
    }
}
