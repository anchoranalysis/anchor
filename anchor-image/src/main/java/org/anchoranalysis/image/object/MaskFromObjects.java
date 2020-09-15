/*-
 * #%L
 * anchor-image
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

package org.anchoranalysis.image.object;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.core.geometry.ReadableTuple3i;
import org.anchoranalysis.image.binary.mask.Mask;
import org.anchoranalysis.image.binary.mask.MaskFactory;
import org.anchoranalysis.image.binary.values.BinaryValues;
import org.anchoranalysis.image.convert.UnsignedByteBuffer;
import org.anchoranalysis.image.extent.Dimensions;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.extent.box.BoundingBox;
import org.anchoranalysis.image.voxel.Voxels;

/**
 * Creates a mask from one or more objects.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MaskFromObjects {

    /** We look for space IN objects, and create channel to display it */
    public static Mask createFromObjects(
            ObjectCollection objects, Dimensions dimensions, BinaryValues outValues) {
        return createFromObjectsWithValues(
                objects,
                dimensions,
                outValues,
                outValues.getOffInt(),
                outValues.createByte().getOnByte());
    }

    /** We look for space NOT in the objects, and create channel to display it */
    public static Mask createFromNotObjects(
            ObjectCollection objects, Dimensions dimensions, BinaryValues outValues) {
        return createFromObjectsWithValues(
                objects,
                dimensions,
                outValues,
                outValues.getOnInt(),
                outValues.createByte().getOffByte());
    }

    /**
     * Creates a mask where the voxels corresponding to any objects in a collection are assigned
     * {@code valueObjects} and other voxels are assigned {@code valueNotObjects}
     *
     * @param objects objects which determine ON and OFF values for the mask
     * @param dimensions the mask's dimensions
     * @param binaryValuesToOutput what defines ON and OFF in the output channel
     * @param valueNotObjects value to assign to voxels that <b>do not</b> correspond to an ON voxel
     *     in any of {@code objects}
     * @param valueObjects value to assign to voxels that <b>do</b> correspond to an ON voxel in any
     *     of {@code objects}
     * @return a newly created mask with newly created buffers
     */
    private static Mask createFromObjectsWithValues(
            ObjectCollection objects,
            Dimensions dimensions,
            BinaryValues binaryValuesToOutput,
            int valueNotObjects,
            byte valueObjects) {

        Mask out = MaskFactory.createMaskOff(dimensions, binaryValuesToOutput);
        if (valueNotObjects != 0) {
            out.voxels().assignValue(valueNotObjects).toAll();
        }

        assignValueToObjects(out.voxels(), objects, valueObjects);

        return out;
    }

    private static void assignValueToObjects(
            Voxels<UnsignedByteBuffer> voxels, ObjectCollection objects, byte valueToAssign) {
        objects.forEach(object -> writeObjectOntoVoxels(object, voxels, valueToAssign));
    }

    private static void writeObjectOntoVoxels(
            ObjectMask object, Voxels<UnsignedByteBuffer> voxelsOut, byte outValByte) {

        BoundingBox box = object.boundingBox();

        ReadableTuple3i maxGlobal = box.calculateCornerMax();
        Point3i pointGlobal = new Point3i();
        Point3i pointLocal = new Point3i();

        byte matchValue = object.binaryValuesByte().getOnByte();

        pointLocal.setZ(0);
        for (pointGlobal.setZ(box.cornerMin().z());
                pointGlobal.z() <= maxGlobal.z();
                pointGlobal.incrementZ(), pointLocal.incrementZ()) {

            UnsignedByteBuffer maskIn = object.sliceBufferLocal(pointLocal.z());

            UnsignedByteBuffer pixelsOut = voxelsOut.sliceBuffer(pointGlobal.z());
            writeToBufferMasked(
                    maskIn,
                    pixelsOut,
                    voxelsOut.extent(),
                    box.cornerMin(),
                    pointGlobal,
                    maxGlobal,
                    matchValue,
                    outValByte);
        }
    }

    private static void writeToBufferMasked(
            UnsignedByteBuffer maskIn,
            UnsignedByteBuffer pixelsOut,
            Extent extentOut,
            ReadableTuple3i cornerMin,
            Point3i pointGlobal,
            ReadableTuple3i maxGlobal,
            byte matchValue,
            byte outValByte) {

        for (pointGlobal.setY(cornerMin.y());
                pointGlobal.y() <= maxGlobal.y();
                pointGlobal.incrementY()) {

            for (pointGlobal.setX(cornerMin.x());
                    pointGlobal.x() <= maxGlobal.x();
                    pointGlobal.incrementX()) {

                if (maskIn.getRaw() == matchValue) {
                    pixelsOut.putRaw(extentOut.offsetSlice(pointGlobal), outValByte);
                }
            }
        }
    }
}
