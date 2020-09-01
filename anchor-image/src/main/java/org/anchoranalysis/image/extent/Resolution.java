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

package org.anchoranalysis.image.extent;

import java.io.Serializable;
import lombok.EqualsAndHashCode;
import org.anchoranalysis.core.axis.AxisType;
import org.anchoranalysis.core.geometry.Point3d;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.core.geometry.Tuple3d;
import org.anchoranalysis.image.scale.ScaleFactor;

/**
 * The resolution of an image i.e. what a single voxel represents in physical units (meters) in x,
 * y, z
 *
 * <p>This class is <b>immutable</b>.
 *
 * @author Owen Feehan
 */
@EqualsAndHashCode
public final class Resolution implements Serializable {

    /** */
    private static final long serialVersionUID = 1L;

    // Stores in Metres
    private final Point3d res;

    public Resolution() {
        this(1.0, 1.0, 1.0);
    }

    public Resolution(double x, double y, double z) {
        this(new Point3d(x, y, z));
    }

    public Resolution(Tuple3d res) {
        // Copy to ensure it is independent of any changes outside
        this.res = new Point3d(res);
    }

    public Resolution duplicateFlattenZ(int prevZSize) {
        return new Resolution(res.x(), res.y(), res.z() * prevZSize);
    }

    public double x() {
        return res.x();
    }

    public double y() {
        return res.y();
    }

    public double z() {
        return res.z();
    }

    public double unitVolume() {
        return x() * y() * z();
    }

    public double unitArea() {
        return x() * y();
    }

    public Resolution scaleXY(ScaleFactor sf) {
        return new Resolution(res.x() * sf.x(), res.y() * sf.y(), res.z());
    }

    private double max2D() {
        return Math.max(res.x(), res.y());
    }

    private double min2D() {
        return Math.min(res.x(), res.y());
    }

    public double max(boolean do3D) {

        if (do3D) {
            return Math.max(max2D(), res.z());
        } else {
            return max2D();
        }
    }

    public double min(boolean do3D) {
        if (do3D) {
            return Math.min(min2D(), res.z());
        } else {
            return min2D();
        }
    }

    public double distanceSq(Point3i point1, Point3i point2) {

        double sx = (double) point1.x() - point2.x();
        double sy = (double) point1.y() - point2.y();
        double sz = (double) point1.z() - point2.z();

        sx *= x();
        sy *= y();
        sz *= z();

        return Math.pow(sx, 2) + Math.pow(sy, 2) + Math.pow(sz, 2);
    }

    public double distanceSq(Point3d point1, Point3d point2) {

        double sx = point1.x() - point2.x();
        double sy = point1.y() - point2.y();
        double sz = point1.z() - point2.z();

        sx *= x();
        sy *= y();
        sz *= z();

        return Math.pow(sx, 2) + Math.pow(sy, 2) + Math.pow(sz, 2);
    }

    public double distance(Point3d point1, Point3d point2) {
        return Math.sqrt(distanceSq(point1, point2));
    }

    public double distance(Point3i point1, Point3i point2) {
        return Math.sqrt(distanceSq(point1, point2));
    }

    public double distanceSquaredZRelative(Point3i point1, Point3i point2) {

        int sx = point1.x() - point2.x();
        int sy = point1.y() - point2.y();
        int sz = point1.z() - point2.z();

        double szAdj = getZRelativeResolution() * sz;

        return Math.pow(sx, 2) + Math.pow(sy, 2) + Math.pow(szAdj, 2);
    }

    public double distanceZRelative(Point3d point1, Point3d point2) {
        return Math.sqrt(distanceSquaredZRelative(point1, point2));
    }

    public double distanceSquaredZRelative(Point3d point1, Point3d point2) {

        double sx = point1.x() - point2.x();
        double sy = point1.y() - point2.y();
        double sz = point1.z() - point2.z();

        sz = sz * getZRelativeResolution();

        return Math.pow(sx, 2) + Math.pow(sy, 2) + Math.pow(sz, 2);
    }

    public double convertVolume(double val) {
        double div = res.x() * res.y() * res.z();
        return val * div;
    }

    public double convertArea(double val) {
        double div = res.x() * res.y();
        return val * div;
    }

    // Assumes X and Y has constant res, and gives the relative resolution of Z
    public double getZRelativeResolution() {
        return z() / x();
    }

    @Override
    public String toString() {
        return String.format("[%6.3e,%6.3e,%6.3e]", res.x(), res.y(), res.z());
    }

    public final double valueByDimension(int dimIndex) {
        return res.valueByDimension(dimIndex);
    }

    public final double valueByDimension(AxisType axisType) {
        return res.valueByDimension(axisType);
    }
}
