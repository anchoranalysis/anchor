package org.anchoranalysis.anchor.mpp.bean.regionmap;

/*
 * #%L
 * anchor-overlay
 * %%
 * Copyright (C) 2016 ETH Zurich, University of Zurich, Owen Feehan
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
