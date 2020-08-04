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

package org.anchoranalysis.image.object.ops;

import java.nio.ByteBuffer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.core.geometry.ReadableTuple3i;
import org.anchoranalysis.image.binary.mask.Mask;
import org.anchoranalysis.image.binary.values.BinaryValues;
import org.anchoranalysis.image.channel.Channel;
import org.anchoranalysis.image.channel.factory.ChannelFactory;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.image.object.ObjectCollection;
import org.anchoranalysis.image.object.ObjectMask;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.datatype.VoxelDataTypeUnsignedByte;

/**
 * Creates a mask from one or more objects
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MaskFromObjects {

    /** We look for space IN objects, and create channel to display it */
    public static Mask createFromObjects(
            ObjectCollection objects, ImageDimensions dimensions, BinaryValues outVal) {
        return createChannelObjectCollectionHelper(
                objects, dimensions, outVal, outVal.getOffInt(), outVal.createByte().getOnByte());
    }

    /** We look for space NOT in the objects, and create channel to display it */
    public static Mask createFromNotObjects(
            ObjectCollection objects, ImageDimensions dimensions, BinaryValues outVal) {
        return createChannelObjectCollectionHelper(
                objects, dimensions, outVal, outVal.getOnInt(), outVal.createByte().getOffByte());
    }

    // We look for the values that are NOT on the masks
    private static Mask createChannelObjectCollectionHelper(
            ObjectCollection objects,
            ImageDimensions dimensions,
            BinaryValues outVal,
            int initialState,
            byte objectState) {

        Channel chnlNew =
                ChannelFactory.instance()
                        .createEmptyInitialised(dimensions, VoxelDataTypeUnsignedByte.INSTANCE);
        Voxels<ByteBuffer> voxelsNew = chnlNew.voxels().asByte();

        if (outVal.getOnInt() != 0) {
            voxelsNew.setAllPixelsTo(initialState);
        }

        writeChannelObjectCollection(voxelsNew, objects, objectState);

        return new Mask(chnlNew, outVal);
    }

    // nullVal is assumed to be 0
    private static void writeChannelObjectCollection(
            Voxels<ByteBuffer> voxels, ObjectCollection objects, byte outVal) {

        objects.forEach(object->
            writeObjectOntoVoxels(object, voxels, outVal)
        );
    }

    private static void writeObjectOntoVoxels(
            ObjectMask object, Voxels<ByteBuffer> voxelsOut, byte outValByte) {

        BoundingBox bbox = object.boundingBox();

        ReadableTuple3i maxGlobal = bbox.calcCornerMax();
        Point3i pointGlobal = new Point3i();
        Point3i pointLocal = new Point3i();

        byte matchValue = object.binaryValuesByte().getOnByte();

        pointLocal.setZ(0);
        for (pointGlobal.setZ(bbox.cornerMin().z());
                pointGlobal.z() <= maxGlobal.z();
                pointGlobal.incrementZ(), pointLocal.incrementZ()) {

            ByteBuffer maskIn = object.voxels().slice(pointLocal.z()).buffer();

            ByteBuffer pixelsOut =
                    voxelsOut.getPlaneAccess().getPixelsForPlane(pointGlobal.z()).buffer();
            writeToBufferMasked(
                    maskIn,
                    pixelsOut,
                    voxelsOut.extent(),
                    bbox.cornerMin(),
                    pointGlobal,
                    maxGlobal,
                    matchValue,
                    outValByte);
        }
    }

    private static void writeToBufferMasked(
            ByteBuffer maskIn,
            ByteBuffer pixelsOut,
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

                if (maskIn.get() != matchValue) {
                    continue;
                }

                int indexGlobal = extentOut.offset(pointGlobal.x(), pointGlobal.y());
                pixelsOut.put(indexGlobal, outValByte);
            }
        }
    }
}
