package org.anchoranalysis.image.voxel.object;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import org.anchoranalysis.spatial.box.PointRange;
import org.anchoranalysis.spatial.point.Point3i;
import org.anchoranalysis.spatial.point.ReadableTuple3i;

/**
 * Derive an {@link ObjectMask} by incrementally adding points.
 *
 * @author Owen Feehan
 */
public class DeriveObjectFromPoints {

    /** Tracks the minimum and maximum along each dimension of the added points. */
    private PointRange range = new PointRange();

    /** Remembers each point that has been added. */
    private List<Point3i> points = new LinkedList<>();

    /**
     * Adds a point to the object.
     *
     * @param point the point to add.
     */
    public void add(ReadableTuple3i point) {
        range.add(point);
        points.add(new Point3i(point));
    }

    /**
     * Derives an {@link ObjectMask} that includes all points that were previously added.
     *
     * <p>The bounding-box of the created {@link ObjectMask} will fit the points as tightly as
     * possible.
     *
     * @return a newly created {@link ObjectMask} if at least one point was added. Otherwise {@link
     *     Optional#empty()}.
     */
    public Optional<ObjectMask> deriveObject() {
        if (!range.isEmpty()) {
            // Assign all the ON voxels to the object
            ObjectMask object = new ObjectMask(range.toBoundingBoxNoCheck());
            for (Point3i point : points) {
                object.assignOn().toVoxel(point);
            }
            return Optional.of(object);
        } else {
            return Optional.empty();
        }
    }
}
