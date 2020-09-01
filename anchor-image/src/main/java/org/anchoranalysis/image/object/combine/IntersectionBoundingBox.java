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

package org.anchoranalysis.image.object.combine;

import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.Extent;

/**
 * A bounding box where intersection occurs
 *
 * <p>We deliberately avoid getters and setters for code optimization reasons as the particular
 * routines are computationally sensitive
 *
 * <p>(we make inlining as easy as possible for the JVM)
 *
 * @author Owen Feehan
 */
public class IntersectionBoundingBox {

    private Dimension x;
    private Dimension y;
    private Dimension z;
    private Extent e1; // Extent of source box
    private Extent e2; // Extent of other box

    public static class Dimension {

        private int min; // Min point of intersection box
        private int max; // Max point of intersection box
        private int rel; // Relative position other to src

        public Dimension(int min, int max, int rel) {
            super();
            this.min = min;
            this.max = max;
            this.rel = rel;
        }

        public int min() {
            return min;
        }

        public int max() {
            return max;
        }

        public int rel() {
            return rel;
        }
    }

    public static IntersectionBoundingBox create(
            BoundingBox boxSource, BoundingBox boxOther, BoundingBox boxIntersect) {

        Point3i relativePositionSource = boxIntersect.relativePositionTo(boxSource);

        Point3i relativePositionDestinationToSource =
                Point3i.immutableSubtract(boxSource.cornerMin(), boxOther.cornerMin());

        Point3i relativePositionSourceMax =
                Point3i.immutableAdd(relativePositionSource, boxIntersect.extent().asTuple());

        return new IntersectionBoundingBox(
                relativePositionSource,
                relativePositionSourceMax,
                relativePositionDestinationToSource,
                boxSource.extent(),
                boxOther.extent());
    }

    private IntersectionBoundingBox(
            Point3i pointMin,
            Point3i pointMax,
            Point3i relativePosition,
            Extent extentSource,
            Extent extentOther) {
        x = new Dimension(pointMin.x(), pointMax.x(), relativePosition.x());
        y = new Dimension(pointMin.y(), pointMax.y(), relativePosition.y());
        z = new Dimension(pointMin.z(), pointMax.z(), relativePosition.z());
        this.e1 = extentSource;
        this.e2 = extentOther;
    }

    public Dimension x() {
        return x;
    }

    public Dimension y() {
        return y;
    }

    public Dimension z() {
        return z;
    }

    public Extent e1() {
        return e1;
    }

    public Extent e2() {
        return e2;
    }
}
