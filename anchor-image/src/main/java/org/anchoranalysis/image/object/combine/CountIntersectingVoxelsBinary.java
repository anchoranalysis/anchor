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

package org.anchoranalysis.image.object.combine;

import org.anchoranalysis.image.convert.UnsignedByteBuffer;
import org.anchoranalysis.image.binary.values.BinaryValuesByte;

/**
 * Counts the number of intersecting voxels where each buffer is encoded as Binary-Values
 *
 * @author Owen Feehan
 */
public class CountIntersectingVoxelsBinary extends CountIntersectingVoxels {

    private byte byteOn1;
    private byte byteOn2;

    public CountIntersectingVoxelsBinary(BinaryValuesByte bvb1, BinaryValuesByte bvb2) {
        super();
        this.byteOn1 = bvb1.getOnByte();
        this.byteOn2 = bvb2.getOnByte();
    }

    @Override
    protected int countIntersectingVoxels(
            UnsignedByteBuffer buffer1, UnsignedByteBuffer buffer2, IntersectionBoundingBox box) {

        int cnt = 0;
        for (int y = box.y().min(); y < box.y().max(); y++) {
            int yOther = y + box.y().rel();

            for (int x = box.x().min(); x < box.x().max(); x++) {
                int xOther = x + box.x().rel();

                byte posCheck = buffer1.getRaw(box.e1().offset(x, y));
                byte posCheckOther = buffer2.getRaw(box.e2().offset(xOther, yOther));

                if (posCheck == byteOn1 && posCheckOther == byteOn2) {
                    cnt++;
                }
            }
        }
        return cnt;
    }
}
