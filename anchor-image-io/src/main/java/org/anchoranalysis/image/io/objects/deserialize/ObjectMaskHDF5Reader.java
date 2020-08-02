/*-
 * #%L
 * anchor-image-io
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

package org.anchoranalysis.image.io.objects.deserialize;

import ch.systemsx.cisd.base.mdarray.MDByteArray;
import ch.systemsx.cisd.hdf5.IHDF5IntReader;
import ch.systemsx.cisd.hdf5.IHDF5Reader;
import java.nio.ByteBuffer;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.object.ObjectMask;
import org.anchoranalysis.image.voxel.box.VoxelBox;
import org.anchoranalysis.image.voxel.box.factory.VoxelBoxFactory;

class ObjectMaskHDF5Reader {

    public ObjectMask apply(IHDF5Reader reader, String datasetPath) {

        VoxelBox<ByteBuffer> voxelBox = createVoxelBox(reader.uint8().readMDArray(datasetPath));

        BoundingBox bbox =
                new BoundingBox(cornerPoint(reader.uint32(), datasetPath), voxelBox.extent());

        return new ObjectMask(bbox, voxelBox);
    }

    public static int extractIntAttr(IHDF5IntReader reader, String path, String attr) {
        return reader.getAttr(path, attr);
    }

    private static Extent extractExtent(MDByteArray mdb) {
        int[] dims = mdb.dimensions();
        return new Extent(dims[0], dims[1], dims[2]);
    }

    /**
     * This approach is a bit efficient as we end up making two memory allocations ( the first in
     * the JHDF5 library, the second for the VoxelBox)
     *
     * <p>However our code, currently uses a memory model of a byte-array per slice. There doesn't
     * seem to be a convenient way to get the same structure back from the MDByteArray.
     *
     * <p>So this is the easiest approach for implementation.
     *
     * @param mdb
     * @return
     */
    private VoxelBox<ByteBuffer> createVoxelBox(MDByteArray mdb) {
        Extent e = extractExtent(mdb);
        VoxelBox<ByteBuffer> vb = VoxelBoxFactory.getByte().create(e);

        for (int z = 0; z < e.getZ(); z++) {

            ByteBuffer bb = vb.getPixelsForPlane(z).buffer();

            for (int y = 0; y < e.getY(); y++) {
                for (int x = 0; x < e.getX(); x++) {
                    bb.put(e.offset(x, y), mdb.get(x, y, z));
                }
            }
        }

        return vb;
    }

    private static Point3i cornerPoint(IHDF5IntReader reader, String path) {
        return new Point3i(
                extractIntAttr(reader, path, "x"),
                extractIntAttr(reader, path, "y"),
                extractIntAttr(reader, path, "z"));
    }
}