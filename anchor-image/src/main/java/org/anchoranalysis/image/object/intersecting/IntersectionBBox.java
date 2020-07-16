/* (C)2020 */
package org.anchoranalysis.image.object.intersecting;

import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.Extent;

/**
 * A bounding box where intersection occurs
 *
 * <p>We deliberately avoid getters and setters for code optimization reasons as the particular
 * routines are computationally sensitive
 *
 * <p>(we make inlining as easy as possible for the JVM)
 *
 * @author Owen Feehan
 */
public class IntersectionBBox {

    private Dimension x;
    private Dimension y;
    private Dimension z;
    private Extent e1; // Extent of source bbox
    private Extent e2; // Extent of other bbox

    public static class Dimension {

        private int min; // Min point of intersection bbox
        private int max; // Max point of intersection bbox
        private int rel; // Relative position other to src

        public Dimension(int min, int max, int rel) {
            super();
            this.min = min;
            this.max = max;
            this.rel = rel;
        }

        public int min() {
            return min;
        }

        public int max() {
            return max;
        }

        public int rel() {
            return rel;
        }
    }

    public static IntersectionBBox create(
            BoundingBox bboxSrc, BoundingBox bboxOther, BoundingBox bboxIntersect) {

        Point3i relPosSrc = bboxIntersect.relPosTo(bboxSrc);

        Point3i relPosTrgtToSrc =
                Point3i.immutableSubtract(bboxSrc.cornerMin(), bboxOther.cornerMin());

        Point3i relPosSrcMax = Point3i.immutableAdd(relPosSrc, bboxIntersect.extent().asTuple());

        return new IntersectionBBox(
                relPosSrc, relPosSrcMax, relPosTrgtToSrc, bboxSrc.extent(), bboxOther.extent());
    }

    private IntersectionBBox(
            Point3i pointMin, Point3i pointMax, Point3i relPos, Extent eSrc, Extent eOther) {
        x = new Dimension(pointMin.getX(), pointMax.getX(), relPos.getX());
        y = new Dimension(pointMin.getY(), pointMax.getY(), relPos.getY());
        z = new Dimension(pointMin.getZ(), pointMax.getZ(), relPos.getZ());
        this.e1 = eSrc;
        this.e2 = eOther;
    }

    public Dimension x() {
        return x;
    }

    public Dimension y() {
        return y;
    }

    public Dimension z() {
        return z;
    }

    public Extent e1() {
        return e1;
    }

    public Extent e2() {
        return e2;
    }
}
