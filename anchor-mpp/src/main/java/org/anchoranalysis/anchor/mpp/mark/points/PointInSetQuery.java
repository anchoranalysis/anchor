/* (C)2020 */
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
