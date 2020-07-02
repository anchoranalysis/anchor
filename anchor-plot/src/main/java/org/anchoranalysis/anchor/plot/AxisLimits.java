package org.anchoranalysis.anchor.plot;

import org.anchoranalysis.anchor.plot.AxisLimits;

/*
 * #%L
 * anchor-plot
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


public class AxisLimits {
	
	// Inclusive
	private double axisMin = Double.POSITIVE_INFINITY;
	
	// Inclusive
	private double axisMax = Double.NEGATIVE_INFINITY;

	
	public AxisLimits() {
		
	}
	
	public AxisLimits( double min, double max) {
		this.axisMin = min;
		this.axisMax = max;
	}

	public AxisLimits duplicate() {
		AxisLimits newObj = new AxisLimits();
		newObj.axisMin = this.axisMin;
		newObj.axisMax = this.axisMax;
		return newObj;
	}
	
	public double getAxisMin() {
		return axisMin;
	}

	public void setAxisMin(double axisMin) {
		this.axisMin = axisMin;
	}
	
	public void setAxisMin(int axisMin) {
		this.axisMin = axisMin;
	}

	public double getAxisMax() {
		return axisMax;
	}

	public void setAxisMax(double axisMax) {
		this.axisMax = axisMax;
	}
	
	public void setAxisMax(int axisMax) {
		this.axisMax = axisMax;
	}
	
	public void addIgnoreInfinity( double value ) {
		if (Double.isFinite(value)) {
			add(value);
		}
	}
	
	public void add( double value ) {
		this.axisMax = Math.max( axisMax, value );
		this.axisMin = Math.min( axisMin, value );
	}
}