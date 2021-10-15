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

package org.anchoranalysis.image.voxel.binary.connected;

import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.binary.BinaryVoxels;
import org.anchoranalysis.image.voxel.binary.values.BinaryValues;
import org.anchoranalysis.image.voxel.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedIntBuffer;
import org.anchoranalysis.image.voxel.iterator.process.buffer.ProcessBufferUnary;
import org.anchoranalysis.spatial.point.Point3i;

class PopulateIndexProcessor<T> implements ProcessBufferUnary<T> {

    private Voxels<UnsignedIntBuffer> indexBuffer;
    private MergeWithNeighbors mergeWithNeighbors;
    private BinaryValues binaryValues;
    private BinaryValuesByte binaryValyesByte;
    private final BufferReadWrite<T> bufferReaderWriter;

    private UnsignedIntBuffer bufferIndex;
    private int count = 1;

    public PopulateIndexProcessor(
            BinaryVoxels<T> visited,
            Voxels<UnsignedIntBuffer> indexBuffer,
            MergeWithNeighbors mergeWithNeighbors,
            BufferReadWrite<T> bufferReaderWriter) {
        this.indexBuffer = indexBuffer;
        this.mergeWithNeighbors = mergeWithNeighbors;
        this.bufferReaderWriter = bufferReaderWriter;

        binaryValues = visited.binaryValues();
        binaryValyesByte = binaryValues.asByte();
    }

    @Override
    public void notifyChangeSlice(int z) {
        bufferIndex = indexBuffer.sliceBuffer(z);
        if (z != 0) {
            mergeWithNeighbors.shift();
        }
    }

    @Override
    public void process(Point3i point, T buffer, int offsetSlice) {
        if (bufferReaderWriter.isBufferOn(buffer, offsetSlice, binaryValues, binaryValyesByte)
                && bufferIndex.getRaw(offsetSlice) == 0) {

            int neighborLabel = mergeWithNeighbors.minNeighborLabel(point, 0, offsetSlice);
            if (neighborLabel == -1) {
                bufferReaderWriter.putBufferCount(buffer, offsetSlice, count);
                bufferIndex.putRaw(offsetSlice, count);
                mergeWithNeighbors.addElement(count);
                count++;
            } else {
                bufferIndex.put(offsetSlice, neighborLabel);
            }
        }
    }

    public int getCount() {
        return count;
    }
}
