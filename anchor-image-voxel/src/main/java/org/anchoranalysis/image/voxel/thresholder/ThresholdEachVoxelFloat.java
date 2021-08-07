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

import java.nio.FloatBuffer;
import org.anchoranalysis.image.voxel.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;
import org.anchoranalysis.image.voxel.iterator.process.buffer.ProcessBufferBinary;
import org.anchoranalysis.spatial.point.Point3i;

/**
 * Puts an <i>on</i> voxel in the output-buffer if {@code voxel-value >= level} or <i>off</i>
 * otherwise.
 *
 * @author Owen Feehan
 */
final class ThresholdEachVoxelFloat
        implements ProcessBufferBinary<FloatBuffer, UnsignedByteBuffer> {

    private final float level;
    private final byte byteOn;
    private final byte byteOff;

    public ThresholdEachVoxelFloat(float level, BinaryValuesByte bvOut) {
        this.level = level;
        this.byteOn = bvOut.getOnByte();
        this.byteOff = bvOut.getOffByte();
    }

    @Override
    public void process(
            Point3i point,
            FloatBuffer buffer1,
            UnsignedByteBuffer buffer2,
            int offset1,
            int offset2) {
        float value = buffer1.get(offset1);
        buffer2.putRaw(offset2, value >= level ? byteOn : byteOff);
    }
}
