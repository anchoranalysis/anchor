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

import org.anchoranalysis.image.voxel.binary.BinaryVoxels;
import org.anchoranalysis.image.voxel.object.ObjectMask;
import org.anchoranalysis.spatial.point.Point3i;

/**
 * The number of touching-faces of a voxel with a neighbor, so long as the neighbor is part of an
 * {@link ObjectMask}.
 *
 * <p>i.e. the sum of all faces of a voxel that touch the face of a voxel belonging to a neighboring
 * pixel
 *
 * @author Owen Feehan
 */
public class CountKernelNeighborhoodMask extends CountKernel {

    private final ObjectMask objectRequireHigh;

    /**
     * Create with object-mask.
     * 
     * @param objectRequireHigh an object with coordinates relative to the {@link BinaryVoxels} the kernel is being applied on,
     *  only on whose <i>on</i> voxels is a neighboring voxel considered.
     */
    public CountKernelNeighborhoodMask(ObjectMask objectRequireHigh) {
        this.objectRequireHigh = objectRequireHigh;
    }

    @Override
    protected boolean doesNeighborVoxelQualify(
            Point3i point) {
        return objectRequireHigh.contains(point);
    }
}
