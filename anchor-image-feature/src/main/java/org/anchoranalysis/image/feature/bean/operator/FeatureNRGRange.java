package org.anchoranalysis.image.feature.bean.operator;

/*-
 * #%L
 * anchor-image-feature
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

import org.anchoranalysis.bean.annotation.BeanField;

public abstract class FeatureNRGRange extends FeatureSingleElemWithRes {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// START BEAN PROPERTIES
	@BeanField
	private double acceptNRG = 0;
	
	@BeanField
	private double rejectNRG = -10;
	// END BEAN PROPERTIES
	
	protected double multiplexNRG( boolean accept ) {
		if (accept) {
			return acceptNRG;
		} else {
			return rejectNRG;
		}
	}
	
	protected String paramDscrNRG() {
		return String.format("acceptNRG=%8.3f rejectNRG=%8.3f", acceptNRG, rejectNRG);
	}
	
	public double getAcceptNRG() {
		return acceptNRG;
	}

	public void setAcceptNRG(double acceptNRG) {
		this.acceptNRG = acceptNRG;
	}

	public double getRejectNRG() {
		return rejectNRG;
	}

	public void setRejectNRG(double rejectNRG) {
		this.rejectNRG = rejectNRG;
	}
}
