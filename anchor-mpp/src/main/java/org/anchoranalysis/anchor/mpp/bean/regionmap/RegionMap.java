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

import java.util.ArrayList;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.functional.FunctionalList;

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
        return FunctionalList.mapToListWithIndex(
            list,
            RegionMembershipWithFlags::new
        );
    }
}
