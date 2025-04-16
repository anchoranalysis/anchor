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

import java.util.Arrays;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.functional.FunctionalList;

/**
 * Maps integer IDs to sub-regions in the map.
 *
 * <p>This class is used to represent a mapping of integer IDs to sub-regions. It is particularly
 * useful in scenarios where object masks need to be compared, such as in
 * org.anchoranalysis.plugin.image.feature.bean.stack.object.AsObjectMask.</p>
 */
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class RegionMap extends AnchorBean<RegionMap> {

    /** 
     * List of region memberships.
     * <p>Each element in this list represents a mapping between an integer ID and a sub-region.</p>
     */
    @BeanField @Getter @Setter private List<RegionMembership> list = Arrays.asList();

    /**
     * Creates a region map with a single entry mapping to a particular region.
     *
     * @param index the index of the region to map
     */
    public RegionMap(int index) {
        list.add(new RegionMembershipAnd(index));
    }

    /**
     * Gets the number of regions in the map.
     *
     * @return the number of regions
     */
    public int numRegions() {
        return list.size();
    }

    /**
     * Gets the region membership for a specific index.
     *
     * @param index the index of the region membership to retrieve
     * @return the RegionMembership at the specified index
     */
    public RegionMembership membershipForIndex(int index) {
        return list.get(index);
    }

    /**
     * Gets the region membership with flags for a specific index.
     *
     * @param index the index of the region membership to retrieve
     * @return a new RegionMembershipWithFlags object for the specified index
     */
    public RegionMembershipWithFlags membershipWithFlagsForIndex(int index) {
        return new RegionMembershipWithFlags(membershipForIndex(index), index);
    }

    /**
     * Creates a list of all region memberships with their corresponding flags.
     *
     * @return a list of RegionMembershipWithFlags objects for all regions in the map
     */
    public List<RegionMembershipWithFlags> createListMembershipWithFlags() {
        return FunctionalList.mapToListWithIndex(list, RegionMembershipWithFlags::new);
    }
}