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

import java.nio.ByteBuffer;
import lombok.AllArgsConstructor;
import org.anchoranalysis.image.object.combine.CountIntersectingVoxels;
import org.anchoranalysis.image.object.combine.IntersectionBoundingBox;
import org.anchoranalysis.mpp.bean.regionmap.RegionMembershipUtilities;

/**
 * Counts the number of intersecting pixels where each buffer is encoded as region-membership
 *
 * @author Owen Feehan
 */
@AllArgsConstructor
class CountIntersectingVoxelsRegionMembership extends CountIntersectingVoxels {

    private final byte regionMembershipFlag;

    @Override
    protected int countIntersectingVoxels(
            ByteBuffer buffer1, ByteBuffer buffer2, IntersectionBoundingBox box) {

        int cnt = 0;
        for (int y = box.y().min(); y < box.y().max(); y++) {
            int yOther = y + box.y().rel();

            for (int x = box.x().min(); x < box.x().max(); x++) {
                int xOther = x + box.x().rel();

                byte posCheck = buffer1.get(box.e1().offset(x, y));
                byte posCheckOther = buffer2.get(box.e2().offset(xOther, yOther));

                if (isPixelInRegion(posCheck) && isPixelInRegion(posCheckOther)) {
                    cnt++;
                }
            }
        }
        return cnt;
    }

    private boolean isPixelInRegion(byte pixelVal) {
        return RegionMembershipUtilities.isMemberFlagAnd(pixelVal, regionMembershipFlag);
    }
}
