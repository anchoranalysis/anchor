/* (C)2020 */
package org.anchoranalysis.anchor.mpp.bean.regionmap;

import java.util.ArrayList;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.bean.annotation.BeanField;

/**
 * Maps integer IDs to sub-regions in the map
 *
 * <p>See org.anchoranalysis.plugin.image.feature.bean.stack.object.AsObjectMask for an example of
 * where equals is needed on this class
 *
 * @author Owen Feehan
 */
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class RegionMap extends AnchorBean<RegionMap> {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private List<RegionMembership> list = new ArrayList<>();
    // END BEAN PROPERTIES

    // Creates a region map with a single entry mapping to a particular region
    public RegionMap(int index) {
        list.add(new RegionMembershipAnd(index));
    }

    public int numRegions() {
        return list.size();
    }

    public RegionMembership membershipForIndex(int index) {
        return list.get(index);
    }

    public RegionMembershipWithFlags membershipWithFlagsForIndex(int index) {
        return new RegionMembershipWithFlags(membershipForIndex(index), index);
    }

    public List<RegionMembershipWithFlags> createListMembershipWithFlags() {
        List<RegionMembershipWithFlags> listOut = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            RegionMembership rm = list.get(i);
            listOut.add(new RegionMembershipWithFlags(rm, i));
        }

        return listOut;
    }
}
