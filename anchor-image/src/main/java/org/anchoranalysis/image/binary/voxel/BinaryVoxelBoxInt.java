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
/* (C)2020 */
package org.anchoranalysis.image.binary.voxel;

import java.nio.IntBuffer;
import org.anchoranalysis.image.binary.values.BinaryValues;
import org.anchoranalysis.image.voxel.box.VoxelBox;

public class BinaryVoxelBoxInt extends BinaryVoxelBox<IntBuffer> {

    public BinaryVoxelBoxInt(VoxelBox<IntBuffer> voxelBox, BinaryValues bv) {
        super(voxelBox, bv);
    }

    @Override
    public boolean isOn(int x, int y, int z) {
        int offset = getVoxelBox().extent().offset(x, y);
        return getVoxelBox().getPixelsForPlane(z).buffer().get(offset)
                != getBinaryValues().getOffInt();
    }

    @Override
    public boolean isOff(int x, int y, int z) {
        return !isOn(x, y, z);
    }

    @Override
    public void setOn(int x, int y, int z) {
        int offset = getVoxelBox().extent().offset(x, y);
        getVoxelBox().getPixelsForPlane(z).buffer().put(offset, getBinaryValues().getOnInt());
    }

    @Override
    public void setOff(int x, int y, int z) {
        int offset = getVoxelBox().extent().offset(x, y);
        getVoxelBox().getPixelsForPlane(z).buffer().put(offset, getBinaryValues().getOffInt());
    }

    @Override
    public BinaryVoxelBox<IntBuffer> duplicate() {
        return new BinaryVoxelBoxInt(getVoxelBox().duplicate(), getBinaryValues());
    }

    public BinaryVoxelBox<IntBuffer> extractSlice(int z) {
        return new BinaryVoxelBoxInt(getVoxelBox().extractSlice(z), getBinaryValues());
    }
}
