/* (C)2020 */
package org.anchoranalysis.anchor.mpp.bean.regionmap;

public class RegionMembershipOr extends RegionMembership {

    @Override
    public boolean isMemberFlag(byte membership, byte flag) {
        return RegionMembershipUtilities.isMemberFlagOr(membership, flag);
    }
}
