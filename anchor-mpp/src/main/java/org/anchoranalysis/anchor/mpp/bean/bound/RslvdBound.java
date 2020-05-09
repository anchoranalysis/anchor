package org.anchoranalysis.anchor.mpp.bean.bound;

import org.anchoranalysis.bean.AnchorBean;

/*
 * #%L
 * anchor-mpp
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
import org.anchoranalysis.core.random.RandomNumberGenerator;

// A bound resolved into pixel units
public class RslvdBound extends AnchorBean<RslvdBound> {

	// START BEAN PROPERTIES
	@BeanField
	private double min = 0.0;
	
	@BeanField
	private double max = 1.0;
	// END BEAN PROPERTIES
	
	public RslvdBound() {
		
	}
	
	public RslvdBound(double min, double max) {
		super();
		this.min = min;
		this.max = max;
	}
	
	public RslvdBound( RslvdBound src ) {
		this.min = src.min;
		this.max = src.max;
	}

	public boolean contains( double val ) {
		return (val>=min && val <=max);
	}
	
	// Getters and Setters
	public double getMin() {
		return min;
	}

	public void setMin(double min) {
		this.min = min;
	}

	public double getMax() {
		return max;
	}

	public void setMax(double max) {
		this.max = max;
	}

	public String getDscr() {
		return String.format("rslvdBound(min=%f,max=%f)", getMin(), getMax());
	}
	
	public void scale( double mult_factor ) {
		this.min = this.min * mult_factor;
		this.max = this.max * mult_factor;
	}
	
	public RslvdBound intersect( RslvdBound b ) {
		
		double minNew = Math.max( this.min, b.min );
		double maxNew = Math.min( this.max, b.max );
		
		if (minNew > maxNew) {
			return null;
		}
		
		return new RslvdBound(minNew, maxNew);
	}
	
	public RslvdBound union( RslvdBound b ) {
		double minNew = Math.min( this.min, b.min );
		double maxNew = Math.max( this.max, b.max );
		
		if (minNew > maxNew) {
			return null;
		}
		
		return new RslvdBound(minNew, maxNew);
	}
	
	// A random value between the bounds (open interval)
	public double randOpen( RandomNumberGenerator re ) {
		double diff = max - min;
		return min + (re.nextDouble() * diff );
	}
	
	public double diff() {
		return max - min;
	}
	
	@Override
	public String toString() {
		return String.format("%f-%f", min, max);
	}
}
