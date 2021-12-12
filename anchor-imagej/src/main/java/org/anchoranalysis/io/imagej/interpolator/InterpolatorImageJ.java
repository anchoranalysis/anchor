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

package org.anchoranalysis.io.imagej.interpolator;

import ij.process.ImageProcessor;
import java.nio.FloatBuffer;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedShortBuffer;
import org.anchoranalysis.image.voxel.interpolator.Interpolator;
import org.anchoranalysis.io.imagej.convert.ConvertToImageProcessor;
import org.anchoranalysis.io.imagej.convert.ConvertToVoxelBuffer;
import org.anchoranalysis.spatial.box.Extent;

/**
 * An implementation of {@link Interpolator} that uses ImageJ.
 *
 * @author Owen Feehan
 */
public class InterpolatorImageJ extends Interpolator {

    @Override
    public boolean canValueRangeChange() {
        return true;
    }

    @Override
    public VoxelBuffer<UnsignedByteBuffer> interpolateByte(
            VoxelBuffer<UnsignedByteBuffer> voxelsSource,
            VoxelBuffer<UnsignedByteBuffer> voxelsDestination,
            Extent extentSource,
            Extent extentDestination) {

        ImageProcessor source = ConvertToImageProcessor.fromByte(voxelsSource, extentSource);
        ImageProcessor destination = resize(source, extentDestination);
        return ConvertToVoxelBuffer.asByte(destination);
    }

    @Override
    public VoxelBuffer<UnsignedShortBuffer> interpolateShort(
            VoxelBuffer<UnsignedShortBuffer> voxelsSource,
            VoxelBuffer<UnsignedShortBuffer> voxelsDestination,
            Extent extentSource,
            Extent extentDestination) {

        ImageProcessor source = ConvertToImageProcessor.fromShort(voxelsSource, extentSource);
        ImageProcessor destination = resize(source, extentDestination);
        return ConvertToVoxelBuffer.asShort(destination);
    }

    @Override
    public VoxelBuffer<FloatBuffer> interpolateFloat(
            VoxelBuffer<FloatBuffer> voxelsSource,
            VoxelBuffer<FloatBuffer> voxelsDestination,
            Extent extentSource,
            Extent extentDestination) {
        ImageProcessor source = ConvertToImageProcessor.fromFloat(voxelsSource, extentSource);
        ImageProcessor destination = resize(source, extentDestination);
        return ConvertToVoxelBuffer.asFloat(destination);
    }

    private static ImageProcessor resize(ImageProcessor source, Extent extentDestination) {
        source.setInterpolate(true);
        return source.resize(extentDestination.x(), extentDestination.y(), true);
    }
}
