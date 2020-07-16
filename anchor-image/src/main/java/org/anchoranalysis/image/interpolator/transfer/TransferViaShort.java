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
package org.anchoranalysis.image.interpolator.transfer;

import java.nio.ShortBuffer;
import org.anchoranalysis.image.interpolator.Interpolator;
import org.anchoranalysis.image.voxel.box.VoxelBox;
import org.anchoranalysis.image.voxel.box.VoxelBoxWrapper;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;

// Lots of copying bytes, which doesn't make it very efficient
// Doesn't seem to be a way to make a BufferedImage using existing butes without ending up with a
// BufferedImage.CUSTOM_TYPE
//   type which messes up our scaling
public class TransferViaShort implements Transfer {

    private VoxelBox<ShortBuffer> src;
    private VoxelBox<ShortBuffer> trgt;
    private VoxelBuffer<ShortBuffer> buffer;

    public TransferViaShort(VoxelBoxWrapper src, VoxelBoxWrapper trgt) {
        this.src = src.asShort();
        this.trgt = trgt.asShort();
    }

    @Override
    public void assignSlice(int z) {
        buffer = src.getPixelsForPlane(z);
    }

    @Override
    public void transferCopyTo(int z) {
        trgt.setPixelsForPlane(z, buffer.duplicate());
    }

    @Override
    public void transferTo(int z, Interpolator interpolator) {

        VoxelBuffer<ShortBuffer> bufIn = trgt.getPixelsForPlane(z);
        VoxelBuffer<ShortBuffer> bufOut =
                interpolator.interpolateShort(buffer, bufIn, src.extent(), trgt.extent());
        if (!bufOut.equals(bufIn)) {
            trgt.setPixelsForPlane(z, bufOut);
        }
    }
}
