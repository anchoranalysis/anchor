/* (C)2020 */
package org.anchoranalysis.io.bean.object.writer;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.color.RGBColor;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.core.geometry.ReadableTuple3i;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.object.ObjectMask;
import org.anchoranalysis.image.stack.rgb.RGBStack;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class IntersectionWriter {

    // Writes only to the intersection of mask and stack (positioned at stackBBox)
    public static void writeRGBMaskIntersection(
            ObjectMask mask, RGBColor color, RGBStack stack, BoundingBox stackBBox)
            throws OperationFailedException {

        if (!stackBBox.intersection().existsWith(mask.getBoundingBox())) {
            throw new OperationFailedException(
                    String.format(
                            "The bounding-box of the mask (%s) does not intersect with the stack (%s)",
                            mask.getBoundingBox().toString(), stackBBox.toString()));
        }
        // Intersection of the mask and stackBBox
        BoundingBox intersection =
                mask.getBoundingBox()
                        .intersection()
                        .with(stackBBox)
                        .orElseThrow(
                                () ->
                                        new OperationFailedException(
                                                "Bounding boxes of mask and stack do not intersect"));

        // Let's make the intersection relative to the stack
        writeOnEachSlice(
                stack,
                color,
                intersection.shiftBackBy(stackBBox.cornerMin()),
                mask.mapBoundingBox(bbox -> bbox.shiftBackBy(stackBBox.cornerMin())));
    }

    private static void writeOnEachSlice(
            RGBStack stack, RGBColor color, BoundingBox intersection, ObjectMask mask) {

        ReadableTuple3i maxGlobal = intersection.calcCornerMax();
        Point3i pointGlobal = new Point3i();

        for (pointGlobal.setZ(intersection.cornerMin().getZ());
                pointGlobal.getZ() <= maxGlobal.getZ();
                pointGlobal.incrementZ()) {
            int relZ = pointGlobal.getZ() - mask.getBoundingBox().cornerMin().getZ();
            stack.writeRGBMaskToSlice(mask, intersection, color, pointGlobal, relZ, maxGlobal);
        }
    }
}
