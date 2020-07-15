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


import java.util.ArrayList;
import java.util.List;

import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.bean.annotation.BeanField;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

// Maps integer IDs to sub-regions in the map
public class RegionMap extends AnchorBean<RegionMap> {

	// START BEAN PROPERTIES
	@BeanField
	private List<RegionMembership> list = new ArrayList<>();
	// END BEAN PROPERTIES
	
	// Creates empty region map
	public RegionMap() {
		
	}
	
	// Creates a region map with a single entry mapping to a particular region
	public RegionMap( int index ) {
		list.add( new RegionMembershipAnd(index) );
	}
	
	public List<RegionMembership> getList() {
		return list;
	}

	public void setList(List<RegionMembership> list) {
		this.list = list;
	}
	
	public int numRegions() {
		return list.size();
	}

	public RegionMembership membershipForIndex( int index ) {
		return list.get(index);
	}
	
	public RegionMembershipWithFlags membershipWithFlagsForIndex( int index ) {
		return new RegionMembershipWithFlags( membershipForIndex(index), index );
	}
	
	public List<RegionMembershipWithFlags> createListMembershipWithFlags() {
		List<RegionMembershipWithFlags> listOut = new ArrayList<>();
		
		for( int i=0; i<list.size(); i++) {
			RegionMembership rm = list.get(i);
			listOut.add( new RegionMembershipWithFlags(rm,i) );
		}
		
		return listOut;
	}

	// See {@link org.anchoranalysis.plugin.image.feature.bean.stack.object.AsObjectMask} for an example of where equals is needed on this class
	@Override
	public boolean equals(Object obj) {
		if (obj == null) { return false; }
		if (obj == this) { return true; }
		if (obj.getClass() != getClass()) {
			return false;
		}
		RegionMap rhs = (RegionMap) obj;
		return new EqualsBuilder()
             .appendSuper(super.equals(obj))
             .append(list, rhs.list)
             .isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(list)
			.toHashCode();
	}
	
	
}
