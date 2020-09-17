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

package org.anchoranalysis.image.stack.region;

import org.anchoranalysis.core.arithmetic.RunningSum;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.geometry.Point2i;
import org.anchoranalysis.image.convert.UnsignedBufferAsInt;
import org.anchoranalysis.image.extent.Extent;

class MeanInterpolator {

    private static final String EXC_ZERO_CNT =
            "\"The interpolator has a count of 0, and cannot return a valid value\"";

    private final Extent sizeToInterpolate;

    /** To avoid repeatedly recreating on the heap */
    private final RunningSum runningSum = new RunningSum();
    
    public MeanInterpolator(double zoomFactor) {
        double zoomFactorInv = 1 / zoomFactor;
        int size = (int) Math.round(zoomFactorInv);

        sizeToInterpolate = new Extent(size,size);
    }

    /**
     * Finds the mean of voxels at a particular point plus a certain extent.
     * 
     * @param cornerPoint
     * @param buffer
     * @param extent
     * @return
     * @throws OperationFailedException
     */
    public double interpolateVoxelsAt(Point2i cornerPoint, UnsignedBufferAsInt buffer, Extent extent)
            throws OperationFailedException {

        runningSum.reset();
        
        sizeToInterpolate.iterateOverXYWithShift(cornerPoint, point -> {
            if (extent.contains(point)) {
                runningSum.increment( buffer.getUnsigned(extent.offset(point)) );
            }
        });

        if (runningSum.getCount() == 0) {
            throw new OperationFailedException(EXC_ZERO_CNT);
        }

        return runningSum.mean();
    }
}
