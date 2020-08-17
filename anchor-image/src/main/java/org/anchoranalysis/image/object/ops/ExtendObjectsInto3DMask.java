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
import org.anchoranalysis.image.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.binary.voxel.BinaryVoxels;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.object.ObjectCollection;
import org.anchoranalysis.image.object.ObjectMask;
import org.anchoranalysis.image.voxel.BoundedVoxels;
import org.anchoranalysis.image.voxel.factory.VoxelsFactory;

/**
 * Extends 2D objects as much as possible in z-dimension, while staying within a 3D binary mask.
 *
 * <p>TODO remove slices which have nothing in them from top and bottom
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExtendObjectsInto3DMask {

    public static ObjectCollection extendObjects(
            ObjectCollection objects2D, BinaryVoxels<ByteBuffer> mask3D) {
        return objects2D.stream().map(object -> extendObject(object, mask3D));
    }

    private static ObjectMask extendObject(ObjectMask object2D, BinaryVoxels<ByteBuffer> voxels3D) {
        return new ObjectMask(extendObj(object2D.boundedVoxels(), voxels3D));
    }

    private static BoundedVoxels<ByteBuffer> extendObj(
            BoundedVoxels<ByteBuffer> obj2D, BinaryVoxels<ByteBuffer> mask3D) {

        BoundingBox newBBox = createBoundingBoxForAllZ(obj2D.boundingBox(), mask3D.extent().z());

        BoundedVoxels<ByteBuffer> newMask = new BoundedVoxels<>(newBBox, VoxelsFactory.getByte());

        ReadableTuple3i max = newBBox.calculateCornerMax();
        Point3i point = new Point3i();

        BinaryValuesByte bv = mask3D.binaryValues().createByte();

        ByteBuffer bufferIn2D = obj2D.voxels().sliceBuffer(0);

        for (point.setZ(0); point.z() <= max.z(); point.incrementZ()) {

            ByteBuffer bufferMask3D = mask3D.voxels().sliceBuffer(point.z());
            ByteBuffer bufferOut3D = newMask.voxels().sliceBuffer(point.z());

            int ind = 0;

            for (point.setY(newBBox.cornerMin().y()); point.y() <= max.y(); point.incrementY()) {

                for (point.setX(newBBox.cornerMin().x());
                        point.x() <= max.x();
                        point.incrementX(), ind++) {

                    if (bufferIn2D.get(ind) != bv.getOnByte()) {
                        continue;
                    }

                    int indexGlobal = mask3D.extent().offset(point.x(), point.y());
                    bufferOut3D.put(
                            ind,
                            bufferMask3D.get(indexGlobal) == bv.getOnByte()
                                    ? bv.getOnByte()
                                    : bv.getOffByte());
                }
            }
        }
        return newMask;
    }

    private static BoundingBox createBoundingBoxForAllZ(BoundingBox exst, int z) {
        Point3i cornerMin = copyPointChangeZ(exst.cornerMin(), 0);
        Extent e = copyExtentChangeZ(exst.extent(), z);

        return new BoundingBox(cornerMin, e);
    }

    private static Point3i copyPointChangeZ(ReadableTuple3i in, int z) {
        Point3i out = new Point3i(in);
        out.setZ(z);
        return out;
    }

    private static Extent copyExtentChangeZ(Extent in, int z) {
        return new Extent(in.x(), in.y(), z);
    }
}
