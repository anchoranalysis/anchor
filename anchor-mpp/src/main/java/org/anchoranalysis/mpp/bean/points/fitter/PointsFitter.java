/*-
 * #%L
 * anchor-mpp
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

package org.anchoranalysis.mpp.bean.points.fitter;

import java.util.List;
import org.anchoranalysis.bean.annotation.GroupingRoot;
import org.anchoranalysis.image.core.dimensions.Dimensions;
import org.anchoranalysis.mpp.bean.points.PointsBean;
import org.anchoranalysis.mpp.mark.CompatibleWithMark;
import org.anchoranalysis.mpp.mark.Mark;
import org.anchoranalysis.spatial.point.Point3f;

/**
 * Abstract base class for fitting a mark (e.g., an ellipsoid) to a set of points.
 *
 * <p>This class extends {@link PointsBean} and implements {@link CompatibleWithMark}, providing a
 * framework for various point fitting algorithms.
 */
@GroupingRoot
public abstract class PointsFitter extends PointsBean<PointsFitter> implements CompatibleWithMark {

    /**
     * Fits a mark to a set of points within given dimensions.
     *
     * @param points the list of points to fit the mark to
     * @param mark the mark to be fitted (will be modified)
     * @param dimensions the dimensions within which the fitting occurs
     * @throws PointsFitterException if an error occurs during the fitting process
     * @throws InsufficientPointsException if there are not enough points to perform the fitting
     */
    public abstract void fit(List<Point3f> points, Mark mark, Dimensions dimensions)
            throws PointsFitterException, InsufficientPointsException;
}
