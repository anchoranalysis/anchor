package org.anchoranalysis.feature.calc;

import java.util.Optional;

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
import org.anchoranalysis.feature.nrg.NRGStackWithParams;

/**
 * Parameters used to initialize a feature before any calculations
 * 
 * @author Owen Feehan
 *
 */
public class FeatureInitParams implements BeanInitParams {

	private Optional<KeyValueParams> keyValueParams;
	private Optional<NRGStack> nrgStack;
	
	public FeatureInitParams() {
		this( Optional.empty() );
	}
	
	public FeatureInitParams(KeyValueParams keyValueParams) {
		this(
			Optional.of(keyValueParams)
		);
	}
	
	public FeatureInitParams(Optional<KeyValueParams> keyValueParams) {
		super();
		this.keyValueParams = keyValueParams;
		this.nrgStack = Optional.empty();
	}
	
	public FeatureInitParams( NRGStackWithParams nrgStack ) {
		this.nrgStack = Optional.of(
			nrgStack.getNrgStack()
		);
		this.keyValueParams = Optional.of(
			nrgStack.getParams()
		);
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
	
	public Optional<KeyValueParams> getKeyValueParams() {
		return keyValueParams;
	}

	public void setKeyValueParams(Optional<KeyValueParams> keyValueParams) {
		this.keyValueParams = keyValueParams;
	}

	public Optional<NRGStack> getNrgStack() {
		return nrgStack;
	}

	public void setNrgStack(Optional<NRGStack> nrgStack) {
		this.nrgStack = nrgStack;
	}
}
