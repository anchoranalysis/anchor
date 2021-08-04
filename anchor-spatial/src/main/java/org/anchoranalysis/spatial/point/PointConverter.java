package org.anchoranalysis.spatial.point;

/*
 * #%L
 * anchor-image
 * %%
 * Copyright (C) 2016 ETH Zurich, University of Zurich, Owen Feehan
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

import java.util.List;
import java.util.function.Function;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.functional.FunctionalList;

/**
 * Conversion utilities between different types and dimensionalities of points
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PointConverter {

    // START singular points

    public static Point2i intFromDoubleFloor(Point2d point) {
        return new Point2i((int) point.x(), (int) point.y());
    }

    public static Point3i intFromDoubleFloor(Point3d point) {
        return new Point3i((int) point.x(), (int) point.y(), (int) point.z());
    }

    public static Point3i intFromDoubleCeil(Point3d point) {
        return new Point3i(
                (int) Math.ceil(point.x()), (int) Math.ceil(point.y()), (int) Math.ceil(point.z()));
    }

    public static Point3i convertTo3i(Point2i point) {
        return new Point3i(point.x(), point.y(), 0);
    }

    public static Point3i convertTo3i(Point2i point, int slice) {
        return new Point3i(point.x(), point.y(), slice);
    }

    public static Point3d convertTo3d(Point2d point) {
        return new Point3d(point.x(), point.y(), 0);
    }

    public static Point2d doubleFromFloat(Point2f point) {
        return new Point2d(point.x(), point.y());
    }

    public static Point3d doubleFromFloat(Point3f point) {
        return new Point3d(point.x(), point.y(), point.z());
    }

    public static Point3d doubleFromInt(Point2i point) {
        return new Point3d(point.x(), point.y(), 0);
    }

    public static Point3d doubleFromInt(ReadableTuple3i point) {
        return new Point3d(point.x(), point.y(), point.z());
    }

    public static Point3f floatFromInt(Point2i point) {
        return new Point3f(point.x(), point.y(), 0);
    }

    public static Point3f floatFromDouble(Point3d point) {
        return new Point3f((float) point.x(), (float) point.y(), (float) point.z());
    }

    public static Point3f floatFromInt(ReadableTuple3i point) {
        return new Point3f(point.x(), point.y(), point.z());
    }

    /**
     * Creates a new {@code Point3f} with X, Y values from the {@code Point3i} but setting Z to be 0
     */
    public static Point3f floatFromIntDropZ(ReadableTuple3i point) {
        return new Point3f(point.x(), point.y(), 0);
    }

    public static Point3i intFromFloat(Point3f point, boolean round) {
        if (round) {
            return new Point3i(roundInt(point.x()), roundInt(point.y()), roundInt(point.z()));
        } else {
            return new Point3i(ceilInt(point.x()), ceilInt(point.y()), ceilInt(point.z()));
        }
    }

    // END singular points

    // START lists of points

    public static List<Point3f> convert3iTo3f(List<Point3i> points) {
        return convert(points, PointConverter::floatFromInt);
    }

    public static List<Point3f> convert3dTo3f(List<Point3d> points) {
        return convert(points, PointConverter::floatFromDouble);
    }

    public static List<Point3f> convert2iTo3f(List<Point2i> points) {
        return convert(points, PointConverter::floatFromInt);
    }

    public static List<Point3d> convert2iTo3d(List<Point2i> points) {
        return convert(points, PointConverter::doubleFromInt);
    }

    public static List<Point3d> convert3fTo3d(List<Point3f> points) {
        return convert(points, PointConverter::doubleFromFloat);
    }

    public static List<Point3i> convert3i(List<Point3f> points, boolean round) {
        return convert(points, point -> intFromFloat(point, round));
    }

    public static List<Point3i> convert3i(List<Point3d> points) {
        return convert(points, PointConverter::intFromDoubleFloor);
    }

    // END lists of points

    private static <S, T> List<S> convert(List<T> points, Function<T, S> mapFunction) {
        return FunctionalList.mapToList(points, mapFunction);
    }

    private static int roundInt(double value) {
        return (int) Math.round(value);
    }

    private static int ceilInt(double value) {
        return (int) Math.ceil(value);
    }
}
