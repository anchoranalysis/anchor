package org.anchoranalysis.feature.input;

/*-
 * #%L
 * anchor-feature
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

import org.anchoranalysis.core.params.KeyValueParams;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;
import org.anchoranalysis.image.extent.ImageDim;
import org.anchoranalysis.image.extent.ImageRes;

public abstract class FeatureInputNRGStack extends FeatureInputParams {

	private NRGStackWithParams nrgStack;
	
	public FeatureInputNRGStack() {
	}
	
	public FeatureInputNRGStack(NRGStackWithParams nrgStack) {
		super();
		this.nrgStack = nrgStack;
	}

	@Override
	public ImageRes getRes() {
		if (nrgStack==null) {
			return null;
		}
		
		ImageDim sd = nrgStack.getDimensions();
		if (sd!=null) {
			return sd.getRes();
		} else {
			return null;
		}
	}

	@Override
	public KeyValueParams getKeyValueParams() {
		return nrgStack.getParams();
	}

	public ImageDim getDimensions() {
		return nrgStack.getDimensions();
	}
	
	public NRGStackWithParams getNrgStack() {
		return nrgStack;
	}

	public void setNrgStack(NRGStackWithParams nrgStack) {
		this.nrgStack = nrgStack;
	}
	

	@Override
	public boolean equals(Object obj) {
		if (this == obj) { return true; }
		if (!super.equals(obj)) { return false; }
		if (!(obj instanceof FeatureInputNRGStack)) { return false; }
		
		FeatureInputNRGStack objCast = (FeatureInputNRGStack) obj;

		if (nrgStack!=null) {
			if (!nrgStack.equals(objCast.nrgStack)) {
				return false;
			}	
		} else {
			if (objCast.nrgStack!=null) {
				return false;
			}
		}
		
		return true;
	}

	@Override
	public int hashCode() {
		if (nrgStack!=null) {
			return nrgStack.hashCode();
		} else {
			// Arbitrary
			return 7;
		}
	}
}
