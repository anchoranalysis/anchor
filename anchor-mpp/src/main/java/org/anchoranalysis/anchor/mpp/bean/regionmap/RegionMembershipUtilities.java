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

package org.anchoranalysis.anchor.mpp.bean.regionmap;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

// 8-bit region membership
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RegionMembershipUtilities {

    private static final byte FLAG_NO_REGION = 0;

    public static boolean isMemberFlagAnd(byte membership, byte flag) {
        return (membership & flag) == flag;
    }

    public static boolean isMemberFlagOr(byte membership, byte flag) {
        return (membership & flag) != 0;
    }

    public static byte setAsMemberFlag(byte membership, byte flag) {
        membership |= flag;
        return membership;
    }

    public static byte flagForNoRegion() {
        return FLAG_NO_REGION;
    }

    public static byte flagForRegion(int index) {
        return flagArr[index];
    }

    /** Combines two regions */
    public static byte flagForRegion(int index1, int index2) {
        return (byte) (flagForRegion(index1) | flagForRegion(index2));
    }

    /** Combines three regions */
    public static byte flagForRegion(int index1, int index2, int index3) {
        return (byte) (flagForRegion(index1) | flagForRegion(index2) | flagForRegion(index3));
    }

    private static byte[] flagArr =
            new byte[] {
                1 << 0, 1 << 1, 1 << 2, 1 << 3, 1 << 4, 1 << 5, 1 << 6,
            };
}
