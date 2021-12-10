/*-
 * #%L
 * anchor-plugin-opencv
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

package org.anchoranalysis.spatial.scale;

import lombok.AllArgsConstructor;
import org.anchoranalysis.spatial.box.Extent;
import org.anchoranalysis.spatial.point.Point2i;

/**
 * Like {@link ScaleFactor} but only allows integer scaling-factors in each dimension.
 *
 * @author Owen Feehan
 */
@AllArgsConstructor
public class ScaleFactorInt {

    /** How much to multiply the existing x-dimension by to create a scaled x-dimension. */
    private final int x;

    /** How much to multiply the existing y-dimension by to create a scaled y-dimension. */
    private final int y;

    /**
     * Converts to a {@link ScaleFactor} that accepts floating-point values.
     *
     * @return a newly created {@link ScaleFactor} containing identical scaling values.
     */
    public ScaleFactor asScaleFactor() {
        return new ScaleFactor(x, y);
    }

    /**
     * Multiplies a point by the respective scaling-factor in each dimension.
     *
     * @param x the point to be scaled in the X-dimension.
     * @param y the point to be scaled in the Y-dimension.
     * @return a newly created scaled {@link Point2i}.
     */
    public Point2i scale(int x, int y) {
        return new Point2i(scaledX(x), scaledY(y));
    }

    /**
     * Multiplies a {@link Point2i} by the respective scaling-factor in each dimension.
     *
     * @param point the point to be scaled.
     * @return a newly created scaled {@link Point2i}.
     */
    public Point2i scale(Point2i point) {
        return scale(point.x(), point.y());
    }

    /**
     * Multiplies an {@link Extent} by the respective scaling-factor in each dimension.
     *
     * @param extent the extent to be scaled.
     * @return a newly created scaled {@link Extent}.
     */
    public Extent scale(Extent extent) {
        return new Extent(scaledX(extent.x()), scaledY(extent.y()), extent.z());
    }

    private int scaledX(int val) {
        return val * x;
    }

    private int scaledY(int val) {
        return val * y;
    }
}
