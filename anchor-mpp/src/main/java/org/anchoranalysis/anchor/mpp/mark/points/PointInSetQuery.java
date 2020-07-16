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

package org.anchoranalysis.anchor.mpp.mark.points;

import java.util.Set;
import java.util.function.DoubleUnaryOperator;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.geometry.Point3d;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class PointInSetQuery {

    public static boolean anyCrnrInSet(Point3d point, Set<Point3d> set) {

        // We test if any combination of the ceil, floor can be found in the set
        //  in 2 dimensions.   i.e. the four corners of a pixel around the point

        return pointInSet(point, set, null, null, null)
                || pointInSet(point, set, Math::floor, Math::ceil, null)
                || pointInSet(point, set, Math::ceil, Math::floor, null)
                || pointInSet(point, set, Math::ceil, Math::ceil, null)
                || pointInSet(point, set, Math::floor, Math::floor, null);
    }

    private static boolean pointInSet(
            Point3d point,
            Set<Point3d> set,
            DoubleUnaryOperator funcX,
            DoubleUnaryOperator funcY,
            DoubleUnaryOperator funcZ) {
        Point3d pointNew =
                new Point3d(
                        applyFuncIfNonNull(point.getX(), funcX),
                        applyFuncIfNonNull(point.getY(), funcY),
                        applyFuncIfNonNull(point.getZ(), funcZ));
        return pointInSet(pointNew, set);
    }

    private static double applyFuncIfNonNull(double in, DoubleUnaryOperator func) {
        if (func != null) {
            return func.applyAsDouble(in);
        } else {
            return in;
        }
    }

    private static boolean pointInSet(Point3d point, Set<Point3d> set) {
        return set.contains(point);
    }
}
