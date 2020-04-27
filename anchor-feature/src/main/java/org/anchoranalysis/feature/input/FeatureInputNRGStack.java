package org.anchoranalysis.feature.input;

import java.util.Optional;

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
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;
import org.anchoranalysis.image.extent.ImageDim;
import org.anchoranalysis.image.extent.ImageRes;

public abstract class FeatureInputNRGStack extends FeatureInputParams {

	private Optional<NRGStackWithParams> nrgStack;
	
	public FeatureInputNRGStack() {
		this.nrgStack = Optional.empty();
	}
	
	public FeatureInputNRGStack(Optional<NRGStackWithParams> nrgStack) {
		super();
		this.nrgStack = nrgStack;
	}

	@Override
	public Optional<ImageRes> getResOptional() {
		return nrgStack
			.map( NRGStackWithParams::getDimensions )
			.map( ImageDim::getRes );
	}

	@Override
	public Optional<KeyValueParams> getParamsOptional() {
		return nrgStack.map( NRGStackWithParams::getParams );
	}

	public ImageDim getDimensionsRequired() throws FeatureCalcException {
		return getDimensionsOptional().orElseThrow(
			() -> new FeatureCalcException("Dimensions are required in the input for this operation")
		);
	}
	
	public Optional<ImageDim> getDimensionsOptional() {
		return nrgStack.map( NRGStackWithParams::getDimensions );
	}
	
	/** 
	 * Returns the nrg-stack or throws an exception if it's not set as it's required.
	 *  
	 * @throws FeatureCalcException if the nrg-stack isn't present
	 * */
	public NRGStackWithParams getNrgStackRequired() throws FeatureCalcException {
		return nrgStack.orElseThrow(
			() -> new FeatureCalcException("An NRG-stack is required in the input for this operation")	
		);
	}
	
	/**
	 * Returns the nrg-stack which may or not be present
	 */
	public Optional<NRGStackWithParams> getNrgStackOptional() {
		return nrgStack;
	}

	public void setNrgStack(NRGStackWithParams nrgStack) {
		this.nrgStack = Optional.of(nrgStack);
	}
	
	public void setNrgStack(Optional<NRGStackWithParams> nrgStack) {
		this.nrgStack = nrgStack;
	}
	

	@Override
	public boolean equals(Object obj) {
		if (this == obj) { return true; }
		if (!super.equals(obj)) { return false; }
		if (!(obj instanceof FeatureInputNRGStack)) { return false; }
		
		FeatureInputNRGStack objCast = (FeatureInputNRGStack) obj;

		if (!nrgStack.equals(objCast.nrgStack)) {
			return false;
		}	
		
		return true;
	}

	@Override
	public int hashCode() {
		return nrgStack.hashCode();
	}
}