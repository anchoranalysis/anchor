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

package org.anchoranalysis.image.voxel.buffer.mean;

import java.nio.FloatBuffer;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;
import org.anchoranalysis.image.voxel.factory.VoxelsFactory;
import org.anchoranalysis.spatial.Extent;

public class MeanIntensityByteBuffer extends MeanIntensityBuffer<UnsignedByteBuffer> {

    /** Simple constructor since no preprocessing is necessary. */
    public MeanIntensityByteBuffer(Extent srcExtent) {
        super(VoxelsFactory.getUnsignedByte(), srcExtent);
    }

    @Override
    protected void processPixel(UnsignedByteBuffer pixels, int index) {
        incrementSumBuffer(index, pixels.getUnsigned(index));
    }

    @Override
    public void finalizeBuffer() {
        int maxIndex = volumeXY();

        UnsignedByteBuffer bufferFlat = flatBuffer();
        FloatBuffer bufferSum = sumBuffer();
        for (int i = 0; i < maxIndex; i++) {
            bufferFlat.putFloat(i, bufferSum.get(i) / numberSlicesProcessed());
        }
    }
}
