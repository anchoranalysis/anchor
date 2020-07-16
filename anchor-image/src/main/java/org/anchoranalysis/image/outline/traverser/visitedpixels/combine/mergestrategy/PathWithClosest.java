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
/* (C)2020 */
package org.anchoranalysis.image.outline.traverser.visitedpixels.combine.mergestrategy;

import java.util.List;
import java.util.Optional;
import lombok.Getter;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.outline.traverser.contiguouspath.ContiguousPixelPath;
import org.anchoranalysis.image.outline.traverser.contiguouspath.DistanceToContiguousPath;
import org.anchoranalysis.image.outline.traverser.visitedpixels.LoopablePoints;

class PathWithClosest {

    @Getter private ContiguousPixelPath path;

    private int closest;

    public PathWithClosest(ContiguousPixelPath path, Point3i mergePoint) {
        this.path = path;
        closest = indexClosest(path, mergePoint);
    }

    public Optional<LoopablePoints> removeLeft() {
        return path.removeLeft(closest);
    }

    public Optional<LoopablePoints> removeRight() {
        return path.removeRight(size() - closest - 1);
    }

    public int distanceFromLeft() {
        return closest;
    }

    public int distanceFromRight() {
        return size() - closest - 1;
    }

    public int size() {
        return path.size();
    }

    private static int indexClosest(ContiguousPixelPath path, Point3i mergePoint) {
        return DistanceToContiguousPath.maxDistanceToClosestPoint(path, mergePoint).getIndex();
    }

    public List<Point3i> points() {
        return path.points();
    }

    public void insertBefore(List<Point3i> points) {
        path.insertBefore(points);
        closest += points.size();
    }

    public void insertAfter(List<Point3i> points) {
        path.insertAfter(points);
    }
}
