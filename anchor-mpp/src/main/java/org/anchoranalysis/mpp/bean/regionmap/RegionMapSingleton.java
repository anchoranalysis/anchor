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
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.mpp.mark.GlobalRegionIdentifiers;

/**
 * A singleton class providing a default RegionMap.
 *
 * <p>This class creates and manages a single instance of {@link RegionMap} with predefined region
 * memberships based on global region identifiers.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RegionMapSingleton {

    /** The singleton instance of RegionMap. */
    private static RegionMap regionMap = null;

    /**
     * Gets the singleton instance of RegionMap.
     *
     * <p>If the instance doesn't exist, it creates one with predefined region memberships. This
     * method is thread-safe.
     *
     * @return the singleton instance of RegionMap
     */
    public static synchronized RegionMap instance() {
        if (regionMap == null) {
            List<RegionMembership> list =
                    Arrays.asList(
                            new RegionMembershipAnd(GlobalRegionIdentifiers.SUBMARK_INSIDE),
                            new RegionMembershipAnd(GlobalRegionIdentifiers.SUBMARK_SHELL),
                            new RegionMembershipAnd(GlobalRegionIdentifiers.SUBMARK_CORE),
                            new RegionMembershipAnd(GlobalRegionIdentifiers.SUBMARK_OUTSIDE),
                            new RegionMembershipAnd(GlobalRegionIdentifiers.SUBMARK_CORE_INNER),
                            new RegionMembershipAnd(GlobalRegionIdentifiers.SUBMARK_SHELL_OUTSIDE));

            regionMap = new RegionMap();
            regionMap.setList(list);
        }
        return regionMap;
    }
}
