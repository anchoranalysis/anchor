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

package org.anchoranalysis.image.voxel.thresholder;

import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;
import org.anchoranalysis.image.voxel.iterator.process.buffer.ProcessBufferUnary;
import org.anchoranalysis.spatial.point.Point3i;

/**
 * Puts an <i>on</i> voxel in the output-buffer if {@code voxel-value >= level} or <i>off</i>
 * otherwise.
 *
 * @author Owen Feehan
 */
final class ThresholdEachVoxel implements ProcessBufferUnary<UnsignedByteBuffer> {

    private final int level;
    private final Voxels<UnsignedByteBuffer> voxelsOut;
    private final byte byteOn;
    private final byte byteOff;

    private UnsignedByteBuffer bufferOut;

    public ThresholdEachVoxel(
            int level, Voxels<UnsignedByteBuffer> boxOut, BinaryValuesByte bvOut) {
        super();
        this.level = level;
        this.voxelsOut = boxOut;
        this.byteOn = bvOut.getOnByte();
        this.byteOff = bvOut.getOffByte();
    }

    @Override
    public void notifyChangeSlice(int z) {
        bufferOut = voxelsOut.sliceBuffer(z);
    }

    @Override
    public void process(Point3i point, UnsignedByteBuffer buffer, int offset) {
        int value = buffer.getUnsigned(offset);
        bufferOut.putRaw(offset, value >= level ? byteOn : byteOff);
    }
}
