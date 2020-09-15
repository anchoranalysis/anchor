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

package org.anchoranalysis.image.interpolator.transfer;

import org.anchoranalysis.image.convert.UnsignedShortBuffer;
import org.anchoranalysis.image.interpolator.Interpolator;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.VoxelsWrapper;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;

// Lots of copying bytes, which doesn't make it very efficient
// Doesn't seem to be a way to make a BufferedImage using existing butes without ending up with a
// BufferedImage.CUSTOM_TYPE
//   type which messes up our scaling
public class TransferViaShort implements Transfer {

    private final Voxels<UnsignedShortBuffer> source;
    private final Voxels<UnsignedShortBuffer> destination;
    private VoxelBuffer<UnsignedShortBuffer> slice;

    public TransferViaShort(VoxelsWrapper source, VoxelsWrapper destination) {
        this.source = source.asShort();
        this.destination = destination.asShort();
    }

    @Override
    public void assignSlice(int z) {
        slice = source.slice(z);
    }

    @Override
    public void transferCopyTo(int z) {
        destination.replaceSlice(z, slice.duplicate());
    }

    @Override
    public void transferTo(int z, Interpolator interpolator) {

        VoxelBuffer<UnsignedShortBuffer> bufIn = destination.slice(z);
        VoxelBuffer<UnsignedShortBuffer> bufOut =
                interpolator.interpolateShort(slice, bufIn, source.extent(), destination.extent());
        if (!bufOut.equals(bufIn)) {
            destination.replaceSlice(z, bufOut);
        }
    }
}
