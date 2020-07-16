package org.anchoranalysis.anchor.mpp.bean.bound;

import java.io.Serializable;
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

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** A bound resolved into pixel units */
@NoArgsConstructor @AllArgsConstructor
public class ResolvedBound extends AnchorBean<ResolvedBound> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// START BEAN PROPERTIES
	@BeanField @Getter @Setter
	private double min = 0.0;
	
	@BeanField @Getter @Setter
	private double max = 1.0;
	// END BEAN PROPERTIES
	
	public ResolvedBound( ResolvedBound src ) {
		this.min = src.min;
		this.max = src.max;
	}

	public boolean contains( double val ) {
		return (val>=min && val <=max);
	}

	public String getDscr() {
		return String.format("resolvedBound(min=%f,max=%f)", getMin(), getMax());
	}
	
	public void scale( double multFactor ) {
		this.min = this.min * multFactor;
		this.max = this.max * multFactor;
	}
	
	/** A random value between the bounds (open interval) */
	public double randOpen( RandomNumberGenerator randomNumberGenerator ) {
		return randomNumberGenerator.sampleDoubleFromRange(min,max);
	}
	
	@Override
	public String toString() {
		return String.format("%f-%f", min, max);
	}
}
