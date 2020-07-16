/* (C)2020 */
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
