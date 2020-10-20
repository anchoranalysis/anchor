/*-
 * #%L
 * anchor-io
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

package org.anchoranalysis.image.io.bean.object.draw;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.color.RGBColor;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.image.core.stack.rgb.RGBStack;
import org.anchoranalysis.image.voxel.object.ObjectMask;
import org.anchoranalysis.spatial.box.BoundingBox;
import org.anchoranalysis.spatial.point.Point3i;
import org.anchoranalysis.spatial.point.ReadableTuple3i;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class IntersectionWriter {

    /**
     * Writes only to the intersection of an object-mask and stack (positioned at {@code stackBox})
     */
    public static void writeRGBMaskIntersection(
            ObjectMask object, RGBColor color, RGBStack stack, BoundingBox stackBox)
            throws OperationFailedException {

        if (!stackBox.intersection().existsWith(object.boundingBox())) {
            throw new OperationFailedException(
                    String.format(
                            "The bounding-box of the object-mask (%s) does not intersect with the stack (%s)",
                            object.boundingBox().toString(), stackBox.toString()));
        }
        // Intersection of the object-mask and stackBBox
        BoundingBox intersection =
                object.boundingBox()
                        .intersection()
                        .with(stackBox)
                        .orElseThrow(
                                () ->
                                        new OperationFailedException(
                                                "Bounding boxes of object and stack do not intersect"));

        // Let's make the intersection relative to the stack
        writeOnEachSlice(
                stack,
                color,
                intersection.shiftBackBy(stackBox.cornerMin()),
                object.shiftBackBy(stackBox.cornerMin()));
    }

    private static void writeOnEachSlice(
            RGBStack stack, RGBColor color, BoundingBox intersection, ObjectMask object) {

        ReadableTuple3i maxGlobal = intersection.calculateCornerMax();
        Point3i pointGlobal = new Point3i();

        for (pointGlobal.setZ(intersection.cornerMin().z());
                pointGlobal.z() <= maxGlobal.z();
                pointGlobal.incrementZ()) {
            int relZ = pointGlobal.z() - object.boundingBox().cornerMin().z();
            stack.writeRGBMaskToSlice(object, intersection, color, pointGlobal, relZ, maxGlobal);
        }
    }
}
