/* (C)2020 */
package org.anchoranalysis.anchor.mpp.bean.regionmap;

// A region membership with its flag stored as a byte
public class RegionMembershipWithFlags {

    private byte flags;
    private RegionMembership regionMembership;

    // We need to clarify that exactly this parameter means!  Is it for maximum bounding box?
    private int regionID;

    public RegionMembershipWithFlags(RegionMembership regionMembership, int regionID) {
        super();
        this.regionMembership = regionMembership;
        this.flags = regionMembership.flags();
        this.regionID = regionID;
    }

    public boolean isMemberFlag(byte membership) {
        return regionMembership.isMemberFlag(membership, flags);
    }

    public int getRegionID() {
        return regionID;
    }

    @Override
    public String toString() {
        return Byte.toString(flags);
    }

    public RegionMembership getRegionMembership() {
        return regionMembership;
    }
}
