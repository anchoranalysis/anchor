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

package org.anchoranalysis.image.points;

import java.nio.ByteBuffer;
import java.util.function.Consumer;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.core.geometry.ReadableTuple3i;
import org.anchoranalysis.image.binary.mask.Mask;
import org.anchoranalysis.image.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.voxel.Voxels;

/**
 * Consumes each certain voxels on certain slices as points
 * <p>
 * Slices are iterated in a certain order.
 * <p>
 * The voxels must be:
 * <ul>
 * <li>Be ON on a mask
 * <li>Lie inside a bounding-box
 * <li>Exist on certain slices
 * </ul>
 * 
 * @author Owen Feehan
 *
 */
class ConsumePointsFromMaskSliced {

    private final int skipAfterSuccessiveEmptySlices;
    private final ReadableTuple3i cornerMin;
    private final ReadableTuple3i cornerMax;
    private final Voxels<ByteBuffer> voxels;
    private final BinaryValuesByte bvb;
    private final int startZ;
    private final Consumer<Point3i> consumer;
    private Extent extent;

    // Stays as -1 until we reach a non-empty slice
    private int successiveEmptySlices = -1;

    public ConsumePointsFromMaskSliced(int skipAfterSuccessiveEmptySlices,
            BoundingBox box, Mask mask, int startZ, Consumer<Point3i> consumer) {
        super();
        this.skipAfterSuccessiveEmptySlices = skipAfterSuccessiveEmptySlices;
        
        this.cornerMin = box.cornerMin();
        this.cornerMax = box.calcCornerMax();
        this.voxels = mask.channel().voxels().asByte();
        this.bvb = mask.binaryValues().createByte();
        this.startZ = startZ;
        this.consumer = consumer;
        this.extent = voxels.extent();
    }    
    
    public void firstHalf() {
        for (int z = startZ; z <= cornerMax.z(); z++) {

            ByteBuffer bb = voxels.sliceBuffer(z);

            if (!addPointsFromSlice(bb, z)) {
                successiveEmptySlices = 0;

                // We don't increase the counter until we've been inside a non-empty slice
            } else if (successiveEmptySlices != -1) {
                successiveEmptySlices++;
                if (successiveEmptySlices >= skipAfterSuccessiveEmptySlices) {
                    break;
                }
            }
        }
    }

    public void secondHalf() {
        for (int z = (startZ - 1); z >= cornerMin.z(); z--) {

            ByteBuffer bb = voxels.sliceBuffer(z);

            if (!addPointsFromSlice(bb, z)) {
                successiveEmptySlices = 0;

                // We don't increase the counter until we've been inside a non-empty slice
            } else if (successiveEmptySlices != -1) {
                successiveEmptySlices++;
                if (successiveEmptySlices >= skipAfterSuccessiveEmptySlices) {
                    break;
                }
            }
        }
    }

    private boolean addPointsFromSlice(ByteBuffer bb, int z) {

        boolean addedToSlice = false;
        for (int y = cornerMin.y(); y <= cornerMax.y(); y++) {
            for (int x = cornerMin.x(); x <= cornerMax.x(); x++) {

                int offset = extent.offset(x, y);
                if (bb.get(offset) == bvb.getOnByte()) {
                    addedToSlice = true;
                    consumer.accept(new Point3i(x, y, z));
                }
            }
        }
        return addedToSlice;
    }
}
