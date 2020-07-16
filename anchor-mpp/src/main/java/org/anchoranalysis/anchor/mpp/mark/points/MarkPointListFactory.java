/* (C)2020 */
package org.anchoranalysis.anchor.mpp.mark.points;

import com.google.common.base.Preconditions;
import java.util.List;
import java.util.function.Function;
import org.anchoranalysis.core.geometry.Point3d;
import org.anchoranalysis.core.geometry.Point3f;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.core.geometry.PointConverter;

public class MarkPointListFactory {

    private MarkPointListFactory() {}

    public static MarkPointList create(List<Point3d> points) {
        return create(points, -1);
    }

    public static MarkPointList create(List<Point3d> points, int id) {
        MarkPointList mark = new MarkPointList();
        mark.getPoints().addAll(points);
        mark.setId(id);
        mark.updateAfterPointsChange();
        return mark;
    }

    public static MarkPointList createMarkFromPoints3f(List<Point3f> points) {
        return createMarkFromPoints(points, PointConverter::doubleFromFloat);
    }

    public static MarkPointList createMarkFromPoints3i(List<Point3i> points) {
        return createMarkFromPoints(points, PointConverter::doubleFromInt);
    }

    private static <T> MarkPointList createMarkFromPoints(
            List<T> points, Function<T, Point3d> convert) {
        Preconditions.checkArgument(!points.isEmpty(), "are empty");

        return new MarkPointList(points.stream().map(convert));
    }
}
