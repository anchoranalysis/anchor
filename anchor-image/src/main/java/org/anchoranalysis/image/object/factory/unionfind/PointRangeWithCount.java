/* (C)2020 */
package org.anchoranalysis.image.object.factory.unionfind;

import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.points.PointRange;

class PointRangeWithCount {

    private PointRange pointRange = new PointRange();
    private int count = 0;

    public void add(Point3i point) {
        pointRange.add(point);
        count++;
    }

    public int getCount() {
        return count;
    }

    public BoundingBox deriveBoundingBox() throws OperationFailedException {
        return pointRange.deriveBoundingBox();
    }
}
