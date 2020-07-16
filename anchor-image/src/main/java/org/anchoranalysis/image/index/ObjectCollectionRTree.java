/* (C)2020 */
package org.anchoranalysis.image.index;

import java.util.List;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.object.ObjectCollection;
import org.anchoranalysis.image.object.ObjectMask;

/**
 * An R-Tree of bounding boxes. The index of the item in a list, determines an integer ID,
 * associated with the item in the R-Tree.
 *
 * @author Owen Feehan
 */
public class ObjectCollectionRTree {

    private BoundingBoxRTree delegate;
    private ObjectCollection objects;

    public ObjectCollectionRTree(ObjectCollection objects) {
        this.objects = objects;
        delegate = new BoundingBoxRTree(objects.size());

        for (int i = 0; i < objects.size(); i++) {
            delegate.add(i, objects.get(i).getBoundingBox());
        }
    }

    public ObjectCollection contains(Point3i point) {
        // We do an additional check to make sure the point is inside the object,
        //  as points can be inside the Bounding Box but not inside the object
        return objects.stream()
                .filterSubset(object -> object.contains(point), delegate.contains(point));
    }

    public ObjectCollection intersectsWith(ObjectMask object) {
        // We do an additional check to make sure the point is inside the object,
        //  as points can be inside the Bounding Box but not inside the object
        return objects.stream()
                .filterSubset(
                        omInd -> omInd.hasIntersectingVoxels(object),
                        delegate.intersectsWith(object.getBoundingBox()));
    }

    public ObjectCollection intersectsWith(BoundingBox bbox) {
        return objects.createSubset(delegate.intersectsWith(bbox));
    }

    public List<Integer> intersectsWithAsIndices(BoundingBox bbox) {
        return delegate.intersectsWith(bbox);
    }
}
