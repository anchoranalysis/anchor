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
package org.anchoranalysis.image.voxel.iterator.intersecting;

import lombok.AllArgsConstructor;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.convert.UnsignedByteBuffer;
import org.anchoranalysis.image.voxel.iterator.process.buffer.ProcessBufferBinary;
import org.anchoranalysis.image.voxel.iterator.process.buffer.ProcessBufferTernary;

/**
 * Processes voxels checking that they lie on a mask, converting a {@link ProcessBufferTernary} to a
 * {@code ProcessBufferBinary<UnsignedByteBuffer>}.
 *
 * @author Owen Feehan
 */
@AllArgsConstructor
class CheckMask implements ProcessBufferTernary<UnsignedByteBuffer> {

    private final ProcessBufferBinary<UnsignedByteBuffer,UnsignedByteBuffer> process;
    private final byte onMaskGlobal;

    @Override
    public void process(
            Point3i point,
            UnsignedByteBuffer buffer1,
            UnsignedByteBuffer buffer2,
            UnsignedByteBuffer bufferObject,
            int offset1,
            int offset2,
            int offsetObject) {
        byte globalMask = bufferObject.getRaw(offsetObject);
        if (globalMask == onMaskGlobal) {
            process.process(point, buffer1, buffer2, offset1, offset2);
        }
    }

    @Override
    public void notifyChangeSlice(int z) {
        process.notifyChangeSlice(z);
    }
}
