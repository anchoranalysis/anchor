package org.anchoranalysis.feature.input.descriptor;

/*
 * #%L
 * anchor-feature
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


/** Defines the type of inputs the feature accepts */
public abstract class FeatureInputDescriptor {

	/**
	 * Are these parameters compatible with everything else?
	 * 
	 * @return
	 */
	public abstract boolean isCompatibleWithEverything();
	
	/**
	 * Rules for preferring to keep one dscr over another.
	 * 
	 * @param dscr
	 * @return the favoured descriptor of the two, or NULL if there is no favourite
	 */
	// TODO remove
	public FeatureInputDescriptor preferTo( FeatureInputDescriptor dscr ) {
		return null;
	}
	
	// TODO remove
	public FeatureInputDescriptor preferToBidirectional( FeatureInputDescriptor dscr ) {
		// If the first try returns NULL, we try to get a preference in the other direction
		FeatureInputDescriptor first = preferTo(dscr);
		if(first!=null) {
			return first;
		}
		return dscr.preferTo(this);
	}
}
