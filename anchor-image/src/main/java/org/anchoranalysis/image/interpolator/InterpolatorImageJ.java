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

package org.anchoranalysis.image.interpolator;

import ij.process.ImageProcessor;
import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import org.anchoranalysis.image.convert.IJWrap;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;

public class InterpolatorImageJ implements Interpolator {

    @Override
    public VoxelBuffer<ByteBuffer> interpolateByte(
            VoxelBuffer<ByteBuffer> voxelsSource,
            VoxelBuffer<ByteBuffer> voxelsDestination,
            Extent extentSource,
            Extent extentDestination) {

        ImageProcessor ipSrc = IJWrap.imageProcessorByte(voxelsSource, extentSource);
        ImageProcessor ipOut = ipSrc.resize(extentDestination.x(), extentDestination.y(), true);
        return IJWrap.voxelBufferFromImageProcessorByte(ipOut);
    }

    @Override
    public VoxelBuffer<ShortBuffer> interpolateShort(
            VoxelBuffer<ShortBuffer> voxelsSource,
            VoxelBuffer<ShortBuffer> voxelsDestination,
            Extent extentSource,
            Extent extentDestination) {

        ImageProcessor ipSrc = IJWrap.imageProcessorShort(voxelsSource, extentSource);
        ImageProcessor ipOut = ipSrc.resize(extentDestination.x(), extentDestination.y(), true);
        return IJWrap.voxelBufferFromImageProcessorShort(ipOut);
    }

    @Override
    public boolean isNewValuesPossible() {
        return true;
    }
}
