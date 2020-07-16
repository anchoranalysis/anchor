/* (C)2020 */
package org.anchoranalysis.image.points;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.geometry.Point3d;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.object.ObjectMask;
import org.anchoranalysis.image.outline.FindOutline;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PointsFromObject {

    /**
     * A list of points from the entire object-mask
     *
     * @param object
     * @return
     * @throws CreateException
     */
    public static List<Point3i> fromAsInteger(ObjectMask object) {
        List<Point3i> points = new ArrayList<>();
        PointsFromBinaryVoxelBox.addPointsFromVoxelBox3D(
                object.binaryVoxelBox(), object.getBoundingBox().cornerMin(), points);
        return points;
    }

    /**
     * A list of points from the entire object-mask
     *
     * @param object
     * @return
     */
    public static List<Point3d> fromAsDouble(ObjectMask object) {
        List<Point3d> points = new ArrayList<>();
        PointsFromBinaryVoxelBox.addPointsFromVoxelBox3DDouble(
                object.binaryVoxelBox(), object.getBoundingBox().cornerMin(), points);
        return points;
    }

    /**
     * A list of points from the outline of an object-mask
     *
     * @param objects
     * @return
     * @throws CreateException
     */
    public static List<Point3i> pointsFromMaskOutline(ObjectMask objects) {
        List<Point3i> points = new ArrayList<>();
        ObjectMask outline = FindOutline.outline(objects, 1, false, true);
        PointsFromBinaryVoxelBox.addPointsFromVoxelBox3D(
                outline.binaryVoxelBox(), outline.getBoundingBox().cornerMin(), points);
        return points;
    }

    public static Set<Point3i> pointsFromMaskOutlineSet(ObjectMask objects) {

        Set<Point3i> points = new HashSet<>();

        ObjectMask outline = FindOutline.outline(objects, 1, false, false);
        PointsFromBinaryVoxelBox.addPointsFromVoxelBox3D(
                outline.binaryVoxelBox(), outline.getBoundingBox().cornerMin(), points);
        // Now get all the points on the outline
        return points;
    }
}
