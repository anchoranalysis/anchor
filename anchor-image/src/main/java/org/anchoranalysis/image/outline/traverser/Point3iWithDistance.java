/* (C)2020 */
package org.anchoranalysis.image.outline.traverser;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.anchoranalysis.core.geometry.Point3i;

@RequiredArgsConstructor
class Point3iWithDistance {

    @Getter private final Point3i point;

    @Getter private final int distance;

    // If non-null, this is a point that is a neighbor but
    //   is disallowed from being on the same contiguous path
    @Getter private Point3i connPoint = null;

    @Override
    public String toString() {
        return String.format("%s--%d", point.toString(), distance);
    }

    public boolean isForceNewPath() {
        return connPoint != null;
    }

    public void markAsNewPath(Point3i connPoint) {
        this.connPoint = connPoint;
    }
}
