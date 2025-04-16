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

import lombok.EqualsAndHashCode;

/**
 * A wrapper for {@link RegionMembership} that includes pre-calculated flags and a region ID.
 *
 * <p>This class combines a {@link RegionMembership} with its calculated flags and an associated
 * region ID.
 */
@EqualsAndHashCode
public class RegionMembershipWithFlags {

    /** The pre-calculated flags for the region membership. */
    private byte flags;

    /** The underlying region membership. */
    private RegionMembership regionMembership;

    /** The ID of the region. */
    private int regionID;

    /**
     * Creates a new instance with the given region membership and region ID.
     *
     * @param regionMembership the underlying {@link RegionMembership}
     * @param regionID the ID of the region
     */
    public RegionMembershipWithFlags(RegionMembership regionMembership, int regionID) {
        super();
        this.regionMembership = regionMembership;
        this.flags = regionMembership.flags();
        this.regionID = regionID;
    }

    /**
     * Checks if the given membership flag is set for this region.
     *
     * @param membership the membership flag to check
     * @return true if the region is a member, false otherwise
     */
    public boolean isMemberFlag(byte membership) {
        return regionMembership.isMemberFlag(membership, flags);
    }

    /**
     * Gets the ID of the region.
     *
     * @return the region ID
     */
    public int getRegionID() {
        return regionID;
    }

    @Override
    public String toString() {
        return Byte.toString(flags);
    }

    /**
     * Gets the underlying region membership.
     *
     * @return the {@link RegionMembership}
     */
    public RegionMembership getRegionMembership() {
        return regionMembership;
    }
}
