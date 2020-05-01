package org.anchoranalysis.image.feature.stack;

import java.util.Optional;

import org.anchoranalysis.feature.input.FeatureInputNRGStack;

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

import org.anchoranalysis.feature.nrg.NRGStack;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;
import org.anchoranalysis.image.feature.objmask.FeatureInputSingleObj;
import org.anchoranalysis.image.init.ImageInitParams;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class FeatureInputStack extends FeatureInputNRGStack {
	
	private Optional<ImageInitParams> so;

	
	/**
	 * Should only be used if it's guaranteed the NRG stack will be added later, as this this required.
	 */
	public FeatureInputStack() {
		this(null);
	}
	
	/**
	 * Params without any ImageInitParams
	 * 
	 * @param nrgStack
	 */
	public FeatureInputStack(NRGStack nrgStack) {
		this.so = Optional.empty();
		this.setNrgStack( new NRGStackWithParams(nrgStack) );
	}

	/**
	 * Params with any ImageInitParams
	 * 
	 * @param nrgStack
	 * @param so
	 */
	public FeatureInputStack(NRGStack nrgStack, ImageInitParams so) {
		super();
		this.so = Optional.of(so);
		this.setNrgStack( new NRGStackWithParams(nrgStack) );
	}

	public Optional<ImageInitParams> getSharedObjs() {
		return so;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) { return false; }
		if (obj == this) { return true; }
		if (obj.getClass() != getClass()) {
			return false;
		}
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}
}
