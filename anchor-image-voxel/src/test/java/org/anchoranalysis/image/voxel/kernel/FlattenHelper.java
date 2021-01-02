package org.anchoranalysis.image.voxel.kernel;

import org.anchoranalysis.spatial.Extent;
import org.anchoranalysis.spatial.point.Point3i;
import lombok.NoArgsConstructor;

@NoArgsConstructor
class FlattenHelper {
    
    public static Extent maybeFlattenExtent(Extent extent, boolean do3D) {
        return do3D ? extent : extent.flattenZ();
    }
    
    public static Point3i maybeFlattenPoint(Point3i point, boolean do3D) {
        return do3D ? point : new Point3i(point.x(), point.y(), 0);
    }
}
