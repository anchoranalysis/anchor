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

package org.anchoranalysis.mpp.mark.points;

import java.io.Serializable;
import org.anchoranalysis.spatial.point.Point3d;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
class DistanceCalculatorToLine implements Serializable {

    /** */
    private static final long serialVersionUID = 1L;

    @Getter private Point3d startPoint;

    @Getter private Point3d endPoint;

    @Getter private Point3d directionVector;

    /**
     * Copy constructor
     *
     * @param src
     */
    public DistanceCalculatorToLine(DistanceCalculatorToLine src) {
        this.startPoint = new Point3d(src.startPoint);
        this.endPoint = new Point3d(src.endPoint);
        this.directionVector = new Point3d(src.directionVector);
    }

    public void setPoints(Point3d startPoint, Point3d endPoint) {
        this.startPoint = new Point3d(startPoint);
        this.endPoint = new Point3d(endPoint);

        // Direction vector
        this.directionVector = new Point3d(this.endPoint);
        this.directionVector.subtract(startPoint);
    }

    public double distanceToLine(Point3d point) {
        // http://mathworld.wolfram.com/Point-LineDistance3-Dimensional.html

        double distanceSquared2to1 = endPoint.distanceSquared(startPoint);
        double distanceSquared1to0 = startPoint.distanceSquared(point);

        // Let's calculation the dot_product
        double firstX = startPoint.x() - point.x();
        double firstY = startPoint.y() - point.y();
        double firstZ = startPoint.z() - point.z();

        double dotProduct =
                (firstX * directionVector.x())
                        + (firstY * directionVector.y())
                        + (firstZ * directionVector.z());

        double num = (distanceSquared2to1 * distanceSquared1to0) - Math.pow(dotProduct, 2);
        return num / distanceSquared2to1;
    }
}
