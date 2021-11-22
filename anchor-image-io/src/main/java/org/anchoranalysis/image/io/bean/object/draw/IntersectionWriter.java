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

import com.google.common.base.Preconditions;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.color.RGBColor;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.image.core.dimensions.Dimensions;
import org.anchoranalysis.image.core.stack.RGBStack;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;
import org.anchoranalysis.image.voxel.datatype.UnsignedByteVoxelType;
import org.anchoranalysis.image.voxel.object.ObjectMask;
import org.anchoranalysis.spatial.box.BoundingBox;
import org.anchoranalysis.spatial.box.Extent;
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
            writeRGBMaskToSlice(stack, object, intersection, color, pointGlobal, relZ, maxGlobal);
        }
    }

    // Only supports 8-bit
    private static void writeRGBMaskToSlice(
            RGBStack stack,
            ObjectMask object,
            BoundingBox box,
            RGBColor color,
            Point3i pointGlobal,
            int zLocal,
            ReadableTuple3i maxGlobal) {
        Preconditions.checkArgument(pointGlobal.z() >= 0);
        Preconditions.checkArgument(stack.allChannelsHaveType(UnsignedByteVoxelType.INSTANCE));

        byte objectMaskOn = object.binaryValuesByte().getOn();

        UnsignedByteBuffer inArr = object.sliceBufferLocal(zLocal);

        UnsignedByteBuffer red = stack.sliceBuffer(0, pointGlobal.z());
        UnsignedByteBuffer green = stack.sliceBuffer(1, pointGlobal.z());
        UnsignedByteBuffer blue = stack.sliceBuffer(2, pointGlobal.z());

        Extent extentMask = object.boundingBox().extent();

        for (pointGlobal.setY(box.cornerMin().y());
                pointGlobal.y() <= maxGlobal.y();
                pointGlobal.incrementY()) {

            for (pointGlobal.setX(box.cornerMin().x());
                    pointGlobal.x() <= maxGlobal.x();
                    pointGlobal.incrementX()) {

                int objectMaskOffset =
                        extentMask.offset(
                                pointGlobal.x() - object.boundingBox().cornerMin().x(),
                                pointGlobal.y() - object.boundingBox().cornerMin().y());

                if (inArr.getRaw(objectMaskOffset) == objectMaskOn) {
                    writeRGBColorToByteArray(
                            color, pointGlobal, stack.getChannel(0).dimensions(), red, blue, green);
                }
            }
        }
    }

    private static void writeRGBColorToByteArray(
            RGBColor color,
            Point3i point,
            Dimensions dimensions,
            UnsignedByteBuffer red,
            UnsignedByteBuffer blue,
            UnsignedByteBuffer green) {
        int index = dimensions.offsetSlice(point);
        red.putUnsigned(index, color.getRed());
        green.putUnsigned(index, color.getGreen());
        blue.putUnsigned(index, color.getBlue());
    }
}
