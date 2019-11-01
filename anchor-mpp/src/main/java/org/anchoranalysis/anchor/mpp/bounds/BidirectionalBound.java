package org.anchoranalysis.anchor.mpp.bounds;

/*-
 * #%L
 * anchor-mpp
 * %%
 * Copyright (C) 2010 - 2019 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann la Roche
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

import org.anchoranalysis.image.extent.ImageDim;

public class BidirectionalBound {

	private RslvdBound forward;
	private RslvdBound reverse;
	
	public RslvdBound getForward() {
		return forward;
	}
	public void setForward(RslvdBound forward) {
		this.forward = forward;
	}
	public RslvdBound getReverse() {
		return reverse;
	}
	public void setReverse(RslvdBound reverse) {
		this.reverse = reverse;
	}
	
	// We get the lowest of forward or reverse
	public RslvdBound calcMinimum() {
		
		if (forward==null) {
			if (reverse==null) {
				return null;
			} else {
				return reverse;
			}
		}
		
		if (reverse==null) {
			return forward;
		}
		
		if (reverse.getMin() < forward.getMin()) {
			return reverse;
		} else {
			return forward;
		}
	}
	
	public RslvdBound calcIntersection() {
		if (forward==null) {
			if (reverse==null) {
				return null;
			} else {
				return reverse;
			}
		}
		
		if (reverse==null) {
			return forward;
		}
		
		// If this returns null, it means there is no intersection between the two bounds
		return forward.intersect(reverse);
	}
	
	
	public RslvdBound calcUnion() {
		if (forward==null) {
			if (reverse==null) {
				return null;
			} else {
				return reverse;
			}
		}
		
		if (reverse==null) {
			return forward;
		}
		
		// If this returns null, it means there is no intersection between the two bounds
		return forward.union(reverse);
	}
	
	public double getMaxOfMax() {
		if (forward!=null && reverse!=null) {
			return Math.max(forward.getMax(), reverse.getMax());
		}
		
		if (forward!=null) {
			return forward.getMax();
		}
		
		if (reverse!=null) {
			return reverse.getMax();
		}
		
		return Double.NaN;
	}
	
	
	public double getMinOfMax() {
		if (forward!=null && reverse!=null) {
			return Math.min(forward.getMax(), reverse.getMax());
		}
		
		if (forward!=null) {
			return forward.getMax();
		}
		
		if (reverse!=null) {
			return reverse.getMax();
		}
		
		return Double.NaN;
	}
	
	public double getMaxOfMin() {
		if (forward!=null && reverse!=null) {
			return Math.max(forward.getMin(), reverse.getMin());
		}
		
		if (forward!=null) {
			return forward.getMax();
		}
		
		if (reverse!=null) {
			return reverse.getMax();
		}
		
		return Double.NaN;
	}
	
	public double getMinOfMin() {
		if (forward!=null && reverse!=null) {
			return Math.min(forward.getMin(), reverse.getMin());
		}
		
		if (forward!=null) {
			return forward.getMax();
		}
		
		if (reverse!=null) {
			return reverse.getMax();
		}
		
		return Double.NaN;
	}
	
	@Override
	public String toString() {
		return String.format("[%s and %s]", forward, reverse);
	}
	
	public boolean isUnboundedAtBothEnds() {
		return getForward()==null && getReverse()==null;
	}
	
	public boolean isUnbounded() {
		return getForward()==null || getReverse()==null;
	}
	
	public boolean isUnboundedOrOutsideRange( double range ) {
		if (isUnbounded()) {
			return true;
		}
		
		return getMaxOfMax()>range;
	}
	
	public boolean isUnboundedOrOutsideRangeAtBothEnds( double range ) {
		
		boolean forwardUnbounded = getForward()==null || getForward().getMax()>range;
		boolean reverseUnbounded = getReverse()==null || getReverse().getMax()>range;
		return forwardUnbounded && reverseUnbounded;
	}
	
	public double ratioBounds( ImageDim sd ) {
		
		if (getForward()==null || getReverse()==null) {
			return -1;
		}
		
		double fwd = getForward().getMax();
		double rvrs = getReverse().getMax();
		
		double maxBoth, minBoth;
		if (fwd >= rvrs) {
			maxBoth = fwd;
			minBoth = rvrs;
		} else {
			maxBoth = rvrs;
			minBoth = fwd;
		}
		
		return maxBoth / minBoth;
	}
	
	// Checks if the forward-direction has the smallest maximum, unbounded max are treated as infinitey
	public boolean hasForwardDirectionSmallestMax() {
		
		// Should never be called if no max is defined
		if (getForward()==null && getReverse()==null) {
			assert false;
			return true;
		}
		
		if (getForward()==null) {
			return false;
		}
		
		if (getReverse()==null) {
			return true;
		}
		
		return (getForward().getMax() < getReverse().getMax());
	}
}
