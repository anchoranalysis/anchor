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

package org.anchoranalysis.mpp.bean.regionmap;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Utility class for managing 8-bit region membership.
 * <p>
 * This class provides methods for manipulating and checking region membership flags.
 * </p>
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RegionMembershipUtilities {

    /** Flag representing no region membership. */
    private static final byte FLAG_NO_REGION = 0;

    /**
     * Checks if a membership flag is set using an AND operation.
     *
     * @param membership the current membership flags
     * @param flag the flag to check
     * @return true if the flag is set, false otherwise
     */
    public static boolean isMemberFlagAnd(byte membership, byte flag) {
        return (membership & flag) == flag;
    }

    /**
     * Checks if a membership flag is set using an OR operation.
     *
     * @param membership the current membership flags
     * @param flag the flag to check
     * @return true if the flag is set, false otherwise
     */
    public static boolean isMemberFlagOr(byte membership, byte flag) {
        return (membership & flag) != 0;
    }

    /**
     * Sets a membership flag.
     *
     * @param membership the current membership flags
     * @param flag the flag to set
     * @return the updated membership flags
     */
    public static byte setAsMemberFlag(byte membership, byte flag) {
        membership |= flag;
        return membership;
    }

    /**
     * Gets the flag representing no region membership.
     *
     * @return the flag for no region
     */
    public static byte flagForNoRegion() {
        return FLAG_NO_REGION;
    }

    /**
     * Gets the flag for a specific region index.
     *
     * @param index the index of the region (0-6)
     * @return the flag for the specified region
     */
    public static byte flagForRegion(int index) {
        return flagArr[index];
    }

    /**
     * Combines flags for two regions.
     *
     * @param index1 the index of the first region
     * @param index2 the index of the second region
     * @return the combined flag for both regions
     */
    public static byte flagForRegion(int index1, int index2) {
        return (byte) (flagForRegion(index1) | flagForRegion(index2));
    }

    /**
     * Combines flags for three regions.
     *
     * @param index1 the index of the first region
     * @param index2 the index of the second region
     * @param index3 the index of the third region
     * @return the combined flag for all three regions
     */
    public static byte flagForRegion(int index1, int index2, int index3) {
        return (byte) (flagForRegion(index1) | flagForRegion(index2) | flagForRegion(index3));
    }

    /** Array of flags for each region index. */
    private static byte[] flagArr =
            new byte[] {
                1 << 0, 1 << 1, 1 << 2, 1 << 3, 1 << 4, 1 << 5, 1 << 6,
            };
}