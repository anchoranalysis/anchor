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

package org.anchoranalysis.image.contour;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import org.anchoranalysis.core.geometry.Point3f;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.core.geometry.PointConverter;

/**
 * A path of successively-neighboring points along the edge of an object
 *
 * @author Owen Feehan
 */
public class Contour {

    @Getter private List<Point3f> points = new ArrayList<>();

    private static final double MAX_DISTANCE_TO_DEFINED_CONNECTED = 2;

    public List<Point3i> pointsDiscrete() {
        return PointConverter.convert3i(getPoints(), false);
    }

    public boolean isClosed() {
        return points.get(0).distance(points.get(points.size() - 1))
                < MAX_DISTANCE_TO_DEFINED_CONNECTED;
    }

    public boolean hasPoint(Point3f pointC) {
        for (Point3f point : getPoints()) {
            if (point.equals(pointC)) {
                return true;
            }
        }
        return false;
    }

    public Point3f getFirstPoint() {
        return points.get(0);
    }

    public Point3f getMiddlePoint() {
        return points.get(points.size() / 2);
    }

    public Point3f getLastPoint() {
        return points.get(points.size() - 1);
    }

    public boolean connectedTo(Contour contour) {

        if (connectedToFirstPointOf(contour)) {
            return true;
        }

        return connectedToLastPointOf(contour);
    }

    public boolean connectedToFirstPointOf(Contour contour) {
        return getLastPoint().distance(contour.getFirstPoint()) < MAX_DISTANCE_TO_DEFINED_CONNECTED;
    }

    public boolean connectedToLastPointOf(Contour contour) {
        return getFirstPoint().distance(contour.getLastPoint()) < MAX_DISTANCE_TO_DEFINED_CONNECTED;
    }

    public String summaryStr() {
        return String.format("[%s-%s]", points.get(0), points.get(points.size() - 1));
    }
}
