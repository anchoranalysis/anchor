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

package org.anchoranalysis.image.io.objects;

import ch.systemsx.cisd.base.mdarray.MDByteArray;
import ch.systemsx.cisd.hdf5.HDF5IntStorageFeatures;
import ch.systemsx.cisd.hdf5.IHDF5Writer;
import java.nio.ByteBuffer;
import java.util.function.ToIntFunction;
import lombok.AllArgsConstructor;
import org.anchoranalysis.core.geometry.ReadableTuple3i;
import org.anchoranalysis.image.binary.voxel.BinaryVoxelBox;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.object.ObjectMask;

/**
 * Writes an ObjectMask to a path within a HDF5 file
 *
 * <p>The mask is written as a 3D array of 255 and 0 bytes
 *
 * <p>The corner-position of the bounding box is added as attributes: x, y, z
 *
 * @author Owen Feehan
 */
@AllArgsConstructor
class ObjectMaskHDF5Writer {

    /** The object-mask to write */
    private final ObjectMask object;

    /** The path in the HDF5 file to write it to */
    private final String pathHDF5;

    /** An opened-writer for the HDF5 file */
    private final IHDF5Writer writer;

    /** Whether to use compression or not */
    private final boolean compression;

    private HDF5IntStorageFeatures compressionLevel() {
        if (compression) {
            return HDF5IntStorageFeatures.INT_DEFLATE_UNSIGNED;
        } else {
            return HDF5IntStorageFeatures.INT_NO_COMPRESSION_UNSIGNED;
        }
    }

    public void apply() {

        writer.uint8()
                .writeMDArray(pathHDF5, byteArray(object.binaryVoxelBox()), compressionLevel());

        addCorner();
    }

    private void addCorner() {
        addAttribute(HDF5PathHelper.EXTENT_X, ReadableTuple3i::getX);
        addAttribute(HDF5PathHelper.EXTENT_Y, ReadableTuple3i::getY);
        addAttribute(HDF5PathHelper.EXTENT_Z, ReadableTuple3i::getZ);
    }

    private void addAttribute(String attrName, ToIntFunction<ReadableTuple3i> extrVal) {

        Integer crnrVal = extrVal.applyAsInt(object.getBoundingBox().cornerMin());
        writer.uint32().setAttr(pathHDF5, attrName, crnrVal.intValue());
    }

    private static MDByteArray byteArray(BinaryVoxelBox<ByteBuffer> bvb) {

        Extent extent = bvb.extent();

        MDByteArray md = new MDByteArray(dimensionsFromExtent(extent));

        for (int z = 0; z < extent.getZ(); z++) {

            ByteBuffer bb = bvb.getPixelsForPlane(z).buffer();

            for (int y = 0; y < extent.getY(); y++) {
                for (int x = 0; x < extent.getX(); x++) {
                    md.set(bb.get(extent.offset(x, y)), x, y, z);
                }
            }
        }
        return md;
    }

    private static int[] dimensionsFromExtent(Extent extent) {
        return new int[] {extent.getX(), extent.getY(), extent.getZ()};
    }
}
