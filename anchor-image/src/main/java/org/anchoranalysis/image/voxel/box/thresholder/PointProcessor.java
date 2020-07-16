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

package org.anchoranalysis.image.voxel.box.thresholder;

import java.nio.ByteBuffer;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.convert.ByteConverter;
import org.anchoranalysis.image.voxel.box.VoxelBox;
import org.anchoranalysis.image.voxel.iterator.ProcessVoxelSliceBuffer;

final class PointProcessor implements ProcessVoxelSliceBuffer<ByteBuffer> {

    private final int level;
    private final VoxelBox<ByteBuffer> boxOut;
    private final byte byteOn;
    private final byte byteOff;

    private ByteBuffer bbOut;

    public PointProcessor(int level, VoxelBox<ByteBuffer> boxOut, BinaryValuesByte bvOut) {
        super();
        this.level = level;
        this.boxOut = boxOut;
        this.byteOn = bvOut.getOnByte();
        this.byteOff = bvOut.getOffByte();
    }

    @Override
    public void notifyChangeZ(int z) {
        bbOut = boxOut.getPixelsForPlane(z).buffer();
    }

    @Override
    public void process(Point3i point, ByteBuffer buffer, int offset) {
        int val = ByteConverter.unsignedByteToInt(buffer.get(offset));

        bbOut.put(offset, val >= level ? byteOn : byteOff);
    }
}
