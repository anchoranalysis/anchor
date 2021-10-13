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

package org.anchoranalysis.image.voxel.interpolator;

import java.nio.FloatBuffer;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedShortBuffer;
import org.anchoranalysis.spatial.box.Extent;

/** Doesn't do any interpolation, just copies values */
public class InterpolatorNone implements Interpolator {

    @Override
    public VoxelBuffer<UnsignedByteBuffer> interpolateByte(
            VoxelBuffer<UnsignedByteBuffer> voxelsSource,
            VoxelBuffer<UnsignedByteBuffer> voxelsDestination,
            Extent extentSource,
            Extent extentDestination) {

        copyByte(
                voxelsSource.buffer(), voxelsDestination.buffer(), extentSource, extentDestination);
        return voxelsDestination;
    }

    @Override
    public VoxelBuffer<UnsignedShortBuffer> interpolateShort(
            VoxelBuffer<UnsignedShortBuffer> voxelsSource,
            VoxelBuffer<UnsignedShortBuffer> voxelsDestination,
            Extent extentSource,
            Extent extentDestination) {
        copyShort(
                voxelsSource.buffer(), voxelsDestination.buffer(), extentSource, extentDestination);
        return voxelsDestination;
    }

    @Override
    public VoxelBuffer<FloatBuffer> interpolateFloat(
            VoxelBuffer<FloatBuffer> voxelsSource,
            VoxelBuffer<FloatBuffer> voxelsDestination,
            Extent extentSource,
            Extent extentDestination) {
        copyFloat(
                voxelsSource.buffer(), voxelsDestination.buffer(), extentSource, extentDestination);
        return voxelsDestination;
    }

    private static void copyByte(
            UnsignedByteBuffer bufferIn,
            UnsignedByteBuffer bufferOut,
            Extent extentIn,
            Extent extentOut) {

        double xScale = intDiv(extentIn.x(), extentOut.x());
        double yScale = intDiv(extentIn.y(), extentOut.y());

        // We loop through every pixel in the output buffer
        for (int y = 0; y < extentOut.y(); y++) {
            for (int x = 0; x < extentOut.x(); x++) {

                int xOrig = intMin(xScale * x, extentIn.x() - 1);
                int yOrig = intMin(yScale * y, extentIn.y() - 1);

                bufferOut.putRaw(bufferIn.getRaw(extentIn.offset(xOrig, yOrig)));
            }
        }
    }

    private static void copyShort(
            UnsignedShortBuffer bufferIn,
            UnsignedShortBuffer bufferOut,
            Extent extentIn,
            Extent extentOut) {

        double xScale = intDiv(extentIn.x(), extentOut.x());
        double yScale = intDiv(extentIn.y(), extentOut.y());

        // We loop through every pixel in the output buffer
        for (int y = 0; y < extentOut.y(); y++) {
            for (int x = 0; x < extentOut.x(); x++) {

                int xOrig = intMin(xScale * x, extentIn.x() - 1);
                int yOrig = intMin(yScale * y, extentIn.y() - 1);

                bufferOut.putRaw(bufferIn.getRaw(extentIn.offset(xOrig, yOrig)));
            }
        }
    }

    private static void copyFloat(
            FloatBuffer bufferIn, FloatBuffer bufferOut, Extent extentIn, Extent extentOut) {

        double xScale = intDiv(extentIn.x(), extentOut.x());
        double yScale = intDiv(extentIn.y(), extentOut.y());

        // We loop through every pixel in the output buffer
        for (int y = 0; y < extentOut.y(); y++) {
            for (int x = 0; x < extentOut.x(); x++) {

                int xOrig = intMin(xScale * x, extentIn.x() - 1);
                int yOrig = intMin(yScale * y, extentIn.y() - 1);

                bufferOut.put(bufferIn.get(extentIn.offset(xOrig, yOrig)));
            }
        }
    }

    private static double intDiv(int num, int dem) {
        return ((double) num) / dem;
    }

    private static int intMin(double val1, int val2) {
        return (int) Math.min(Math.round(val1), val2);
    }

    @Override
    public boolean isNewValuesPossible() {
        return false;
    }
}
