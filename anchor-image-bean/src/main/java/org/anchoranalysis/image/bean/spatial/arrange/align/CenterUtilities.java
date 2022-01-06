package org.anchoranalysis.image.bean.spatial.arrange.align;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.spatial.box.BoundingBox;
import org.anchoranalysis.spatial.box.Extent;
import org.anchoranalysis.spatial.point.Point3i;
import org.anchoranalysis.spatial.point.ReadableTuple3i;

/**
 * Utilities to help center a {@link BoundingBox} inside another {@link BoundingBox}.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class CenterUtilities {

    public static BoundingBox centerSmaller(
            ReadableTuple3i smallerCornerMin, Extent smallerExtent, BoundingBox larger) {
        Point3i relativeCorner = relativeCornerToCell(larger.extent(), smallerExtent);
        return shiftBoundingBoxCorner(
                smallerCornerMin, larger.cornerMin(), relativeCorner, smallerExtent);
    }

    /**
     * Immutably adds {@code shift1} and {@code shift2} to the minimum corner of {@code box}, but
     * preserves the {@link Extent}.
     */
    private static BoundingBox shiftBoundingBoxCorner(
            ReadableTuple3i cornerMin,
            ReadableTuple3i shift1,
            ReadableTuple3i shift2,
            Extent extent) {
        Point3i cornerMinShifted = Point3i.immutableAdd(cornerMin, shift1, shift2);
        return new BoundingBox(cornerMinShifted, extent);
    }

    /**
     * The minimum corner of {@code smaller} relative to {@code larger}, so as to center it across
     * all dimensions.
     */
    private static Point3i relativeCornerToCell(Extent larger, Extent smaller) {
        Point3i relativeCorner = Point3i.immutableSubtract(larger.asTuple(), smaller.asTuple());
        relativeCorner.divideBy(2); // To center
        return relativeCorner;
    }
}
