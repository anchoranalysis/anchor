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
package org.anchoranalysis.image.extent;

import java.io.Serializable;
import org.anchoranalysis.core.axis.AxisType;
import org.anchoranalysis.core.geometry.Point3d;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.core.geometry.Tuple3d;
import org.anchoranalysis.image.scale.ScaleFactor;

/**
 * The resolution of an image i.e. what a single voxel represents in physical units (meters) in x,
 * y, z
 *
 * <p>This class is IMMUTABLE.
 *
 * @author Owen Feehan
 */
public final class ImageResolution implements Serializable {

    /** */
    private static final long serialVersionUID = 1L;

    // Stores in Metres
    private final Point3d res;

    public ImageResolution() {
        this(1.0, 1.0, 1.0);
    }

    public ImageResolution(double x, double y, double z) {
        this(new Point3d(x, y, z));
    }

    public ImageResolution(Tuple3d res) {
        // Copy to ensure it is independent of any changes outside
        this.res = new Point3d(res);
    }

    public ImageResolution duplicateFlattenZ(int prevZSize) {
        return new ImageResolution(res.getX(), res.getY(), res.getZ() * prevZSize);
    }

    public double getX() {
        return res.getX();
    }

    public double getY() {
        return res.getY();
    }

    public double getZ() {
        return res.getZ();
    }

    public double unitVolume() {
        return getX() * getY() * getZ();
    }

    public double unitArea() {
        return getX() * getY();
    }

    public ImageResolution scaleXY(ScaleFactor sf) {
        return new ImageResolution(res.getX() * sf.getX(), res.getY() * sf.getY(), res.getZ());
    }

    private double max2D() {
        return Math.max(res.getX(), res.getY());
    }

    private double min2D() {
        return Math.min(res.getX(), res.getY());
    }

    public double max(boolean do3D) {

        if (do3D) {
            return Math.max(max2D(), res.getZ());
        } else {
            return max2D();
        }
    }

    public double min(boolean do3D) {
        if (do3D) {
            return Math.min(min2D(), res.getZ());
        } else {
            return min2D();
        }
    }

    public double distanceSq(Point3i point1, Point3i point2) {

        double sx = (double) point1.getX() - point2.getX();
        double sy = (double) point1.getY() - point2.getY();
        double sz = (double) point1.getZ() - point2.getZ();

        sx *= getX();
        sy *= getY();
        sz *= getZ();

        return Math.pow(sx, 2) + Math.pow(sy, 2) + Math.pow(sz, 2);
    }

    public double distanceSq(Point3d point1, Point3d point2) {

        double sx = point1.getX() - point2.getX();
        double sy = point1.getY() - point2.getY();
        double sz = point1.getZ() - point2.getZ();

        sx *= getX();
        sy *= getY();
        sz *= getZ();

        return Math.pow(sx, 2) + Math.pow(sy, 2) + Math.pow(sz, 2);
    }

    public double distance(Point3d point1, Point3d point2) {
        return Math.sqrt(distanceSq(point1, point2));
    }

    public double distance(Point3i point1, Point3i point2) {
        return Math.sqrt(distanceSq(point1, point2));
    }

    public double distanceSquaredZRelative(Point3i point1, Point3i point2) {

        int sx = point1.getX() - point2.getX();
        int sy = point1.getY() - point2.getY();
        int sz = point1.getZ() - point2.getZ();

        double szAdj = getZRelativeResolution() * sz;

        return Math.pow(sx, 2) + Math.pow(sy, 2) + Math.pow(szAdj, 2);
    }

    public double distanceZRelative(Point3d point1, Point3d point2) {
        return Math.sqrt(distanceSquaredZRelative(point1, point2));
    }

    public double distanceSquaredZRelative(Point3d point1, Point3d point2) {

        double sx = point1.getX() - point2.getX();
        double sy = point1.getY() - point2.getY();
        double sz = point1.getZ() - point2.getZ();

        sz = sz * getZRelativeResolution();

        return Math.pow(sx, 2) + Math.pow(sy, 2) + Math.pow(sz, 2);
    }

    public double convertVolume(double val) {
        double div = res.getX() * res.getY() * res.getZ();
        return val * div;
    }

    public double convertArea(double val) {
        double div = res.getX() * res.getY();
        return val * div;
    }

    // Assumes X and Y has constant res, and gives the relative resolution of Z
    public double getZRelativeResolution() {
        return getZ() / getX();
    }

    @Override
    public String toString() {
        return String.format("[%6.3e,%6.3e,%6.3e]", res.getX(), res.getY(), res.getZ());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((res == null) ? 0 : res.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        ImageResolution other = (ImageResolution) obj;
        if (res == null) {
            if (other.res != null) return false;
        } else if (!res.equals(other.res)) return false;
        return true;
    }

    public final double getValueByDimension(int dimIndex) {
        return res.getValueByDimension(dimIndex);
    }

    public final double getValueByDimension(AxisType axisType) {
        return res.getValueByDimension(axisType);
    }
}
