package org.anchoranalysis.feature.nrg;

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

import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.params.KeyValueParams;
import org.anchoranalysis.image.chnl.Chnl;
import org.anchoranalysis.image.extent.ImageDim;
import org.anchoranalysis.image.stack.Stack;


// An NRG stack with associated parameters
public class NRGStackWithParams {

	private NRGStack nrgStack;
	private KeyValueParams params;

	public NRGStackWithParams( Chnl chnl ) throws CreateException {
		super();
		this.nrgStack = new NRGStack(chnl);
		this.params = new KeyValueParams();
	}
	
	public NRGStackWithParams( NRGStack nrgStack ) throws CreateException {
		super();
		this.nrgStack = nrgStack;
		this.params = new KeyValueParams();
	}
	
	public NRGStackWithParams( NRGStack nrgStack, KeyValueParams params ) {
		super();
		this.nrgStack = nrgStack;
		this.params = params;
	}
	
	public NRGStackWithParams( Stack stackIn, KeyValueParams params ) throws CreateException {
		this.nrgStack = new NRGStack( stackIn );
		this.params = params;
	}
	
	public NRGStackWithParams( Stack stackIn ) throws CreateException {
		this.nrgStack = new NRGStack( stackIn );
		this.params = new KeyValueParams();
	}
	
	public NRGStackWithParams( ImageDim dim ) {
		this.nrgStack = new NRGStack(dim);
		this.params = new KeyValueParams();
	}

	public NRGStackWithParams extractSlice(int z) {
		return new NRGStackWithParams(
			nrgStack.extractSlice(z),
			params
		);
	}
	
	public NRGStack getNrgStack() {
		return nrgStack;
	}

	public KeyValueParams getParams() {
		return params;
	}

	public ImageDim getDimensions() {
		return nrgStack.getDimensions();
	}


	public void setParams(KeyValueParams params) {
		this.params = params;
	}

	public NRGStackWithParams copyChangeParams( KeyValueParams paramsToAssign ) {
		return new NRGStackWithParams(nrgStack, paramsToAssign );
	}
	
	
}
