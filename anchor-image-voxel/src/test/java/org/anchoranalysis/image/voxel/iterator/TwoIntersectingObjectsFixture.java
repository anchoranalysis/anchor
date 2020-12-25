package org.anchoranalysis.image.voxel.iterator;

import org.anchoranalysis.image.voxel.object.ObjectMask;
import org.anchoranalysis.image.voxel.object.ObjectMaskFixture;
import org.anchoranalysis.spatial.point.Point2i;
import lombok.Getter;

/**
 * Two object-masks that intersect.
 * 
 * @author Owen Feehan
 *
 */
public class TwoIntersectingObjectsFixture {

    private static final Point2i CORNER = new Point2i(10, 15);
    
    @Getter private final ObjectMask first;
    @Getter private final ObjectMask second;
    
    public TwoIntersectingObjectsFixture(int shift, boolean use3D) {
        ObjectMaskFixture fixture = new ObjectMaskFixture(false, use3D);
        first = fixture.filledMask(CORNER);
        second = fixture.filledMask(cornerSecondObject(shift)); 
    }
    
    /**
     * Creates a corner for a second object that is adjacent to the side of the first object, with
     * perhaps a shift
     *
     * @param shift how much to increase the corner of the second object
     */
    private Point2i cornerSecondObject(int shift) {
        Point2i extent = new Point2i(ObjectMaskFixture.WIDTH, ObjectMaskFixture.HEIGHT);
        extent.incrementX(shift);
        extent.incrementY(shift);
        return Point2i.immutableAdd(CORNER, extent);
    }
}
