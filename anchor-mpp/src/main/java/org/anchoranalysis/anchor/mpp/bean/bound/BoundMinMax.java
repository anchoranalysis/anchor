package org.anchoranalysis.anchor.mpp.bean.bound;

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


public abstract class BoundMinMax extends Bound {

	/**
	 * 
	 */
	private static final long serialVersionUID = -493791653577617743L;
	
	// START BEAN PROPERTIES
	@BeanField
	private RslvdBound delegate;
	// END BEAN PROPERTIES
	
	public BoundMinMax() {
		delegate = new RslvdBound();
	}
	
	public BoundMinMax( double min, double max ) {
		delegate = new RslvdBound(min,max);
	}
	
	public BoundMinMax( BoundMinMax src ) {
		delegate = new RslvdBound(src.delegate);
	}

	// Getters and Setters
	public double getMin() {
		return delegate.getMin();
	}

	public void setMin(double min) {
		delegate.setMin(min);
	}

	public double getMax() {
		return delegate.getMax();
	}

	public void setMax(double max) {
		delegate.setMax(max);
	}

	@Override
	public String getBeanDscr() {
		return delegate.getDscr();
	}
	
	@Override
	public void scale( double mult_factor ) {
		delegate.scale(mult_factor);
	}

	public RslvdBound getDelegate() {
		return delegate;
	}

	public void setDelegate(RslvdBound delegate) {
		this.delegate = delegate;
	}
}