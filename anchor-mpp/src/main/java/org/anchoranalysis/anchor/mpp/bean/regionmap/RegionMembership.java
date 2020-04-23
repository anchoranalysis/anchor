package org.anchoranalysis.anchor.mpp.bean.regionmap;

import org.anchoranalysis.bean.AnchorBean;

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


import org.anchoranalysis.bean.annotation.BeanField;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public abstract class RegionMembership extends AnchorBean<RegionMembership> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	// START BEAN PROPERTIES
	@BeanField
	private boolean bit0 = false;
	
	@BeanField
	private boolean bit1 = false;
	
	@BeanField
	private boolean bit2 = false;
	
	@BeanField
	private boolean bit3 = false;
	
	@BeanField
	private boolean bit4 = false;
	
	@BeanField
	private boolean bit5 = false;
	
	@BeanField
	private boolean bit6 = false;
	
	@BeanField
	private boolean bit7 = false;
	// END BEAN PROPERTIES
	
	public RegionMembership() {
		
	}
	
	private static String bitAsString( boolean bit ) {
		if (bit) {
			return "1";
		} else {
			return "0";
		}
	}
	
	@Override
	public String toString() {
		return String.format(
			"%s%s%s%s%s%s%s%s",
			bitAsString(bit0),
			bitAsString(bit1),
			bitAsString(bit2),
			bitAsString(bit3),
			bitAsString(bit4),
			bitAsString(bit5),
			bitAsString(bit6),
			bitAsString(bit7)
		);
				
	}
	
	public RegionMembership( int index ) {
		switch( index ) {
		case 0:
			bit0 = true;
			break;
		case 1:
			bit1 = true;
			break;
		case 2:
			bit2 = true;
			break;
		case 3:
			bit3 = true;
			break;
		case 4:
			bit4 = true;
			break;
		case 5:
			bit5 = true;
			break;
		case 6:
			bit6 = true;
			break;
		case 7:
			bit7 = true;
			break;			
		}
	}
	
	public byte flags() {
		byte membership = 0;
		
		if (bit0) {
			membership = RegionMembershipUtilities.setAsMemberFlag( membership, RegionMembershipUtilities.flagForRegion(0) );
		}
		if (bit1) {
			membership = RegionMembershipUtilities.setAsMemberFlag( membership, RegionMembershipUtilities.flagForRegion(1) );
		}
		if (bit2) {
			membership = RegionMembershipUtilities.setAsMemberFlag( membership, RegionMembershipUtilities.flagForRegion(2) );
		}
		if (bit3) {
			membership = RegionMembershipUtilities.setAsMemberFlag( membership, RegionMembershipUtilities.flagForRegion(3) );
		}		
		if (bit4) {
			membership = RegionMembershipUtilities.setAsMemberFlag( membership, RegionMembershipUtilities.flagForRegion(4) );
		}
		if (bit5) {
			membership = RegionMembershipUtilities.setAsMemberFlag( membership, RegionMembershipUtilities.flagForRegion(5) );
		}
		if (bit6) {
			membership = RegionMembershipUtilities.setAsMemberFlag( membership, RegionMembershipUtilities.flagForRegion(6) );
		}
		if (bit7) {
			membership = RegionMembershipUtilities.setAsMemberFlag( membership, RegionMembershipUtilities.flagForRegion(7) );
		}
		return membership;
	}
	
	public boolean isBit0() {
		return bit0;
	}
	public void setBit0(boolean bit0) {
		this.bit0 = bit0;
	}
	public boolean isBit1() {
		return bit1;
	}
	public void setBit1(boolean bit1) {
		this.bit1 = bit1;
	}
	public boolean isBit2() {
		return bit2;
	}
	public void setBit2(boolean bit2) {
		this.bit2 = bit2;
	}
	public boolean isBit3() {
		return bit3;
	}
	public void setBit3(boolean bit3) {
		this.bit3 = bit3;
	}
	public boolean isBit4() {
		return bit4;
	}
	public void setBit4(boolean bit4) {
		this.bit4 = bit4;
	}
	public boolean isBit5() {
		return bit5;
	}
	public void setBit5(boolean bit5) {
		this.bit5 = bit5;
	}
	public boolean isBit6() {
		return bit6;
	}
	public void setBit6(boolean bit6) {
		this.bit6 = bit6;
	}
	public boolean isBit7() {
		return bit7;
	}
	public void setBit7(boolean bit7) {
		this.bit7 = bit7;
	}

	public abstract boolean isMemberFlag( byte membership, byte flag );
	
	// See {@link ch.ethz.biol.cell.mpp.nrg.feature.ind.AsObjMask} for an example of where equals is needed on this class
	@Override
	public boolean equals(Object obj) {
		if (obj == null) { return false; }
		if (obj == this) { return true; }
		if (obj.getClass() != getClass()) {
			return false;
		}
		RegionMembership rhs = (RegionMembership) obj;
		return new EqualsBuilder()
             .appendSuper(super.equals(obj))
             .append(bit0, rhs.bit0)
             .append(bit1, rhs.bit1)
             .append(bit2, rhs.bit2)
             .append(bit3, rhs.bit3)
             .append(bit4, rhs.bit4)
             .append(bit5, rhs.bit5)
             .append(bit6, rhs.bit6)
             .append(bit7, rhs.bit7)
             .isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(bit0)
			.append(bit1)
			.append(bit2)
			.append(bit3)
			.append(bit4)
			.append(bit5)
			.append(bit6)
			.append(bit7)
			.toHashCode();
	}
}
