/*-
 * #%L
 * anchor-mpp
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

package org.anchoranalysis.mpp.overlap;

import lombok.AllArgsConstructor;
import org.anchoranalysis.image.convert.UnsignedByteBuffer;
import org.anchoranalysis.image.voxel.BoundedVoxels;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.iterator.intersecting.CountVoxelsIntersectingBounded;

/**
 * Counts the number of intersecting-pixels where bytes are encoded as region memberships
 *
 * @author Owen Feehan
 */
@AllArgsConstructor
class CountIntersectingVoxels {

    private final PredicateRegionMembership predicate;

    public CountIntersectingVoxels(byte regionMembershipFlag) {
        predicate = new PredicateRegionMembership(regionMembershipFlag);
    }

    public int count(
            BoundedVoxels<UnsignedByteBuffer> voxels1, BoundedVoxels<UnsignedByteBuffer> voxels2) {
        return CountVoxelsIntersectingBounded.countByte(voxels1, voxels2, predicate);
    }

    public int countMasked(
            BoundedVoxels<UnsignedByteBuffer> voxels1,
            BoundedVoxels<UnsignedByteBuffer> voxels2,
            Voxels<UnsignedByteBuffer> maskGlobal,
            byte onMaskGlobal) {
        return CountVoxelsIntersectingBounded.countByteMasked(
                maskGlobal, onMaskGlobal, voxels1, voxels2, predicate);
    }
}