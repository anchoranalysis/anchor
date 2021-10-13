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
 * Conversion utilities between points with different types and dimensionalities.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PointConverter {

    // START singular points
    /**
     * Convert a {@link Point2d} to a {@link Point2i} using the <i>floor</i> operation.
     * 
     * @param point the point to convert.
     * @return the newly-created converted point.
     */
    public static Point2i intFromDoubleFloor(Point2d point) {
        return new Point2i((int) point.x(), (int) point.y());
    }

    /**
     * Convert a {@link Point3d} to a {@link Point3i} using the <i>floor</i> operation.
     * 
     * @param point the point to convert.
     * @return the newly-created converted point.
     */
    public static Point3i intFromDoubleFloor(Point3d point) {
        return new Point3i((int) point.x(), (int) point.y(), (int) point.z());
    }

    /**
     * Convert a {@link Point3d} to a {@link Point3i} using the <i>ceiling</i> operation.
     * 
     * @param point the point to convert.
     * @return the newly-created converted point.
     */
    public static Point3i intFromDoubleCeil(Point3d point) {
        return new Point3i(
                (int) Math.ceil(point.x()), (int) Math.ceil(point.y()), (int) Math.ceil(point.z()));
    }

    /**
     * Convert a {@link Point2i} to a {@link Point3i} using {@code 0} as the value of the Z-axis component.
     * 
     * @param point the point to convert.
     * @return the newly-created converted point.
     */
    public static Point3i convertTo3i(Point2i point) {
        return new Point3i(point.x(), point.y(), 0);
    }

    /**
     * Convert a {@link Point3i} to a {@link Point2i} using an explicit value for the Z-axis component.
     * 
     * @param point the point to convert.
     * @param z the value for the Z-axis component in the newly created point.
     * @return the newly-created converted point.
     */
    public static Point3i convertTo3i(Point2i point, int z) {
        return new Point3i(point.x(), point.y(), z);
    }

    /**
     * Convert a {@link Point2d} to a {@link Point3d} using {@code 0} as the value of the Z-axis component.
     * 
     * @param point the point to convert.
     * @return the newly-created converted point.
     */
    public static Point3d convertTo3d(Point2d point) {
        return new Point3d(point.x(), point.y(), 0);
    }

    /**
     * Convert a {@link Point2f} to a {@link Point3d}.
     * 
     * @param point the point to convert.
     * @return the newly-created converted point.
     */
    public static Point2d doubleFromFloat(Point2f point) {
        return new Point2d(point.x(), point.y());
    }

    /**
     * Convert a {@link Point3f} to a {@link Point3d}.
     * 
     * @param point the point to convert.
     * @return the newly-created converted point.
     */
    public static Point3d doubleFromFloat(Point3f point) {
        return new Point3d(point.x(), point.y(), point.z());
    }

    /**
     * Convert a {@link Point2i} to a {@link Point3d} using {@code 0} as the value of the Z-axis component.
     * 
     * @param point the point to convert.
     * @return the newly-created converted point.
     */
    public static Point3d doubleFromInt(Point2i point) {
        return new Point3d(point.x(), point.y(), 0);
    }

    /**
     * Convert a {@link ReadableTuple3i} to a {@link Point3d}.
     * 
     * @param point the point to convert.
     * @return the newly-created converted point.
     */
    public static Point3d doubleFromInt(ReadableTuple3i point) {
        return new Point3d(point.x(), point.y(), point.z());
    }

    /**
     * Convert a {@link Point2i} to a {@link Point3f} using {@code 0} as the value of the Z-axis component.
     * 
     * @param point the point to convert.
     * @return the newly-created converted point.
     */
    public static Point3f floatFromInt(Point2i point) {
        return new Point3f(point.x(), point.y(), 0);
    }
    
    /**
     * Convert a {@link Point2d} to a {@link Point2f}.
     * 
     * @param point the point to convert.
     * @return the newly-created converted point.
     */
    public static Point2f floatFromDouble(Point2d point) {
        return new Point2f((float) point.x(), (float) point.y());
    }

    /**
     * Convert a {@link Point3d} to a {@link Point3f}.
     * 
     * @param point the point to convert.
     * @return the newly-created converted point.
     */
    public static Point3f floatFromDouble(Point3d point) {
        return new Point3f((float) point.x(), (float) point.y(), (float) point.z());
    }

    /**
     * Convert a {@link ReadableTuple3i} to a {@link Point3f}.
     * 
     * @param point the point to convert.
     * @return the newly-created converted point.
     */
    public static Point3f floatFromInt(ReadableTuple3i point) {
        return new Point3f(point.x(), point.y(), point.z());
    }

    /**
     * Creates a {@link ReadableTuple3i} to a {@link Point3f}, preserving X- and Y-components but set setting the Z-component to be {@code 0}.
     * 
     * @param point the point to convert.
     * @return the newly-created converted point.
     */
    public static Point3f floatFromIntDropZ(ReadableTuple3i point) {
        return new Point3f(point.x(), point.y(), 0);
    }

    /**
     * Convert a {@link Point3f} to a {@link Point3i}, optionally rounding values.
     * 
     * @param point the point to convert.
     * @param round if true, each component's value is rounded to the nearest integer, otherwise the <i>ceiling</i> operation is used.
     * @return the newly-created converted point.
     */
    public static Point3i intFromFloat(Point3f point, boolean round) {
        if (round) {
            return new Point3i(roundInt(point.x()), roundInt(point.y()), roundInt(point.z()));
        } else {
            return new Point3i(ceilInt(point.x()), ceilInt(point.y()), ceilInt(point.z()));
        }
    }
    // END singular points

    
    // START lists of points

    /**
     * Convert a list of {@link Point3i} to a list of {@link Point3f}.
     * 
     * @param points the list of points to convert.
     * @return a newly-created list of converted points.
     */
    public static List<Point3f> convert3iTo3f(List<Point3i> points) {
        return convert(points, PointConverter::floatFromInt);
    }

    /**
     * Convert a list of {@link Point3d} to a list of {@link Point3f}.
     * 
     * @param points the list of points to convert.
     * @return a newly-created list of converted points.
     */
    public static List<Point3f> convert3dTo3f(List<Point3d> points) {
        return convert(points, PointConverter::floatFromDouble);
    }

    /**
     * Convert a list of {@link Point2i} to a list of {@link Point3f}.
     * 
     * @param points the list of points to convert.
     * @return a newly-created list of converted points.
     */
    public static List<Point3f> convert2iTo3f(List<Point2i> points) {
        return convert(points, PointConverter::floatFromInt);
    }

    /**
     * Convert a list of {@link Point2i} to a list of {@link Point3d}.
     * 
     * @param points the list of points to convert.
     * @return a newly-created list of converted points.
     */
    public static List<Point3d> convert2iTo3d(List<Point2i> points) {
        return convert(points, PointConverter::doubleFromInt);
    }

    /**
     * Convert a list of {@link Point3f} to a list of {@link Point3d}.
     * 
     * @param points the list of points to convert.
     * @return a newly-created list of converted points.
     */
    public static List<Point3d> convert3fTo3d(List<Point3f> points) {
        return convert(points, PointConverter::doubleFromFloat);
    }

    /**
     * Convert a list of {@link Point3f} to a list of {@link Point3i}, optionally rounding values.
     * 
     * @param points the list of points to convert.
     * @param round if true, each component's value is rounded to the nearest integer, otherwise the <i>ceiling</i> operation is used.
     * @return a newly-created list of converted points.
     */
    public static List<Point3i> convert3i(List<Point3f> points, boolean round) {
        return convert(points, point -> intFromFloat(point, round));
    }

    /**
     * Convert a list of {@link Point3d} to a list of {@link Point3i}.
     * 
     * @param points the list of points to convert.
     * @return a newly-created list of converted points.
     */
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
