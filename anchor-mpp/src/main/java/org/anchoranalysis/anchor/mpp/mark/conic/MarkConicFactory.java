/* (C)2020 */
package org.anchoranalysis.anchor.mpp.mark.conic;

import com.google.common.base.Preconditions;
import org.anchoranalysis.anchor.mpp.mark.Mark;
import org.anchoranalysis.core.geometry.Point2d;
import org.anchoranalysis.core.geometry.Point3d;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.core.geometry.PointConverter;
import org.anchoranalysis.image.orientation.Orientation2D;
import org.anchoranalysis.image.orientation.Orientation3DEulerAngles;

public class MarkConicFactory {

    private MarkConicFactory() {}

    public static Mark createMarkFromPoint(Point3i point, int size, boolean do3D) {
        return createMarkFromPoint(PointConverter.doubleFromInt(point), size, do3D);
    }

    public static Mark createMarkFromPoint(Point3d point, int size, boolean do3D) {
        Preconditions.checkArgument(size > 0);
        Preconditions.checkArgument(do3D || point.getZ() == 0);

        if (do3D) {
            MarkEllipsoid me = new MarkEllipsoid();
            me.setMarksExplicit(
                    point, new Orientation3DEulerAngles(), new Point3d(size, size, size));
            return me;
        } else {
            MarkEllipse me = new MarkEllipse();
            me.setMarksExplicit(point, new Orientation2D(), new Point2d(size, size));
            return me;
        }
    }
}
