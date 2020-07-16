/* (C)2020 */
package org.anchoranalysis.anchor.mpp.bean.regionmap;

public class RegionMembershipAnd extends RegionMembership {

    public RegionMembershipAnd() {
        super();
    }

    public RegionMembershipAnd(int index) {
        super(index);
    }

    @Override
    public boolean isMemberFlag(byte membership, byte flag) {
        return RegionMembershipUtilities.isMemberFlagAnd(membership, flag);
    }
}
