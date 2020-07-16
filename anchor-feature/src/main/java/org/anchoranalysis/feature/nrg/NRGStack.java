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

import org.anchoranalysis.core.error.OperationFailedRuntimeException;
import org.anchoranalysis.image.channel.Channel;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.image.stack.Stack;

//
// A stack used to supply the NRG in a marked point processes routine
//
// We require all channels have the same size
//
// If the NRGStack is empty, then dimensions can be set
public class NRGStack {

	// Always one of the following two variables should be non-null and the other should be null.
	private final Stack delegate;
	private final ImageDimensions dimensions;
	
	public NRGStack( ImageDimensions dimensions ) {
		this.dimensions = dimensions;
		this.delegate = null;
	}
	
	public NRGStack( Channel chnl ) {
		super();
		this.dimensions = null;
		this.delegate = new Stack(chnl);
	}
	
	public NRGStack( Stack stackIn ) {
		this.dimensions = null;
		this.delegate = stackIn;
	}

	public final int getNumChnl() {
		if (delegate==null) {
			return 0;
		}
		
		return delegate.getNumChnl();
	}

	public ImageDimensions getDimensions() {
		if (delegate==null) {
			return dimensions;
		}
		
		return delegate.getDimensions();
	}
	
	private void throwInvalidIndexException( int numChnls, int index ) {
		throw new OperationFailedRuntimeException(
			String.format("There are %d channels in the nrg-stack. Cannot access index %d.", numChnls, index)	
		);
	}

	public final Channel getChnl(int index) {
		
		if (delegate==null) {
			throwInvalidIndexException( 0, index );
		}
		
		if (index>=delegate.getNumChnl()) {
			throwInvalidIndexException( delegate.getNumChnl(), index );
		}
		
		return delegate.getChnl(index);
	}

	public Stack asStack() {
		if (delegate==null) {
			return new Stack();
		}
		return delegate;
	}

	public NRGStack extractSlice(int z) {
		return new NRGStack(
			delegate.extractSlice(z)
		);
	}
}
