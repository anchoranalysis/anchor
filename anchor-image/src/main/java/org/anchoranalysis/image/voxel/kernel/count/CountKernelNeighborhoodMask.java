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

package org.anchoranalysis.image.voxel.kernel.count;

import java.nio.ByteBuffer;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.binary.voxel.BinaryVoxels;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.object.ObjectMask;
import org.anchoranalysis.image.voxel.kernel.LocalSlices;

/**
 * The number of touching-faces of a voxel with a neighbor, so long as the neighbor is part of an
 * object-mask
 *
 * <p>i.e. the sum of all faces of a voxel that touch the face of a voxel belonging to a neighboring
 * pixel
 *
 * @author Owen Feehan
 */
public class CountKernelNeighborhoodMask extends CountKernelNeighborhoodBase {

    private BinaryVoxels<ByteBuffer> vbRequireHigh;
    private BinaryValuesByte bvRequireHigh;
    private ObjectMask objectRequireHigh;

    private LocalSlices localSlicesRequireHigh;

    public CountKernelNeighborhoodMask(
            boolean useZ,
            BinaryValuesByte bv,
            ObjectMask objectRequireHigh,
            boolean multipleMatchesPerVoxel) {
        super(useZ, bv, multipleMatchesPerVoxel);
        this.objectRequireHigh = objectRequireHigh;
        this.vbRequireHigh = objectRequireHigh.binaryVoxels();
        this.bvRequireHigh = vbRequireHigh.getBinaryValues().createByte();
    }

    @Override
    public void notifyZChange(LocalSlices inSlices, int z) {
        super.notifyZChange(inSlices, z);
        localSlicesRequireHigh =
                new LocalSlices(
                        z + objectRequireHigh.getBoundingBox().cornerMin().getZ(),
                        3,
                        vbRequireHigh.getVoxels());
    }

    @Override
    protected boolean isNeighborVoxelAccepted(
            Point3i point, int xShift, int yShift, int zShift, Extent extent) {

        ByteBuffer inArr = localSlicesRequireHigh.getLocal(zShift);

        if (inArr == null) {
            return false;
        }

        int x1 = point.getX() + objectRequireHigh.getBoundingBox().cornerMin().getX() + xShift;

        if (!vbRequireHigh.extent().containsX(x1)) {
            return false;
        }

        int y1 = point.getY() + objectRequireHigh.getBoundingBox().cornerMin().getY() + yShift;

        if (!vbRequireHigh.extent().containsY(y1)) {
            return false;
        }

        int indexGlobal = vbRequireHigh.extent().offset(x1, y1);
        return bvRequireHigh.isOn(inArr.get(indexGlobal));
    }
}
