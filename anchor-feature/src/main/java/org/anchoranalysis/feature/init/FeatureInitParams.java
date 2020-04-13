package org.anchoranalysis.feature.init;

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


import org.anchoranalysis.bean.init.params.BeanInitParams;
import org.anchoranalysis.core.params.KeyValueParams;
import org.anchoranalysis.feature.nrg.NRGStack;

/**
 * Parameters used to initialise a feature
 * 
 * @author Owen Feehan
 *
 */
public class FeatureInitParams extends BeanInitParams {

	private KeyValueParams keyValueParams;
	private NRGStack nrgStack;
	
	public FeatureInitParams() {
		super();
	}
	
	public FeatureInitParams(KeyValueParams keyValueParams) {
		super();
		this.keyValueParams = keyValueParams;
	}
		
	protected FeatureInitParams( FeatureInitParams src ) {
		this.keyValueParams = src.keyValueParams;
		this.nrgStack = src.nrgStack;
	}
	
	
	// Shallow-copy
	public FeatureInitParams duplicate() {
		FeatureInitParams out = new FeatureInitParams();
		out.nrgStack = nrgStack;
		out.keyValueParams = keyValueParams;
		return out;
	}
	
	public KeyValueParams getKeyValueParams() {
		return keyValueParams;
	}

	public void setKeyValueParams(KeyValueParams keyValueParams) {
		this.keyValueParams = keyValueParams;
	}

	public NRGStack getNrgStack() {
		return nrgStack;
	}

	public void setNrgStack(NRGStack nrgStack) {
		this.nrgStack = nrgStack;
	}
}
